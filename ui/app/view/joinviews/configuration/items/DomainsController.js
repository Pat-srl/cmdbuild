Ext.define('CMDBuildUI.view.joinviews.configuration.items.DomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-domains',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            classaliaschange: 'onClassAliasChange'
        },
        '#domainstree': {
            beforeitemdblclick: 'onBeforeItemDblClick',
            itemexpand: 'onItemExpand',
            beforeedit: 'onBeforeEdit',
            edit: 'onEdit',
            beforecheckchange: 'onBeforeCheckChange',
            checkchange: 'onCheckChange'
        }
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input
     * @param {String} newValue
     * @param {String} oldValue
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        var store = this.getView().getViewModel().get('theView').joinWith();

        if (store) {
            Ext.Array.forEach(store.getRange(), function (item, index, allitems) {
                if (item.get('source') === oldValue) {
                    item.set('source', newValue);
                }
            });
        }
    },

    onAfterRender: function (view) {
        CMDBuildUI.util.Utilities.showLoader(true, view.down('#domainstree'));
    },

    /**
     * @event #joinTreeStore.rootchange (defined in viewmodel)
     *
     * @param {Ext.data.TreeModel} newRoot
     */
    onTreeStoreRootChange: function (newRoot) {
        const me = this;
        Ext.asap(
            function (_newRoot) {
                if (_newRoot && _newRoot.get('text')) {
                    _newRoot.expand();
                    me.onItemExpand(_newRoot);
                }
            },
            this,
            [newRoot]
        );
    },

    /**
     * @event #domainstree.beforeitemdblclick
     */
    onBeforeItemDblClick: function () {
        return false;
    },

    /**
     * @event #domainstree.itemexpand
     *
     * @param {Ext.data.TreeModel} node
     */
    onItemExpand: function (node) {
        Ext.suspendLayouts();
        const me = this;
        const vm = me.view.getViewModel();
        const targetClass = node.get('targetType') || node.get('targetClass') || vm.get('theView.masterClass');

        if (targetClass) {
            Ext.Array.forEach(node.childNodes, function (child, index, allchilds) {
                me.setPropertiesForUncheckedNode(child);
            });
            node.removeAll();
            const targetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass);
            const targetHierarchy = targetObject.getHierarchy();
            targetObject.getDomains().then(function (domains) {
                const domainsArray = domains.getRange();
                if (domainsArray.length) {
                    domainsArray.forEach(function (domain) {
                        if (domain.get('source') === domain.get('destination')) {
                            me.addDoubleDirectionNodes(node, domain);
                        } else {
                            me.addNodeIfNotExist(
                                node,
                                targetHierarchy,
                                domain,
                                Ext.Array.contains(targetHierarchy, domain.get('source')),
                                false
                            );
                        }
                    });
                } else {
                    CMDBuildUI.util.Utilities.showLoader(false, me.view.down('#domainstree'));
                    vm.getParent().set('stepNavigationLocked', false);
                }
            });
        }

        Ext.resumeLayouts();
    },

    /**
     * @event #domainstree.beforeedit
     *
     * @param {*} editor
     * @param {*} context
     * @param {*} eOpts
     */
    onBeforeEdit: function (editor, context, eOpts) {
        if (context.field === 'targetType') {
            const record = context.record;
            const store = context.column.getEditor().getStore();
            const childrensClasses = [];
            const targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(record.get('targetClass'));

            childrensClasses.push({
                label: targetClass.getTranslatedDescription(),
                value: targetClass.get('name')
            });
            Ext.Array.forEach(targetClass.getChildren(), function (childClass) {
                childrensClasses.push({
                    label: childClass.getTranslatedDescription(),
                    value: childClass.get('name')
                });
            });

            store.beginUpdate();
            store.setData(childrensClasses);
            store.endUpdate();
        }
    },
    /**
     * @event #domainstree.edit
     *
     * @param {*} editor
     * @param {*} context
     * @param {*} eOpts
     */
    onEdit: function (editor, context, eOpts) {
        const me = this;
        const mainView = me.getView().up('joinviews-configuration-main');
        const vm = mainView.getViewModel();
        const checkedStore = vm.get('theView').joinWith();
        const record = context.record;
        const storedRecord = checkedStore.findRecord('domainAlias', record.get('domainAlias'));
        let value;

        if (context.originalValue !== context.value) {
            switch (context.field) {
                case 'domainAlias':
                case 'targetAlias':
                    const lastAliasIndex = mainView.getNewAliasIndex(
                        mainView.aliasType.klass,
                        record.get(context.field)
                    );
                    value = Ext.String.format(
                        '{0}{1}',
                        context.value,
                        lastAliasIndex ? Ext.String.format('_{0}', lastAliasIndex) : ''
                    );
                    mainView.clearAliasIndex(mainView.aliasType.klass, context.originalValue);
                    if (storedRecord) {
                        storedRecord.set(context.field, value);
                    }
                    record.set(context.field, value);
                    break;
                case 'targetType':
                    mainView.clearAliasIndex(mainView.aliasType.klass, record.get('targetAlias'));
                    const lastTargetClassAliasIndex = mainView.getNewAliasIndex(
                        mainView.aliasType.klass,
                        record.get(context.field)
                    );
                    value = Ext.String.format(
                        '{0}{1}',
                        record.get('targetType'),
                        lastTargetClassAliasIndex ? Ext.String.format('_{0}', lastTargetClassAliasIndex) : ''
                    );
                    storedRecord.set('targetAlias', value);
                    storedRecord.set(context.field, context.value);
                    record.set('targetAlias', value);

                    if (record.get(context.field) !== record.get('targetClass')) {
                        if (
                            !record.nextSibling ||
                            (record.nextSibling && record.nextSibling.get('text') !== record.get('text'))
                        ) {
                            const text = record.get('text');
                            const targetClass = record.get('targetClass');
                            const domain = record.get('domain');
                            const direction = record.get('direction');
                            const parentNode = record.parentNode;
                            const cleanNode = me.getCleanNode(text, targetClass, domain, direction, parentNode);

                            record.parentNode.insertBefore(cleanNode, record.nextSibling);
                        }
                    }
                    record.collapse();
                    record.expand();
                    break;
                case 'joinType':
                    if (storedRecord) {
                        storedRecord.set('joinType', record.get('joinType'));
                    }
                    record.set(context.field, context.value);
                    break;
                default:
                    // do nothing
                    break;
            }
            mainView.fireEventArgs('domainchange', [record, context]);
        }
    },

    /**
     * @event #domainstree.beforecheckchange
     */
    onBeforeCheckChange: function () {
        return !this.getView().lookupViewModel().get('actions.view');
    },

    /**
     * @event #domainstree.checkchange
     *
     * @param {Ext.data.TreeModel} node
     * @param {Boolean} checked
     */
    onCheckChange: function (node, checked) {
        var me = this;
        if (checked) {
            me.setPropertiesForCheckedNode(node);
        } else {
            me.setPropertiesForUncheckedNode(node);
        }
    },
    privates: {
        /**
         * @private
         *
         * @param {CMDBuildUI.model.views.JoinViewJoin[]} join
         * @param {Ext.data.TreeModel} node
         * @param {CMDBuildUI.model.domains.Domain} domain
         * @param {String} [direction="direct|inverse"]
         */
        isCheckedItem: function (join, node, domain, direction) {
            var checkedItem;
            join.findBy(function (item) {
                var sameSource;
                if (node.isModel) {
                    sameSource = item.get('source') === node.get('targetAlias');
                }
                if (item.get('domain') === domain.get('name') && sameSource) {
                    if (direction) {
                        if (direction === item.get('direction')) {
                            checkedItem = item;
                            return true;
                        }
                    } else {
                        checkedItem = item;
                        return true;
                    }
                }
                return false;
            });
            return checkedItem;
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         * @param {CMDBuildUI.model.domains.Domain} domain
         * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} linkedObject
         * @param {Boolean} isDirect
         * @param {CMDBuildUI.model.views.JoinViewJoin} checkedItem
         */
        addNode: function (node, domain, linkedObject, isDirect, checkedItem) {
            var me = this,
                mainView = me.getView().up('joinviews-configuration-main'),
                vm = mainView.lookupViewModel(),
                targetClassAlias,
                domainAlias;
            if (checkedItem) {
                var lastTargetClassAliasIndex = mainView.addAliasFromExisisting(
                    mainView.aliasType.klass,
                    checkedItem.get('targetAlias')
                );
                targetClassAlias = Ext.String.format(
                    '{0}{1}',
                    checkedItem.get('targetAlias'),
                    lastTargetClassAliasIndex ? Ext.String.format('_{0}', lastTargetClassAliasIndex) : ''
                );
                var lastDomainIndex = mainView.addAliasFromExisisting(
                    mainView.aliasType.klass,
                    checkedItem.get('domainAlias')
                );
                domainAlias = Ext.String.format(
                    '{0}{1}',
                    checkedItem.get('domainAlias'),
                    lastDomainIndex ? Ext.String.format('_{0}', lastDomainIndex) : ''
                );
            }

            var text = Ext.String.format(
                '{0} [{1}]',
                isDirect ? domain.getTranslatedDescriptionDirect() : domain.getTranslatedDescriptionInverse(),
                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(
                    isDirect ? domain.get('destination') : domain.get('source')
                )
            );
            var newNode = {
                _id: CMDBuildUI.util.Utilities.generateUUID(),
                text: text,
                targetClass: isDirect ? domain.get('destination') : domain.get('source'),
                targetAlias: !Ext.isEmpty(checkedItem) ? checkedItem.get('targetAlias') : '',
                targetType: !Ext.isEmpty(checkedItem) ? checkedItem.get('targetType') : '',
                joinType: !Ext.isEmpty(checkedItem) ? checkedItem.get('joinType') : '',
                domain: domain.get('name'),
                domainAlias: !Ext.isEmpty(checkedItem) ? checkedItem.get('domainAlias') : '',
                checked: !Ext.isEmpty(checkedItem) ? true : false,
                direction: isDirect ? 'direct' : 'inverse',
                parent: node.get('_id'),
                expanded: !Ext.isEmpty(checkedItem) ? true : false,
                leaf: false
            };
            node.appendChild(newNode);
            if (newNode.checked) {
                mainView.fireEventArgs('domaincheckchange', [node.findChild('_id', newNode._id)]);
            }

            if (newNode.targetType !== '' && newNode.targetClass !== newNode.targetType) {
                var targetClass = isDirect ? domain.get('destination') : domain.get('source'),
                    domainName = domain.get('name'),
                    direction = isDirect ? 'direct' : 'inverse',
                    parentNode = node;

                var nodeExists = vm
                    .get('theView')
                    .joinWith()
                    .queryBy(function (join) {
                        var cond =
                            join.get('domain') === domainName &&
                            domain.get('destinations').indexOf(join.get('targetType')) > -1;
                        return cond;
                    });

                if (nodeExists.length) {
                    nodeExists.each(function (checkedItem) {
                        isDirect = checkedItem.get('direction') === 'direct';
                        linkedObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            checkedItem.get('targetType')
                        );
                        if (!node.findChild('domainAlias', checkedItem.get('domainAlias'))) {
                            me.addNode(node, domain, linkedObject, isDirect, checkedItem);
                        }
                    });
                } else {
                    newNode = me.getCleanNode(text, targetClass, domainName, direction, parentNode);
                    node.appendChild(newNode);
                }
            }
            node.sort();
            me.nodeAppended();
        },

        /**
         * @private
         *
         * disable step navigation buttons until all nodes are inserted on tree panel
         */
        nodeAppended: function () {
            var me = this,
                mainView = me.getView().up('joinviews-configuration-main'),
                vm = mainView.lookupViewModel();
            if (this.lastAppendedNodeTimeout) {
                this.lastAppendedNodeTimeout.cancel();
            }
            this.lastAppendedNodeTimeout = new Ext.util.DelayedTask(function () {
                if (vm && !vm.destroyed && vm.get('currentStep') > 0) {
                    var navTree = mainView.down('#domainstree');
                    navTree.getRootNode().sort();
                    vm.set('stepNavigationLocked', false);
                    CMDBuildUI.util.Utilities.showLoader(false, navTree);
                }
            });
            this.lastAppendedNodeTimeout.delay(500);
        },
        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         * @param {String[]} targetHierarchy
         * @param {CMDBuildUI.model.domains.Domain} domain
         */
        addNodeIfNotExist: function (node, targetHierarchy, domain, isDirect, isOtherDirection) {
            var me = this,
                vm = me.view.getViewModel(),
                join = vm.get('theView').joinWith();

            if (domain.get('active')) {
                var isPresent = node.findChildBy(
                    function (child) {
                        if (child.get('domain') === this.domain.get('name')) {
                            var sameDirection = child.get('direction') === this.direction;
                            return sameDirection;
                        }
                        return false;
                    },
                    {
                        domain: domain,
                        direction: isDirect ? 'direct' : 'inverse'
                    },
                    true
                );
                if (!isPresent) {
                    var linkedObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                        isDirect ? domain.get('destination') : domain.get('source')
                    );
                    if (linkedObject && linkedObject.get('active')) {
                        me.addNode(
                            node,
                            domain,
                            linkedObject,
                            isDirect,
                            me.isCheckedItem(join, node, domain, isDirect ? 'direct' : 'inverse')
                        );
                    }
                }
            }
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} parentNode
         * @param {CMDBuildUI.model.domains.Domain} domain
         */
        addDoubleDirectionNodes: function (parentNode, domain) {
            var me = this,
                vm = me.view.getViewModel(),
                join = vm.get('theView').joinWith();
            if (domain.get('active')) {
                var directNodeIsPresent = parentNode.findChildBy(function (child) {
                    return child.get('domain') === domain.get('name') && child.get('direction') === 'direct';
                }, me);

                var inverseNodeIsPresent = parentNode.findChildBy(function (child) {
                    return child.get('domain') === domain.get('name') && child.get('direction') === 'inverse';
                }, me);
                var linkedObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(domain.get('destination'));
                if (linkedObject && linkedObject.get('active')) {
                    if (!directNodeIsPresent) {
                        me.addNode(
                            parentNode,
                            domain,
                            linkedObject,
                            true,
                            me.isCheckedItem(join, parentNode, domain, 'direct')
                        );
                    }

                    if (!inverseNodeIsPresent) {
                        me.addNode(
                            parentNode,
                            domain,
                            linkedObject,
                            false,
                            me.isCheckedItem(join, parentNode, domain, 'inverse')
                        );
                    }
                }
            }
        },
        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         */
        checkParent: function (node) {
            var me = this,
                mainView = me.getView().up('joinviews-configuration-main'),
                parent = node.parentNode;

            if (parent && !parent.isRoot()) {
                parent.set('checked', true);
                parent.set('targetType', parent.get('targetType'));

                if (Ext.isEmpty(parent.get('domainAlias'))) {
                    var domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(parent.get('domain'));
                    var lastDomainAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.klass, domain.get('name'));
                    parent.set(
                        'domainAlias',
                        Ext.String.format(
                            '{0}{1}',
                            domain.get('name'),
                            lastDomainAliasIndex ? Ext.String.format('_{0}', lastDomainAliasIndex) : ''
                        )
                    );
                }

                // join type
                if (Ext.isEmpty(parent.get('joinType'))) {
                    parent.set('joinType', CMDBuildUI.model.views.JoinViewJoin.jointypes.outer_join);
                }

                // target type
                if (Ext.isEmpty(parent.get('targetType'))) {
                    parent.set('targetType', parent.get('targetClass'));
                }

                // target alias
                if (Ext.isEmpty(parent.get('targetAlias'))) {
                    var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(parent.get('targetClass'));
                    var lastClassAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.klass, klass.get('name'));
                    parent.set(
                        'targetAlias',
                        Ext.String.format(
                            '{0}{1}',
                            klass.get('name'),
                            lastClassAliasIndex ? Ext.String.format('_{0}', lastClassAliasIndex) : ''
                        )
                    );
                }
                me.getView().up('form').fireEventArgs('domaincheckchange', [node]);
                if (parent.parentNode && !parent.parentNode.root) {
                    me.checkParent(parent);
                }
            }
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         */
        uncheckChild: function (node) {
            var me = this,
                view = me.getView(),
                childrens = node.childNodes;

            // notify event for node checkchange to other components
            view.up('form').fireEventArgs('domaincheckchange', [node]);

            Ext.Array.forEach(childrens, function (childNode) {
                me.setPropertiesForUncheckedNode(childNode);
            });
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         */
        setPropertiesForCheckedNode: function (node) {
            var me = this,
                view = me.getView(),
                vm = view.lookupViewModel(),
                mainView = view.up('joinviews-configuration-main'),
                domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(node.get('domain')),
                klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(node.get('targetClass'));

            // target type
            node.set('targetType', node.get('targetClass'));

            // join type
            node.set('joinType', CMDBuildUI.model.views.JoinViewJoin.jointypes.outer_join);

            // domain alias
            var lastDomainAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.klass, domain.get('name'));
            node.set(
                'domainAlias',
                Ext.String.format(
                    '{0}{1}',
                    domain.get('name'),
                    lastDomainAliasIndex ? Ext.String.format('_{0}', lastDomainAliasIndex) : ''
                )
            );

            // target alias
            var lastClassAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.klass, klass.get('name'));
            node.set(
                'targetAlias',
                Ext.String.format(
                    '{0}{1}',
                    klass.get('name'),
                    lastClassAliasIndex ? Ext.String.format('_{0}', lastClassAliasIndex) : ''
                )
            );

            view.up('form').fireEventArgs('domaincheckchange', [node]);
            if (node.parentNode && !node.parentNode.isRoot()) {
                me.checkParent(node);
            }
            var checkedStore = vm.get('theView').joinWith();
            var obj = {
                direction: node.get('direction'),
                source: node.get('source'),
                domain: node.get('domain'),
                domainAlias: node.get('domainAlias'),
                joinType: node.get('joinType'),
                targetAlias: node.get('targetAlias'),
                // targetClass: item.get('targetClass'),
                targetType: node.get('targetType')
            };
            if (node.get('parentId') === 'root') {
                obj.source = vm.get('theView.masterClassAlias');
            } else {
                obj.source = node.parentNode.get('targetAlias');
            }
            if (!checkedStore.findRecord('domainAlias', obj.domainAlias)) {
                checkedStore.add(obj);
            } else {
                CMDBuildUI.util.Logger.log(
                    'possible duplicate in tree node store',
                    CMDBuildUI.util.Logger.levels.debug
                );
            }
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         */
        setPropertiesForUncheckedNode: function (node) {
            var me = this,
                view = me.getView(),
                vm = view.lookupViewModel(),
                mainView = view.up('joinviews-configuration-main'),
                checkedStore = vm.get('theView').joinWith(),
                checkedRecord = checkedStore.findRecord('domainAlias', node.get('domainAlias'));
            // remove checked item from checked store if exist
            if (checkedRecord) {
                checkedStore.remove(checkedRecord);
            }

            // remove used alias from stored object
            mainView.clearAliasIndex(mainView.aliasType.klass, node.get('domainAlias'));
            mainView.clearAliasIndex(mainView.aliasType.klass, node.get('targetAlias'));

            // reset all properties
            node.set('checked', false);
            node.set('source', '');
            node.set('targetType', '');
            node.set('domainAlias', '');
            node.set('targetAlias', '');
            node.set('joinType', '');
            node.set('recursionEnabled', false);

            // notify event for node checkchange to other components
            me.getView().up('form').fireEventArgs('domaincheckchange', [node]);
            // uncheck child if exists
            me.uncheckChild(node, false);
        },

        /**
         * @private
         *
         * @param {String} text
         * @param {String} targetClass
         * @param {String} domain
         * @param {String} direction
         * @param {String} parentNode
         */
        getCleanNode: function (text, targetClass, domain, direction, parentNode, source) {
            return {
                _id: CMDBuildUI.util.Utilities.generateUUID(),
                text: text,
                targetClass: targetClass,
                domain: domain,
                targetType: '', // defined on record
                dimainAlias: '', // defined on record
                joinType: '', // defined on record
                targetAlias: '', // defined on record
                checked: false,
                filter: '{}', //
                direction: direction,
                parent: parentNode.get('_id'),
                expanded: false,
                leaf: false
            };
        }
    }
});
