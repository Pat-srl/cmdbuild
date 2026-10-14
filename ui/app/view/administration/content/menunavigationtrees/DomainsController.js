Ext.define('CMDBuildUI.view.administration.content.menunavigationtrees.DomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menunavigationtrees-domains',

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

    onAfterRender: function (view) {
        var me = this;
        var vm = view.getViewModel();
        vm.bind('{theNavigationtree.nodes}', function () {
            var directionsErrors = vm.get('theNavigationtree').checkDirections();

            if (directionsErrors.length) {
                CMDBuildUI.util.Logger.log(directionsErrors, CMDBuildUI.util.Logger.levels.debug);
                var hasInvalidDomains = directionsErrors.find(function (el) {
                    return el.error === 'notfound';
                });
                var hasInvalidDirections = directionsErrors.find(function (el) {
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

    fixTreeNodesDirection: function (reason) {
        var me = this;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.navigationtrees.texts.fixconfirmmessage,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Utilities.showLoader(true);
                    if (reason === 'notfound') {
                        var saveBtn = me
                            .getView()
                            .up('administration-content-menunavigationtree-view')
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
            },
            this
        );
    },
    /**
     * @event #joinTreeStore.rootchange (defined in viewmodel)
     *
     * @param {Ext.data.TreeModel} newRoot
     */
    onTreeStoreRootChange: function (newRoot) {
        var me = this;
        Ext.asap(
            function (_newRoot) {
                if (_newRoot.get('text')) {
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
        var me = this,
            vm = me.view.getViewModel();
        var targetClass =
            node.get('targetClass') || node.get('domainTargetClass') || vm.get('theNavigationtree.targetClass');
        if (targetClass) {
            var targetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass);
            var targetHierarchy = targetObject.getHierarchy();
            // add server nodes
            vm.get('theNavigationtree')
                .nodes()
                .each(function (serverItem) {
                    try {
                        if (serverItem.get('parent') === node.get('_id')) {
                            var serverItemIsDirect = serverItem.get('direction') === '_2';
                            var domainsStore = Ext.getStore('domains.Domains');
                            var serverItemDomain = domainsStore.findRecord('name', serverItem.get('domain'));
                            serverItem.set(
                                'domainTargetClass',
                                serverItemIsDirect
                                    ? serverItemDomain.get('destination')
                                    : serverItemDomain.get('source')
                            );
                            var serverItemTargetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                serverItem.get('domainTargetClass')
                            );
                            var serverItemTargetHierarchy = serverItemTargetObject.getHierarchy();
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
            targetObject.getDomains(true).then(function (domains) {
                var domainsArray = domains.getRange();
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
            var record = context.record,
                store = context.column.getEditor().getStore(),
                childrensClasses = [];
            var targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
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
        var me = this,
            mainView = me.getView().up('administration-content-menunavigationtree-view'),
            record = context.record;
        if (context.originalValue !== context.value) {
            switch (context.field) {
                case 'targetClass':
                    if (record.get(context.field) !== record.get('domainTargetClass')) {
                        if (!record.nextSibling || record.nextSibling.get('text') !== record.get('text')) {
                            var text = record.get('text'),
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
    onViewModeBtnClick: function (view, rowIndex, colIndex, column, event, record) {
        // open the popip and show the form for set:
        // subclassViewMode
        // subclassViewShowIntermediateNodes
        // subclassFilter
        // subclassViewMode        ;
        var vm = view.lookupViewModel();
        var popupId = 'menunavtree-viewmode-popup';
        var content = {
            xtype: 'administration-content-menunavigationtrees-viewmodeform',
            scrollable: 'y',
            viewModel: {
                data: {
                    record: record,
                    actions: vm.get('actions')
                }
            }
        };
        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup(popupId);
            }
        };
        // create and open panel
        var popup = CMDBuildUI.util.Utilities.openPopup(
            popupId,
            CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.viewmode,
            content,
            listeners,
            {
                ui: 'administration-actionpanel',
                draggable: true
            }
        );

        return popup;
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
            var me = this,
                mainView = me.getView().up('administration-content-menunavigationtree-view');
            var directText = domain.getTranslatedDescriptionDirect() + ' [' + linkedObject.get('description') + ']';
            var inverseText = domain.getTranslatedDescriptionInverse() + ' [' + linkedObject.get('description') + ']';
            var text = isDirect ? directText : inverseText;
            var newNode = {
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
                description: !Ext.isEmpty(checkedItem) ? checkedItem.get('description') : '',
                subclassViewMode: !Ext.isEmpty(checkedItem) ? checkedItem.get('subclassViewMode') : 'cards',
                subclassViewShowIntermediateNodes: !Ext.isEmpty(checkedItem)
                    ? checkedItem.get('subclassViewShowIntermediateNodes')
                    : '',
                subclassFilter: !Ext.isEmpty(checkedItem) ? checkedItem.get('subclassFilter') : ''
            };

            // subclass_<NomeClasse>_description
            if (newNode.subclassFilter && newNode.subclassFilter.length) {
                var subclasses = newNode.subclassFilter.split(',');
                Ext.Array.forEach(subclasses, function (subclass) {
                    newNode[Ext.String.format('subclass_{0}_description', subclass)] =
                        checkedItem[Ext.String.format('subclass_{0}_description', subclass)];
                });

                //TODO: set locale key
                //navtree.<codice>.item.<codice item>.subclass.<NomeClasse>.description;
            }
            parentNode.appendChild(newNode);
            if (newNode.checked) {
                mainView.fireEventArgs('domaincheckchange', [parentNode.findChild('_id', newNode._id)]);
            }
            if (newNode.targetClass != '' && newNode.targetClass !== newNode.domainTargetClass) {
                var domainTargetClass = isDirect ? domain.get('destination') : domain.get('source'),
                    domainName = domain.get('name'),
                    direction = isDirect ? '_2' : '_1',
                    cleanNode = me.getCleanNode(text, domainTargetClass, domainName, direction, parentNode);

                parentNode.appendChild(cleanNode);
            }
            try {
                parentNode.sort(function (a, b) {
                    return a.get('text') === b.get('text') ? 0 : a.get('text') < b.get('text') ? -1 : 1;
                }, true);
            } catch (e) {}
            me.nodeAppended();
        },

        /**
         * @private
         *
         * hook after appended
         */
        nodeAppended: function () {},
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
            var me = this,
                parent = node.parentNode;

            if (parent && !parent.isRoot()) {
                parent.set('checked', true);
                parent.set('targetClass', parent.get('targetClass') || parent.get('domainTargetClass'));
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
                view = me.getView();

            node.set('targetClass', node.get('domainTargetClass'));
            view.up('form').fireEventArgs('domaincheckchange', [node]);

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
            var me = this,
                view = me.getView();

            // reset all properties
            node.set('checked', false);
            node.set('targetClass', '');
            node.set('filter', '');
            node.set('recursionEnabled', false);
            node.set('showOnlyOne', false);
            node.set('description', '');
            node.set('subclassViewMode', 'cards');
            node.set('subclassViewShowIntermediateNodes', '');
            node.set('subclassFilter', '');
            Ext.Array.forEach(Ext.Object.getKeys(node.getData()), function (key) {
                if (Ext.String.startsWith(key, 'subclass_')) {
                    delete node.data[key];
                }
            });

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
        }
    }
});
