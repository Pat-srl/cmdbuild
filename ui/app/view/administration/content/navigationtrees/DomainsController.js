Ext.define('CMDBuildUI.view.administration.content.navigationtrees.DomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-navigationtrees-domains',

    control: {
        '#': {
            afterrender: 'onAfterRender'
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
     *
     * @param {CMDBuildUI.view.administration.content.navigationtrees.Domains} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const me = this;
        const vm = view.getViewModel();
        CMDBuildUI.util.Utilities.showLoader(true, view.down('#domainstree'));

        vm.bind('{theNavigationtree.nodes}', function () {
            const directionsErrors = vm.get('theNavigationtree').checkDirections();

            if (directionsErrors.length) {
                CMDBuildUI.util.Logger.log(directionsErrors, CMDBuildUI.util.Logger.levels.debug);
                const hasInvalidDomains = directionsErrors.find(function (el) {
                    return el.error === 'notfound';
                });
                const hasInvalidDirections = directionsErrors.find(function (el) {
                    return el.error === 'direction';
                });
                if (hasInvalidDomains) {
                    view.up('panel').insert(0, {
                        margin: 10,
                        ui: 'messagewarning',
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                flex: 1,
                                ui: 'custom',
                                xtype: 'panel',
                                html: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.missingelements
                            },
                            {
                                xtype: 'button',
                                ui: 'administration-warning-action-small',
                                text: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.fixtree,
                                listeners: {
                                    click: function () {
                                        me.fixTreeNodesDirection('notfound');
                                    }
                                }
                            }
                        ]
                    });
                }

                if (hasInvalidDirections) {
                    view.up('panel').insert(hasInvalidDomains ? 1 : 0, {
                        margin: 10,
                        ui: 'messagewarning',
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                flex: 1,
                                ui: 'custom',
                                xtype: 'panel',
                                html: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.configissue
                            },
                            {
                                xtype: 'button',
                                ui: 'administration-warning-action-small',
                                text: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.fixtree,
                                listeners: {
                                    click: function () {
                                        me.fixTreeNodesDirection('direction');
                                    }
                                }
                            }
                        ]
                    });
                }
            }
        });
    },

    /**
     *
     * @param {Ext.data.TreeModel} newRoot
     */
    onTreeStoreRootChange: function (newRoot) {
        Ext.asap(function () {
            if (newRoot.get('text')) {
                newRoot.expand();
            }
        });
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
        const targetClass =
            node.get('targetClass') || node.get('domainTargetClass') || vm.get('theNavigationtree.targetClass');
        if (targetClass) {
            const targetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass);
            const targetHierarchy = targetObject.getHierarchy();
            // add server nodes
            vm.get('theNavigationtree')
                .nodes()
                .each(function (serverItem) {
                    try {
                        if (serverItem.get('parent') === node.get('_id')) {
                            const serverItemIsDirect = serverItem.get('direction') === '_2';
                            const domainsStore = Ext.getStore('domains.Domains');
                            const serverItemDomain = domainsStore.findRecord('name', serverItem.get('domain'));
                            serverItem.set(
                                'domainTargetClass',
                                serverItemIsDirect
                                    ? serverItemDomain.get('destination')
                                    : serverItemDomain.get('source')
                            );
                            const serverItemTargetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                serverItem.get('domainTargetClass')
                            );
                            if (!node.findChild('_id', serverItem.get('_id'))) {
                                me.addNode(
                                    node,
                                    serverItemDomain,
                                    serverItemTargetObject,
                                    serverItemIsDirect,
                                    serverItem
                                );
                            }
                        }
                    } catch (error) {
                        CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                    }
                });

            targetObject.getDomains().then(function (domains) {
                const domainsArray = domains.getRange();
                if (domainsArray.length) {
                    const childNodesToRemove = Ext.clone(node.childNodes);
                    domainsArray.forEach(function (domain) {
                        if (domain.get('source') === domain.get('destination')) {
                            me.addDoubleDirectionNodes(node, domain, childNodesToRemove);
                        } else {
                            me.addNodeIfNotExist(node, targetHierarchy, domain, childNodesToRemove);
                        }
                    });
                    Ext.Array.forEach(childNodesToRemove, function (child, index, allchilds) {
                        node.removeChild(child);
                    });
                } else {
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
        if (context.field === 'targetClass') {
            const record = context.record;
            const store = context.column.getEditor().getStore();
            const childrensClasses = [];
            const targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                record.get('domainTargetClass') || record.get('targetClass')
            );
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
            Ext.Array.sort(childrensClasses, function (a, b) {
                return a.label === b.label ? 0 : a.label < b.label ? -1 : 1;
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
        const record = context.record;
        if (context.originalValue !== context.value) {
            switch (context.field) {
                case 'targetClass':
                    if (record.get(context.field) !== record.get('domainTargetClass')) {
                        if (!record.nextSibling || record.nextSibling.get('text') !== record.get('text')) {
                            const text = record.get('text'),
                                targetClass = record.get('domainTargetClass'),
                                domain = record.get('domain'),
                                direction = record.get('direction'),
                                parentNode = record.parentNode,
                                cleanNode = me.getCleanNode(text, targetClass, domain, direction, parentNode);

                            record.parentNode.insertBefore(cleanNode, record.nextSibling);
                        }
                    }
                    record.collapse();
                    record.expand();
                    break;
                default:
                    // do nothing
                    break;
            }
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
        const me = this;
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
         * @param {Ext.data.TreeModel} parentNode
         * @param {CMDBuildUI.model.domains.Domain} domain
         * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} linkedObject
         * @param {Boolean} isDirect
         * @param {CMDBuildUI.model.views.JoinViewJoin} checkedItem
         */
        addNode: function (parentNode, domain, linkedObject, isDirect, checkedItem) {
            const me = this;
            const directText = domain.getTranslatedDescriptionDirect() + ' [' + linkedObject.get('description') + ']';
            const inverseText = domain.getTranslatedDescriptionInverse() + ' [' + linkedObject.get('description') + ']';
            const text = isDirect ? directText : inverseText;
            const newNode = {
                _id: (checkedItem && checkedItem.get('_id')) || CMDBuildUI.util.Utilities.generateUUID(),
                text: text,
                targetClass: checkedItem ? checkedItem.get('targetClass') : '',
                domainTargetClass: isDirect ? domain.get('destination') : domain.get('source'),
                domain: domain.get('name'),
                checked: !Ext.isEmpty(checkedItem) ? true : false,
                direction: isDirect ? '_2' : '_1',
                parent: parentNode.get('_id'),
                expanded: !Ext.isEmpty(checkedItem) ? true : false,
                leaf: false,
                recursionEnabled: !Ext.isEmpty(checkedItem) ? checkedItem.get('recursionEnabled') : false,
                filter: !Ext.isEmpty(checkedItem) ? checkedItem.get('filter') : '',
                showOnlyOne: !Ext.isEmpty(checkedItem) ? checkedItem.get('showOnlyOne') : false,
                subclassViewMode: !Ext.isEmpty(checkedItem) ? checkedItem.get('subclassViewMode') : 'cards'
            };

            parentNode.appendChild(newNode);
            if (newNode.targetClass != '' && newNode.targetClass !== newNode.domainTargetClass) {
                const domainTargetClass = isDirect ? domain.get('destination') : domain.get('source');
                const domainName = domain.get('name');
                const direction = isDirect ? '_2' : '_1';
                const cleanNode = me.getCleanNode(text, domainTargetClass, domainName, direction, parentNode);

                parentNode.appendChild(cleanNode);
            }

            me.nodeAppended();
        },

        /**
         * @private
         *
         * hook after appended
         */
        nodeAppended: function () {
            const me = this;
            const view = me.getView();
            const domainsTree = view.down('#domainstree');

            if (this.lastAppendedNodeTimeout) {
                this.lastAppendedNodeTimeout.cancel();
            }
            this.lastAppendedNodeTimeout = new Ext.util.DelayedTask(function () {
                if (view && !view.destroyed) {
                    domainsTree.getStore().sort();
                    CMDBuildUI.util.Utilities.showLoader(false, domainsTree);
                }
            });
            this.lastAppendedNodeTimeout.delay(1000);
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} parentNode
         * @param {String[]} targetHierarchy
         * @param {CMDBuildUI.model.domains.Domain} domain
         * @param {Array} childNodesToRemove
         */
        addNodeIfNotExist: function (parentNode, targetHierarchy, domain, childNodesToRemove) {
            const me = this;

            if (domain.get('active')) {
                const isDirect = Ext.Array.contains(targetHierarchy, domain.get('source'));
                const nodeIsPresent = parentNode.findChild('domain', domain.get('name'));
                if (!nodeIsPresent) {
                    const linkedObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                        isDirect ? domain.get('destination') : domain.get('source')
                    );
                    if (linkedObject && linkedObject.get('active')) {
                        me.addNode(parentNode, domain, linkedObject, isDirect);
                    }
                } else {
                    Ext.Array.remove(childNodesToRemove, nodeIsPresent);
                }
            }
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} parentNode
         * @param {CMDBuildUI.model.domains.Domain} domain
         * @param {Array} childNodesToRemove
         */
        addDoubleDirectionNodes: function (parentNode, domain, childNodesToRemove) {
            const me = this;

            if (domain.get('active')) {
                const directNodeIsPresent = parentNode.findChildBy(function (child) {
                    return child.get('domain') === domain.get('name') && child.get('direction') === '_1';
                }, me);

                const inverseNodeIsPresent = parentNode.findChildBy(function (child) {
                    return child.get('domain') === domain.get('name') && child.get('direction') === '_2';
                }, me);
                const linkedObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(domain.get('destination'));
                if (linkedObject && linkedObject.get('active')) {
                    if (!directNodeIsPresent) {
                        me.addNode(parentNode, domain, linkedObject, false);
                    } else {
                        Ext.Array.remove(childNodesToRemove, directNodeIsPresent);
                    }

                    if (!inverseNodeIsPresent) {
                        me.addNode(parentNode, domain, linkedObject, true);
                    } else {
                        Ext.Array.remove(childNodesToRemove, inverseNodeIsPresent);
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
            const me = this;
            const parent = node.parentNode;

            if (parent && !parent.isRoot()) {
                parent.set('checked', true);
                parent.set('targetClass', parent.get('targetClass') || parent.get('domainTargetClass'));
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
            const me = this;
            const childrens = node.childNodes;

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
            const me = this;

            node.set('targetClass', node.get('domainTargetClass'));
            if (node.parentNode && !node.parentNode.isRoot()) {
                me.checkParent(node);
            }
        },

        /**
         * @private
         *
         * @param {Ext.data.TreeModel} node
         */
        setPropertiesForUncheckedNode: function (node) {
            const me = this;

            // reset all properties
            node.set('checked', false);
            node.set('targetClass', '');
            node.set('filter', '');
            node.set('recursionEnabled', false);
            node.set('showOnlyOne', false);

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
        getCleanNode: function (text, domainTargetClass, domain, direction, parentNode, source) {
            return {
                _id: CMDBuildUI.util.Utilities.generateUUID(),
                text: text,
                domainTargetClass: domainTargetClass,
                targetClass: '',
                domain: domain,

                checked: false,
                filter: '', //
                direction: direction,
                parent: parentNode.get('_id'),
                expanded: false,
                leaf: false
            };
        },

        /**
         *
         * @param {String} reason
         */
        fixTreeNodesDirection: function (reason) {
            const me = this;
            const vm = me.getViewModel();
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.administration.common.messages.attention,
                CMDBuildUI.locales.Locales.administration.navigationtrees.texts.fixconfirmmessage,
                function (btnText) {
                    if (btnText.toLowerCase() === 'yes') {
                        CMDBuildUI.util.Utilities.showLoader(true);
                        if (reason === 'notfound') {
                            const saveBtn = me
                                .getView()
                                .up('administration-content-navigationtrees-view')
                                .down('#saveBtn');
                            saveBtn.fireEventArgs('click', [saveBtn]);
                            CMDBuildUI.util.Utilities.showLoader(false);
                        } else {
                            vm.get('theNavigationtree')
                                .fixDirections()
                                .then(
                                    function () {
                                        CMDBuildUI.util.Utilities.showLoader(false);
                                        me.redirectTo(Ext.History.getToken(), true);
                                    },
                                    function () {
                                        CMDBuildUI.util.Utilities.showLoader(false);
                                    }
                                );
                        }
                    }
                }
            );
        }
    }
});
