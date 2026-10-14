Ext.define('CMDBuildUI.view.management.navigation.Utils', {
    singleton: true,

    /**
     *
     * @param {*} nodeRecord Menu node
     * @param {Ext.list.TreeItem} treeitem
     */
    navtreeAddFirstLevel: function (nodeRecord, treeitem) {
        const treestore = Ext.create('CMDBuildUI.store.menu.MenuTreeItem', {
            defaultRootId: nodeRecord.get('objecttypename')
        });

        // CMDBuildUI.model.navigationTrees.DomainTree.load(nodeRecord.get("objectTypeName"), {
        treestore.load({
            callback: function (records, operation, success) {
                if (success) {
                    if (!nodeRecord._childrenloaded) {
                        const root = records[0];
                        nodeRecord.set('_navtreedef', root);
                        nodeRecord.set('_targettype', CMDBuildUI.util.helper.ModelHelper.objecttypes.klass);
                        nodeRecord.set('_targettypename', root.get('targetClass'));
                        nodeRecord.set('isNavtreeRoot', true);
                        CMDBuildUI.view.management.navigation.Utils.loadChildren(
                            // nodeRecord
                            treeitem
                        );

                        if (treeitem.getSelected()) {
                            Ext.GlobalEvents.fireEventArgs('menunavtreeitemchanged', [nodeRecord]);
                        }
                    }
                }
            }
        });
    },

    /**
     *
     * @param {Ext.list.TreeItem} treeitem Menu node
     * @param {Ext.list.TreeItem || Ext.data.Model} navTreeNode
     * @returns
     */
    loadChildren: function (treeitem, navTreeNode) {
        const currentnode = treeitem.getNode();
        if (!currentnode) {
            return;
        }
        currentnode._childrenloaded = true;

        const navtreedef = currentnode.get('_navtreedef');
        const targetType = currentnode.get('_targettype');
        const targetTypeName = currentnode.get('_targettypename');

        if (targetType && targetTypeName) {
            // add temp spinner node
            const loadernode = currentnode.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.loader,
                objectdescription: '&nbsp;',
                leaf: true
            });

            // get sorters
            let sorters = [];
            const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetTypeName, targetType);
            if (item) {
                item.defaultOrder()
                    .getRange()
                    .forEach(function (o) {
                        sorters.push({
                            property: o.get('attribute'),
                            direction: o.get('direction') === 'descending' ? 'DESC' : 'ASC'
                        });
                    });
            }

            if (Ext.isEmpty(sorters)) {
                sorters = [
                    {
                        property: 'Description'
                    }
                ];
            }

            // create temp store
            const childrenstore = Ext.create('Ext.data.Store', {
                fields: ['_id', '_type', 'Description'],
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(targetType, targetTypeName),
                    extraParams: {
                        attrs: 'Id,Description,IdClass'
                    }
                },
                advancedFilter: CMDBuildUI.view.management.navigation.Utils.getFilterForNode(
                    currentnode.get('_navtreedef'),
                    currentnode.get('_objectid'),
                    currentnode.get('_objecttype')
                ),
                pageSize: 0,
                remoteSort: true,
                sorters: sorters
            });

            // load store
            childrenstore.load({
                callback: function (records, operation, success) {
                    const subnodes = [];
                    let subclassFilterEmpty;

                    // erase loader
                    loadernode.erase();

                    if (!records || !records.length) {
                        if (treeitem.expanderElement) {
                            treeitem.expanderElement.destroy();
                        }
                        return;
                    }

                    // add subnodes
                    navtreedef.childNodes.forEach(function (childdef) {
                        const isLeaf = !childdef.childNodes.length;
                        const childrentypename = childdef.get('targetClass');
                        const childrentype = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(childrentypename);
                        const nodeLabel = childdef.get('_description_translation')
                            ? childdef.get('_description_translation')
                            : CMDBuildUI.util.helper.ModelHelper.getObjectDescription(childrentypename, childrentype);
                        const basenode = {
                            menutype: CMDBuildUI.model.menu.MenuItem.types.navtreeitem,
                            objecttypename: currentnode.get('objecttypename'),
                            objectdescription: nodeLabel,
                            _navtreedef: childdef,
                            _targettype: childrentype,
                            _targettypename: childrentypename,
                            _label: nodeLabel,
                            leaf: isLeaf
                        };

                        if (childdef.get('subclassViewMode') === 'subclasses') {
                            const subclassFilter = childdef.get('subclassFilter');
                            subclassFilterEmpty = Ext.isEmpty(subclassFilter) ? true : false;
                            subclassFilter.split(',').forEach(function (subtype) {
                                // add config for intermediate node
                                let superclassnode;
                                if (childdef.get('subclassViewShowIntermediateNodes')) {
                                    let label = childdef.get('_description_translation');
                                    if (!label) {
                                        label =
                                            CMDBuildUI.util.helper.ModelHelper.getObjectDescription(childrentypename);
                                    }
                                    superclassnode = {
                                        menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                                        objectdescription: label,
                                        leaf: false
                                    };
                                }

                                // define label
                                let subLabel = childdef.get('_subclass_' + subtype + '_description_translation');
                                if (!subLabel) {
                                    subLabel = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(
                                        subtype,
                                        childrentype
                                    );
                                }

                                // add subnodes
                                subnodes.push(
                                    Ext.applyIf(
                                        {
                                            objectdescription: subLabel,
                                            _targettypename: subtype,
                                            _label: subLabel,
                                            superclassnode: superclassnode
                                        },
                                        basenode
                                    )
                                );
                            });
                        } else {
                            subnodes.push(basenode);
                        }
                    });

                    // add nodes
                    if (subnodes.length > 1) {
                        records.forEach(function (record) {
                            const rnode = currentnode.appendChild({
                                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                                objectdescription: record.get('Description'),
                                leaf: false
                            });
                            subnodes.forEach(function (basenode) {
                                let parentnode = rnode;

                                // get / create superclass node
                                if (basenode.superclassnode) {
                                    parentnode =
                                        rnode.findChild(
                                            'objectdescription',
                                            basenode.superclassnode.objectdescription
                                        ) || rnode.appendChild(basenode.superclassnode);
                                }

                                // append node
                                parentnode.appendChild(
                                    Ext.apply(
                                        {
                                            _objectid: record.get('_id'),
                                            _objecttype: record.get('_type'),
                                            _objectdescription: record.get('Description')
                                        },
                                        basenode
                                    )
                                );
                            });
                        });
                    } else {
                        // append the node
                        const basenode = subnodes[0];
                        if (subclassFilterEmpty) {
                            basenode.menutype = CMDBuildUI.model.menu.MenuItem.types.folder;
                        }
                        records.forEach(function (record) {
                            currentnode.appendChild(
                                Ext.applyIf(
                                    {
                                        objectdescription: record.get('Description'),
                                        _objectid: record.get('_id'),
                                        _objecttype: record.get('_type'),
                                        _objectdescription: record.get('Description')
                                    },
                                    basenode
                                )
                            );
                        });
                    }

                    if (navTreeNode) {
                        const nav = CMDBuildUI.util.Navigation.getManagementNavigation();
                        const navStore = nav.getStore();

                        if (navTreeNode.get('_navtreedef')) {
                            navTreeNode = navStore.findRecord('_id', navTreeNode.get('_id'));
                        } else {
                            const indexRecordSelected = navStore.findBy(function (node) {
                                return (
                                    navTreeNode.getId() == node.get('_objectid') &&
                                    navTreeNode.get('_type') == node.get('_objecttype')
                                );
                            });
                            navTreeNode = navStore.getRange()[indexRecordSelected];
                        }

                        Ext.asap(function () {
                            CMDBuildUI.util.Navigation.getMainContainer()
                                .down('management-navigation-container')
                                .openResourceByNode(navTreeNode);

                            nav.setSelection(navTreeNode);
                        });
                    }
                }
            });
        }
    },

    /**
     *
     * @param {*} nodedef
     * @param {String|Number} recordid
     * @param {String} recordtype
     *
     * @returns {Object} Filter configuration
     */
    getFilterForNode: function (nodedef, recordid, recordtype) {
        const filter = {};
        // add relation filter
        if (nodedef.get('domain')) {
            filter.relation = [
                {
                    domain: nodedef.get('domain'),
                    source: recordtype,
                    destination: nodedef.get('targetClass'),
                    direction: nodedef.get('direction'),
                    type: 'oneof',
                    cards: [
                        {
                            className: recordtype,
                            id: recordid
                        }
                    ]
                }
            ];
        }
        // add cql filter
        if (nodedef.get('ecqlFilter')) {
            filter.ecql = {
                id: nodedef.get('ecqlFilter').id
            };
        }
        // filter by sub-types
        if (nodedef.get('subclassViewMode') === 'cards' && !Ext.isEmpty(nodedef.get('subclassFilter'))) {
            filter.attributes = {
                IdClass: [
                    {
                        operator: 'in',
                        value: nodedef.get('subclassFilter').split(',')
                    }
                ]
            };
        }
        return filter;
    }
});
