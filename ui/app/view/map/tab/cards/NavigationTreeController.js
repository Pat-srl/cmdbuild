//TODO: Handle the loading of new records in the main store
Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-navigationtree',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforeselect: 'onBeforeSelect',
            beforedeselect: 'onBeforeDeselect',
            selectionchange: 'onSelectionChange',
            select: 'onSelect',
            deselect: 'onDeselect',
            itemexpand: 'onItemExpand',
            checkchange: 'onCheckChange',
            beforecellclick: 'onBeforeCellClick'
        }
    },

    listen: {
        global: {
            cardupdated: 'onCardUpdated',
            carddeleted: 'onCardDeleted'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const me = this,
            vm = this.getViewModel();

        vm.bind({
            gisNavigation: '{gisNavigation}',
            store: '{navigationTreeStore}'
        }, function (data) {
            if (data.store) {
                const root = data.store.getRoot();
                view.getNodeHashMap().add(root);

                if (data.gisNavigation) {
                    const storeRoot = data.store.getRoot(),
                        mask = CMDBuildUI.util.Utilities.addLoadMask(view);
                    me.loadChildren(storeRoot, data.gisNavigation.getRoot()).then(function () {
                        if (view) {
                            me.expandCMDBuildNode(storeRoot).then(function () {
                                me.onCheckChange(storeRoot, true);
                                vm.set("initialized", true);
                                CMDBuildUI.util.Utilities.removeLoadMask(mask);
                            });
                        }
                    });
                }
            }
        });

        vm.bind({
            attach_nav_tree_collection: '{attachNavTreeCollection}',
            initialized: '{initialized}'
        }, function (data) {
            if (data.attach_nav_tree_collection && data.initialized) {
                data.attach_nav_tree_collection.addListener('add', me.onCollectionAddHandler, me);
                if (data.attach_nav_tree_collection.length != 0) {
                    const hashMap = view.getMapContainerView().getHashMap();
                    hashMap.beginUpdate();
                    me.onCollectionAdd(data.attach_nav_tree_collection.getRange());
                    hashMap.endUpdate();
                }
            }
        });

        //this piece of code is responsable for listening new nodes appended that might be marked as selected in the tree
        vm.bind({
            objectTypeName: '{objectTypeName}',
            tree: '{gisNavigation}'
        }, function (data) {
            if (data.objectTypeName && data.tree) {
                const domainStore = Ext.getStore('domains.Domains'),
                    navTreeNodes = data.tree.findAllBy(function (item) {
                        const targetClassName = item.get('targetClass'),
                            targetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targetClassName),
                            domain = domainStore.findRecord('_id', item.get('domain'));

                        if (targetKlass) {
                            //removes the invalid subTypes
                            var targetKlassSubTypes = Ext.Array.map(targetKlass.getChildren(), function (item) {
                                return item.getId();
                            });
                            if (domain) {
                                targetKlassSubTypes = Ext.Array.difference(
                                    targetKlassSubTypes, item.get('direction') == '_1' ?
                                    domain.get('disabledSourceDescendants') :
                                    domain.get('disabledDestinationDescendants')
                                );
                            }

                            //removes the invalid superTypes
                            var targetKlassHierarchy = targetKlass.getHierarchy();
                            if (domain) {
                                targetKlassHierarchy = Ext.Array.difference(
                                    targetKlassHierarchy, item.get('direction') == '_1' ?
                                    domain.get('disabledDestinationDescendants') :
                                    domain.get('disabledSourceDescendants')
                                );
                            }

                            if (data.objectTypeName == targetClassName) {
                                return item;
                            }

                            else if (Ext.Array.contains(targetKlassSubTypes, data.objectTypeName) || Ext.Array.contains(targetKlassHierarchy, data.objectTypeName)) {
                                vm.set("extraCheckOnType", true);
                                //if the object is subtype or supertype of the target or 
                                return item;
                            }
                        } else {
                            return false;
                        }
                    });

                const handleAdd = function (objectId, store, navTreeNodes) {
                    if (store) {
                        const selected = [],
                            not_found_ids = [],
                            selectionModel = view.getSelectionModel();

                        Ext.Array.forEach(navTreeNodes, function (item, index, array) {
                            const _id_composed = Ext.String.format('{0}-{1}', objectId, item.getId()),
                                found = store.getNodeById(_id_composed);
                            if (found) {
                                selected.push(found);
                            } else {
                                not_found_ids.push(_id_composed);
                            }
                        });

                        if (not_found_ids.length) {
                            if (selectionModel.selectionMode !== "SIMPLE") {
                                selectionModel.deselectAll(true);
                            }
                            addListenerOnStore(objectId, store, navTreeNodes, not_found_ids);
                        }

                        if (selected.length) {
                            Ext.Array.forEach(selected, function (item, index, array) {
                                Ext.asap(function (record) {
                                    me.expandConsecutively(record).then(function (record) {
                                        if (vm.get("forceFirstCheck")) {
                                            me.customCheck(record);
                                            vm.set("forceFirstCheck", false);
                                        }
                                    });
                                }, me, [item]);
                            });
                            const keepSelect = selectionModel.selectionMode === "SIMPLE" ? true : false;
                            selectionModel.select(selected, keepSelect, true);
                        }
                    }
                }

                const _handleAdd = function (objectId, store, navTreeNodes, not_found_ids, st, node, index, eOpts) {
                    const found = Ext.Array.contains(not_found_ids, node.getId()) ? true : false;
                    if (found) {
                        handleAdd(objectId, store, navTreeNodes);
                    } else {
                        addListenerOnStore(objectId, store, navTreeNodes, not_found_ids);
                    }
                }

                const addListenerOnStore = function (objectId, store, navTreeNodes, not_found_ids) {
                    store.addListener('nodeappend', _handleAdd, me, {
                        single: me,
                        args: [objectId, store, navTreeNodes, not_found_ids]
                    });
                }

                const handleInsertion = function (objectTypeName, objectId, description, store, navTreeNodes) {
                    if (store) {
                        const not_found_node = [];

                        Ext.Array.forEach(navTreeNodes, function (navTreeNode, index, array) {
                            const _id_composed = Ext.String.format('{0}-{1}', objectId, navTreeNode.getId());
                            if (!store.getNodeById(_id_composed)) {
                                var newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                    _id_composed: _id_composed,
                                    _id: objectId,
                                    description: description,
                                    type: objectTypeName,
                                    navTreeNodeId: navTreeNode.getId(),
                                    leaf: navTreeNode.isNavLeaf(),
                                    checked: true
                                });
                                newnode.setNavTreeNode(navTreeNode);
                                not_found_node.push(newnode);
                            }
                        });

                        /**
                         * 
                         */
                        const makeExtraCheckOnType = function (type, navtreedef) {
                            const targetKlassName = navtreedef.get('targetClass'),
                                targetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targetKlassName),
                                typeKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(type);

                            if (targetKlassName == type) {
                                return true;
                            }

                            var targetKlassSubTypes = targetKlass.getChildren();

                            //filters the unwanted classes
                            if (navtreedef.get("subclassFilter")) {
                                const subclassFilter = navtreedef.get("subclassFilter").split(',');
                                var tmpTargetKlassSubTypes = [];
                                Ext.Array.forEach(targetKlassSubTypes, function (item, index, array) {
                                    if (Ext.Array.contains(subclassFilter, item.getId())) {
                                        tmpTargetKlassSubTypes = Ext.Array.merge(tmpTargetKlassSubTypes, item);
                                        tmpTargetKlassSubTypes = Ext.Array.merge(tmpTargetKlassSubTypes, item.getChildren());
                                    }
                                });

                                targetKlassSubTypes = tmpTargetKlassSubTypes;
                            }

                            if (Ext.Array.contains(targetKlassSubTypes, typeKlass)) {
                                return true;
                            }

                            if (Ext.Array.contains(targetKlass.getHierarchy(), type)) {
                                //should never enter here because the "type" is not a superclass but always the final type
                                return true;
                            }
                            return false;
                        }

                        if (not_found_node.length) {
                            Ext.Array.forEach(not_found_node, function (item, index, array) {
                                if (vm.get("extraCheckOnType")) {
                                    const navtreedef = item.getNavTreeNode();
                                    if (makeExtraCheckOnType(item.get('type'), navtreedef)) {
                                        me.recursiveLoadParent(item);
                                    }
                                } else {
                                    me.recursiveLoadParent(item);
                                }
                            });
                        }
                    }
                }

                if (navTreeNodes) {
                    //this bind is responsable for setting as selected the records in the navigation tree
                    vm.bind({
                        objectId: '{objectId}',
                        store: '{navigationTreeStore}',
                        initialized: '{initialized}'
                    }, function (data) {
                        if (data.store) {
                            data.store.removeListener('nodeappend', _handleAdd, me);

                            if (data.objectId && data.initialized) {
                                handleAdd(data.objectId, data.store, navTreeNodes);
                            } else {
                                const selectionModel = view.getSelectionModel();
                                if (selectionModel.selectionMode !== "SIMPLE") {
                                    selectionModel.deselectAll();
                                }
                            }
                        }
                    });

                    //this bind is responsable for populating the tree when selecting a card, loading the card based on the gisnavigationtree
                    vm.bind({
                        store: '{navigationTreeStore}',
                        initialized: '{initialized}',
                        theObject: '{theObject}'
                    }, function (data) {
                        if (data.store && data.initialized && data.theObject) {
                            handleInsertion(data.theObject.get('_type'), data.theObject.get('_id'), Ext.String.htmlEncode(data.theObject.get('Description')), data.store, navTreeNodes);
                        }
                    });
                }
            }
        });
    },

    /**
     * 
     * @param {*} view 
     * @param {*} td 
     * @param {*} cellIndex 
     * @param {*} record 
     * @param {*} tr 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     * @returns 
     */
    onBeforeCellClick: function (view, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        if (e.target.className.includes('checkbox')) {
            const checked = !record.get('checked');
            record.set('checked', checked);
            view.fireEventArgs('checkchange', [record, checked, e, eOpts]);
            return false;
        }
    },
    /**
     * 
     * @param {Ext.selection.RowModel} selectionmodel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * 
     * @note returning false will not select the record and will not continue by firing 'selectionchange' event
     * returning true will add the record to selectionmodel and firing 'selectionchange' event
     */
    onBeforeSelect: function (selectionmodel, record, index, eOpts) {
        const objectTypeName = this.getViewModel().get("objectTypeName");

        //intermediate folders are not selectable
        if (record.get('isIntermediate')) {
            return false;
        }

        if (objectTypeName == record.get('type')) {
            if (selectionmodel.selectionMode === "SIMPLE") {
                return true;
            }

            var isNew = false;
            //check if record is newer despite of already selected one
            const selectedRange = selectionmodel.getSelected().getRange();

            for (var i = 0; i < selectedRange.length && !isNew; i++) {
                const selected = selectedRange[i];
                if (selected.get('_id') != record.get('_id')) {
                    isNew = true;
                }
            }

            if (isNew) {
                //this piece of code is useful to remove the existing selection. Due to selectionMode = 'MULTI' this part is needed
                //suspend events
                selectionmodel.deselectAll(true);
                //fire events
                selectionmodel.select([record], false, false);
                return false;
            } else {
                return true;
            }
        }

        return false;
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * @returns false will not deselect the record and not firing 'onDeselect' event
     * returns true will deselect the record and firing 'onDeselect' event
     */
    onBeforeDeselect: function (selectionModel, record, index, eOpts) {
        return this.getViewModel().get("settingsMap.selectAll") ? false : true;
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     */
    onSelect: function (selectionModel, record, index, eOpts) {
        if (selectionModel.selectionMode === "SIMPLE" && record) {
            this.getView().onSelectItem(record)
        }
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selectionModel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     */
    onDeselect: function (selectionModel, record, index, eOpts) {
        if (selectionModel.selectionMode === "SIMPLE" && record) {
            this.getView().modifySelection(record, false);
        }
    },

    /**
     * 
     * @param {Ext.selection.Model} selectionModel 
     * @param {Ext.data.Model[]} selected 
     * @param {Object} eOpts 
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        if (selectionModel.selectionMode !== "SIMPLE") {
            const vm = this.getView().getMapContainerView().getViewModel();
            var objectId = null;
            if (selected.length > 0) {
                objectId = Ext.num(selected[0].get('_id'));
            }

            vm.set("objectId", objectId);
        }
    },

    /**
     * Fired by user expanding the node
     * @param {*} node 
     * @param {*} eOpts 
     */
    onItemExpand: function (node, eOpts) {
        this.expandCMDBuildNode(node);
    },

    /**
     * 
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     * @param {*} item 
     * @param {*} e 
     * @param {*} record 
     * @param {*} row 
     */
    onActionColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        const me = this,
            navTreeView = this.getView(),
            vm = navTreeView.getViewModel(),
            objectType = vm.get("objectType"),
            objectTypeName = vm.get("objectTypeName"),
            objectId = Ext.num(record.get('_id')),
            targhetTypeName = record.get('type'),
            targhetKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(targhetTypeName),
            targhetTypeKlassHierarchy = targhetKlass.getHierarchy();

        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                if (Ext.Array.contains(targhetTypeKlassHierarchy, objectTypeName)) {
                    if (!vm.get("settingsMap.selectAll")) {
                        if (!record.get('isIntermediate')) {
                            if (vm.get("objectId") == record.get("_id")) {
                                const selectionModel = navTreeView.getSelectionModel();
                                if (selectionModel.selectionMode === "SIMPLE") {
                                    if (selectionModel.isSelected(record)) {
                                        navTreeView.modifySelection(record, false);
                                        selectionModel.deselect(record, true);
                                    } else {
                                        navTreeView.modifySelection(record, true);
                                        selectionModel.select(record, true, true);
                                    }
                                }
                            }
                            vm.set("objectId", objectId);
                        } else {
                            vm.set("objectId", null);
                        }

                        me.customCheck(record);
                    }
                } else {
                    var url;
                    if (!record.get('isIntermediate')) {
                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(targhetTypeName, objectId, null, true);
                    } else {
                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(targhetTypeName, null, null, true);
                    }
                    CMDBuildUI.util.Utilities.redirectTo(url);
                }
                break;
            default:
                CMDBuildUI.util.Logger.log(
                    Ext.String.format('Redirect from tree not implemented for type: {0}', data.objectType),
                    CMDBuildUI.util.Logger.levels.debug);
                break;
        }
    },

    /**
     * 
     * @param {Ext.data.TreeModel} node 
     * @param {Boolean} checked 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onCheckChange: function (node, checked, e, eOpts) {
        const me = this,
            view = this.getView(),
            hashMap = view.getMapContainerView().getHashMap();

        node.set('checked', !checked); //restores node check to not interupt this.downCheckChange first check
        hashMap.beginUpdate();

        if (!node.isRoot() && !node.get('isIntermediate')) {
            //finds double occurencies for the node in the tree
            const nodeHashMap = view.getNodeHashMap(),
                group = nodeHashMap.getGroups().get(node.get('_id'));

            Ext.Array.forEach(group.getRange(), function (node) {
                me.downCheckChange(node, checked, hashMap);
                me.upCheckChange(node, checked, hashMap);
            });

        } else {
            me.downCheckChange(node, checked, hashMap);
            me.upCheckChange(node, checked, hashMap);
        }

        hashMap.endUpdate();
    },

    /**
     * Issue #3688
     * @param {*} card 
     */
    onCardUpdated: function (card) {
        const nodeHashMap = this.getView().getNodeHashMap(),
            groups = nodeHashMap.getGroups(),
            group = groups.get(card.getId());

        if (group) {
            const description = Ext.String.htmlEncode(card.get('Description'));
            Ext.Array.each(group.getRange(), function (item, index, array) {
                item.set('description', description);
            });
        }
    },

    /**
     * 
     * @param {*} card 
     */
    onCardDeleted: function (card) {
        const nodeHashMap = this.getView().getNodeHashMap(),
            groups = nodeHashMap.getGroups(),
            group = groups.get(card.getId());

        if (group) {
            Ext.Array.each(group.getRange(), function (item, index, array) {
                item.remove();
            });
        }
    },

    privates: {
        /**
         * 
         * @param {*} node 
         * @param {*} navtreedef 
         * @param {*} callback 
         * @param {*} scope 
         * @returns 
         */
        loadChildren: function (node, navtreedef, callback, scope) {
            const me = this,
                view = this.getView(),
                vm = this.getViewModel(),
                deferred = new Ext.Deferred(),
                idNavTree = navtreedef.getId(),
                selectAll = vm.get("settingsMap.selectAll"),
                objectTypeName = vm.get("objectTypeName"),
                selectionModel = view.getSelectionModel();

            var destinationTypeName, sourceTypeName, sourceId;

            node._childrenloaded = deferred;

            if (navtreedef.isNavRoot()) {
                destinationTypeName = navtreedef.get('targetClass');
            } else {
                // get config
                destinationTypeName = node.get('isIntermediate') ? node.get('type') : navtreedef.get('targetClass');
                sourceTypeName = node.get('isIntermediate') ? navtreedef.getParent().get('targetClass') : node.get('type');
                sourceId = node.get('_id');

                if (!me.checkDisabledDomainClasses(navtreedef, sourceTypeName, destinationTypeName)) {
                    node._childrenloaded = true;
                    deferred.resolve();
                    return;
                }
            }

            const destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName),
                advancedFilter = navtreedef.getDownFilter(sourceTypeName, sourceId, destinationTypeName),
                path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName),
                childrenstore = me.getTemporaryStore(path, advancedFilter);

            // load store
            childrenstore.load({
                callback: function (records, operation, success) {
                    //FIXME: move the logics after the load in another component
                    //Could add elements to attach_nav_tree_collection and make handle all by it's handler "onCollectionAddHandler" which calculates automatically the correct check value to insert. withouc calculating it here
                    if (success) {
                        //stores the nodes
                        const nodeHashMap = view.getNodeHashMap(),
                            //stores nodes checked value
                            hashMap = view.getMapContainerView().getHashMap();

                        hashMap.beginUpdate();

                        const showOnlyOne = navtreedef.get('showOnlyOne'),
                            firstShowOnlyOne = {};

                        if (showOnlyOne) {
                            for (var i = 0; i < node.childNodes.length; i++) {
                                var item = node.childNodes[i];
                                if (item.getNavTreeNode().getId() == idNavTree) {
                                    if (hashMap.get(item.get('_id'))) {
                                        firstShowOnlyOne[idNavTree] = true;
                                        i = node.childNodes.length;
                                    }
                                }
                            }
                        }

                        // append items to the node
                        records.forEach(function (r) {
                            const _id_composed = Ext.String.format('{0}-{1}', r.get('_id'), idNavTree);

                            if (nodeHashMap.getByKey(_id_composed)) {
                                return;
                            }

                            const hashKey = r.get("_id"),
                                hashValue = nodeHashMap.getGroups().getByKey(hashKey);
                            var fireevent, checked;

                            if (showOnlyOne) {
                                if (!Ext.isEmpty(hashValue)) {
                                    checked = hashValue.getAt(0).get('checked');

                                    if (checked) {
                                        if (!firstShowOnlyOne[idNavTree]) {
                                            checked = true;
                                            firstShowOnlyOne[idNavTree] = true;
                                        } else {
                                            checked = false;
                                            fireevent = true;
                                        }
                                    } else {
                                        checked = false;
                                    }
                                } else {
                                    if (!firstShowOnlyOne[idNavTree]) {
                                        if (node.get('checked')) {
                                            checked = true;
                                            firstShowOnlyOne[idNavTree] = true;
                                        } else {
                                            checked = false;
                                        }
                                    } else {
                                        checked = false;
                                    }
                                }
                            } else {
                                if (!Ext.isEmpty(hashValue)) {
                                    checked = hashValue.getAt(0).get('checked');

                                    if (checked != node.get('checked')) {
                                        fireevent = true;
                                    }
                                } else {
                                    checked = node.get('checked');
                                }
                            }

                            const description = Ext.String.htmlEncode(r.get("Description")),
                                c = node.createNode({
                                    text: description,
                                    leaf: navtreedef.isNavLeaf(),
                                    checked: checked,
                                    isIntermediate: false,
                                    _id_composed: _id_composed,
                                    _id: r.get('_id'),
                                    description: description,
                                    navTreeNodeId: idNavTree,
                                    parentid: node.get('_id'),
                                    parenttype: node.get('type'),
                                    type: r.get('_type')
                                });
                            c.setNavTreeNode(navtreedef);

                            nodeHashMap.add(c);
                            //adds the new check information
                            hashMap.add(r.get("_id"), checked);
                            node.appendChild(c);

                            if (fireevent) {
                                view.fireEventArgs('checkchange', [c, checked]);
                            }

                            if (selectAll && c.get("type") == objectTypeName) {
                                selectionModel.select(c, true, true);
                            }
                        });

                        node._childrenloaded = true;
                        node.sort();
                        hashMap.endUpdate();
                        deferred.resolve();
                    } else {
                        node._childrenloaded = false;
                        deferred.reject();
                    }

                    // delete item after its usage
                    Ext.asap(function () {
                        childrenstore.destroy();
                    });
                }
            });

            return deferred.promise;
        },

        /**
         * 
         * @param {*} domainName 
         * @param {*} sourceClass 
         * @param {*} destinationClass 
         * @returns {Boolean} if true the check is passed. if false, the check is failed
         */
        checkDisabledDomainClasses: function (navtreedef, sourceClassName, destinationClassName) {
            const domains = Ext.getStore('domains.Domains'),
                domain = domains.findRecord('_id', navtreedef.get('domain'));

            if (Ext.isEmpty(domain)) {
                return false;
            }

            if (Ext.Array.contains(domain.get('disabledSourceDescendants'), navtreedef.get('direction') == '_1' ? destinationClassName : sourceClassName) ||
                Ext.Array.contains(domain.get('disabledDestinationDescendants'), navtreedef.get('direction') == '_1' ? sourceClassName : destinationClassName)) {
                return false;
            }

            return true;
        },

        /**
         * 
         * @param {*} proxyUrl 
         * @param {*} advancedFilter 
         * @returns 
         */
        getTemporaryStore: function (proxyUrl, advancedFilter) {
            // create temp store 
            return Ext.create("Ext.data.Store", {
                fields: ['_id', 'Description'],
                proxy: {
                    type: 'baseproxy',
                    url: proxyUrl,
                    extraParams: {
                        attrs: 'Id,Description,IdClass'
                    }
                },
                advancedFilter: advancedFilter,
                pageSize: 0,
                sorters: ['Description'],
                remoteSort: true
            });
        },

        /**
         * 
         * @param {*} collection 
         * @param {*} details 
         * @param {*} eOpts 
         */
        onCollectionAddHandler: function (collection, details, eOpts) {
            //stores nodes checked value
            const hashMap = this.getView().getMapContainerView().getHashMap();
            hashMap.beginUpdate();
            this.onCollectionAdd(details);
            hashMap.endUpdate();
        },

        /**
         * This function elaborates the new GeoValueTree elements wich must be added in the tree.
         * The function verifies if the records are yet in the tree, if they are the inserting is skipped otherwise they are inserted in the tree.
         * 
         * The function is called recursively until all items are added in the tree. 
         * NOTE: The item not added are stored and added late. The late insertion can be caused because the child we want to insert doesn't have the proper parent in the tree yet.
         * this is caused because the onCollectionAddHandler.details.items are sorted by the key and is not mantained the order given by the server.
         * @param {} items 
         */
        onCollectionAdd: function (items) {
            const me = this,
                itemsNotAdded = [],
                nodeHashMap = this.getView().getNodeHashMap();

            Ext.Array.forEach(items, function (item, index, array) {
                const _id_composed = item.getId();

                if (nodeHashMap.get(_id_composed)) {
                    return;
                } else {
                    const navtreedef = item.getNavTreeNode();
                    var _id_composed_parent;

                    if (navtreedef.isNavRoot()) {
                        _id_composed_parent = CMDBuildUI.view.map.tab.cards.NavigationTree.root_id_composed;
                    } else {
                        _id_composed_parent = Ext.String.format('{0}-{1}', item.get('parentid'), navtreedef.getParent().getId());
                    }

                    const parentNode = nodeHashMap.get(_id_composed_parent);
                    if (parentNode) {
                        me.insertNode(parentNode, item);
                    } else {
                        itemsNotAdded.push(item);
                    }
                    return;
                }
            });

            if (items.length == itemsNotAdded.length) {
                return;
            }

            if (itemsNotAdded.length) {
                me.onCollectionAdd(itemsNotAdded);
            }
        },

        /**
         * 
         * @param {CMDBuildUI.model.gis.GeoValueTree} node 
         * @param {CMDBuildUI.model.gis.GeoValueTree} child 
         */
        insertNode: function (node, child) {
            const view = this.getView(),
                nodeHashMap = view.getNodeHashMap(),
                //stores nodes checked value
                hashMap = view.getMapContainerView().getHashMap();

            const checked = this.checkedValue(node, child);
            child.set('checked', checked);

            const parentNavTree = node.getNavTreeNode(),
                navTreeNode = child.getNavTreeNode(),
                idNavTree = navTreeNode.getId(),
                addIntermediate = navTreeNode.get("subclassViewMode") === "subclasses" && navTreeNode.get("subclassViewShowIntermediateNodes");

            if (addIntermediate || (parentNavTree && parentNavTree.childs().getRange().length > 1 && navTreeNode.get("subclassViewMode") === "cards")) {
                const _id_composed_intermediate = Ext.String.format('{0}-{1}-{2}-intermediate', node.get('_id'), idNavTree, node.get('type'));
                var subChildNode = node.findChild('_id_composed', _id_composed_intermediate);

                if (!subChildNode) {
                    subChildNode = node.createNode({
                        description: navTreeNode.get("_description_translation") || CMDBuildUI.util.helper.ModelHelper.getObjectDescription(navTreeNode.get("targetClass")),
                        leaf: true,
                        checked: node.get('checked'),
                        isIntermediate: true,
                        navTreeNodeId: idNavTree,
                        _id_composed: _id_composed_intermediate,
                        _id: node.getId(),
                        type: child.get('type')
                    });
                    subChildNode.setNavTreeNode(navTreeNode);
                    node.appendChild(subChildNode);
                }
                node = subChildNode;
            }

            const nodes = [];
            if (navTreeNode.get("subclassViewMode") === "subclasses") {
                const childKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(child.get('type')),
                    subclassFilter = navTreeNode.get("subclassFilter");

                if (!Ext.isEmpty(subclassFilter)) {
                    subclassFilter.split(',').forEach(function (subtype) {
                        const subtypeKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(subtype),
                            isKlassChild = subtypeKlass === childKlass || Ext.Array.contains(subtypeKlass.getChildren(), childKlass);

                        if (isKlassChild) {
                            const _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', node.get('_id'), idNavTree, subtype),
                                desc = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subtype);
                            var subTypeChildNode = node.findChild('_id_composed', _id_composed);

                            if (!subTypeChildNode) {
                                subTypeChildNode = node.appendChild({
                                    description: desc,
                                    // _navtreedef: subChild,
                                    leaf: true,
                                    checked: node.get('checked'),
                                    isIntermediate: true,
                                    navTreeNodeId: idNavTree,
                                    _id_composed: _id_composed,
                                    _id: node.get('_id'),
                                    type: subtype
                                });
                                subTypeChildNode.setNavTreeNode(navTreeNode);
                            }
                            nodes.push(subTypeChildNode);
                        }
                    });
                }
            }

            if (navTreeNode.isNavLeaf()) {
                child.set('leaf', true);
            } else {
                if (child._childrenloaded && Ext.isEmpty(child.childNodes)) {
                    child.set('leaf', true);
                } else {
                    child.set('leaf', false);
                }
            }

            nodeHashMap.add(child);
            if (nodes.length) {
                Ext.Array.forEach(nodes, function (node) {
                    node.appendChild(child);
                });
            } else {
                node.appendChild(child);
            }

            //adds the new check information
            hashMap.add(child.get("_id"), checked);
            view.fireEventArgs('checkchange', [child, checked]);
        },

        /**
         * 
         * @param {*} parent 
         * @param {*} child 
         * @returns 
         */
        checkedValue: function (parent, child) {
            const hashKey = child.get("_id"),
                navtreedef = child.getNavTreeNode(),
                showOnlyOne = navtreedef.get('showOnlyOne'),
                hashValue = this.getView().getNodeHashMap().get(hashKey);
            var checked = true;

            if (showOnlyOne) {
                if (!Ext.isEmpty(hashValue)) {
                    checked = hashValue[0].get('checked');

                    if (checked) {
                        //this for analizes the siblings. Modifies the checked value
                        for (var i = 0; i < parent.childNodes.length; i++) {
                            const item = parent.childNodes[i];
                            if (item.get('navTreeNodeId') == child.get('navTreeNodeId') && item.get('checked')) {
                                checked = false;
                                break;
                            }
                        }

                        if (checked && parent.get('checked')) {
                            //no sibling has a checked value;
                            return true;
                        } else {
                            //a sibling has a checked value
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    for (var i = 0; i < parent.childNodes.length; i++) {
                        const item = parent.childNodes[i];
                        if (item.get('navTreeNodeId') == child.get('navTreeNodeId') && item.get('checked')) {
                            checked = false;
                            break;
                        }
                    }

                    if (checked && parent.get('checked')) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                if (!Ext.isEmpty(hashValue)) {
                    return hashValue[0].get('checked');
                } else {
                    return parent.get('checked');
                }
            }
        },

        /**
         * 
         * @param {*} node 
         * @param {*} menuDef 
         */
        expandCMDBuildNode: function (node) {
            //FIXME: the intermediate nodes are different nodes, should not have set the type or carry the one of the parent as _id do
            const me = this,
                deferred = new Ext.Deferred();
            if (node.isIntermediate) {
                deferred.resolve();
                return deferred;
            }

            const promises = [],
                childNode = node;

            if (!childNode._childrenloaded) {
                const menudef = childNode.getNavTreeNode();
                Ext.Array.forEach(menudef.childs().getRange(), function (subChild, index, allitems) {
                    var subChildNode;
                    const addintermediate = subChild.get("subclassViewMode") === "subclasses" && subChild.get("subclassViewShowIntermediateNodes");
                    if (addintermediate || (allitems.length > 1 && subChild.get("subclassViewMode") === "cards")) {
                        const _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', childNode.get('_id'), subChild.getId(), childNode.get('type'));
                        subChildNode = childNode.findChild('_id_composed', _id_composed);

                        if (!subChildNode) {
                            subChildNode = childNode.appendChild({
                                description: subChild.get("_description_translation") || CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subChild.get("targetClass")),
                                leaf: true,
                                checked: childNode.get('checked'),
                                isIntermediate: true,
                                _id_composed: _id_composed,
                                _id: childNode.get('_id'),
                                type: subChild.get('targetClass')
                            });
                            subChildNode.setNavTreeNode(subChild);
                        }
                    } else {
                        subChildNode = childNode;
                    }

                    if (subChild.get("subclassViewMode") === "subclasses") {
                        const subclassFilter = subChild.get("subclassFilter");

                        if (!Ext.isEmpty(subclassFilter)) {
                            subclassFilter.split(',').forEach(function (subtype) {
                                const _id_composed = Ext.String.format('{0}-{1}-{2}-intermediate', subChildNode.get('_id'), subChild.getId(), subtype);
                                var subTypeChildNode = subChildNode.findChild('_id_composed', _id_composed);

                                if (!subTypeChildNode) {
                                    var desc = subChild.get("_subclass_" + subtype + "_description_translation");
                                    if (!desc) {
                                        desc = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(subtype);
                                    }
                                    subTypeChildNode = subChildNode.appendChild({
                                        description: desc,
                                        // _navtreedef: subChild,
                                        leaf: true,
                                        checked: subChildNode.get('checked'),
                                        isIntermediate: true,
                                        _id_composed: _id_composed,
                                        _id: subChildNode.get('_id'),
                                        type: subtype
                                    });
                                    subTypeChildNode.setNavTreeNode(subChild);
                                }
                                if (!subTypeChildNode._childrenloaded) {
                                    promises.push(me.loadChildren(subTypeChildNode, subChild));
                                } else if (subTypeChildNode._childrenloaded instanceof Ext.Deferred) {
                                    promises.push(subTypeChildNode._childrenloaded);
                                }
                            });
                        }
                    } else {
                        if (!subChildNode._childrenloaded) {
                            promises.push(me.loadChildren(subChildNode, subChild));
                        } else if (subChildNode._childrenloaded instanceof Ext.Deferred) {
                            promises.push(subChildNode._childrenloaded);
                        }
                    }
                });
            } else if (childNode._childrenloaded instanceof Ext.Deferred) {
                promises.push(childNode._childrenloaded);
            }

            if (promises.length != 0) {
                Ext.Promise.all(promises).then(function () {
                    if (!node._childrenloaded) {
                        node._childrenLoaded = true;
                    }
                    deferred.resolve();
                });
            } else {
                deferred.resolve();
            }
            return deferred.promise;
        },

        /**
         * 
         * @param {Ext.data.TreeModel} node 
         * @param {Boolean} checked 
         */
        customCheck: function (node) {
            if (node.get('checked')) {
                return;
            }

            var parentNode = node,
                navigationTree = node.getNavTreeNode();

            while (!navigationTree.isNavRoot()) {
                if (navigationTree.get('showOnlyOne')) {
                    this.onCheckChange(parentNode, true);
                    return;
                } else {
                    parentNode = parentNode.parentNode;
                    navigationTree = parentNode.getNavTreeNode();
                }
            }

            this.onCheckChange(node, true);
        },

        /**
         * 
         * @param {*} node 
         * @param {*} checked 
         * @param {*} hashMap 
         * @returns 
         */
        downCheckChange: function (node, checked, hashMap) {
            if (node.get('checked') == checked) {
                return;
            }

            const me = this,
                firstShowOnlyOne = {},
                nodeHashMap = this.getView().getNodeHashMap();

            node.set('checked', checked);

            /**
             * sets the sigling to unchecked if needed
             */
            if (checked) {
                const navtreedef = node.getNavTreeNode();
                if (navtreedef && navtreedef.get('showOnlyOne')) {
                    Ext.Array.forEach(node.parentNode.childNodes, function (item, array, index) {
                        if (item != node && item.getNavTreeNode() == navtreedef) {
                            const group = nodeHashMap.getGroups().get(item.get('_id'));
                            Ext.Array.forEach(group.getRange(), function (item) {
                                me.downCheckChange(item, false, hashMap);
                            });
                        }
                    });
                }
            }

            if (!node.get('isIntermediate')) {
                hashMap.add(node.get('_id'), checked);
            }

            node.eachChild(function (child) {
                if (!child.get('isIntermediate')) {
                    //finds double occurencies for the child in the tree
                    const group = nodeHashMap.getGroups().get(child.get('_id'));
                    if (group) {
                        const childs = group.getRange();
                        Ext.Array.forEach(childs, function (child) {
                            const navtreedef = child.getNavTreeNode();
                            if (navtreedef.get('showOnlyOne')) {
                                if (!firstShowOnlyOne[navtreedef.getId()]) {
                                    if (checked) {
                                        firstShowOnlyOne[navtreedef.getId()] = true;
                                    }
                                    me.downCheckChange(child, checked, hashMap);
                                    me.upCheckChange(child, checked, hashMap);
                                } else {
                                    me.downCheckChange(child, false, hashMap);
                                    me.upCheckChange(child, false, hashMap);
                                }
                            } else {
                                me.downCheckChange(child, checked, hashMap);
                                me.upCheckChange(child, checked, hashMap);
                            }
                        });
                    }
                } else {
                    me.downCheckChange(child, checked, hashMap);
                    me.upCheckChange(child, checked, hashMap);
                }
            });
        },

        /**
         * 
         * @param {*} node 
         * @param {*} checked 
         * @param {*} hashMap 
         */
        upCheckChange: function (node, checked, hashMap) {
            if (checked) {
                node.set('checked', checked);
                if (!node.get('isIntermediate')) {
                    hashMap.add(node.get('_id'), checked);
                }

                const me = this,
                    parent = node.parentNode,
                    navtreedef = node.getNavTreeNode();

                if (navtreedef && navtreedef.get('showOnlyOne')) {
                    const nodeHashMap = this.getView().getNodeHashMap();

                    Ext.Array.forEach(parent.childNodes, function (item) {
                        if (item != node && item.getNavTreeNode() == navtreedef && item.get('checked')) {
                            var group = nodeHashMap.getGroups().get(item.get('_id'));
                            Ext.Array.forEach(group.getRange(), function (item) {
                                me.downCheckChange(item, false, hashMap);
                            });
                        }
                    });
                }

                if (parent) {
                    this.upCheckChange(parent, checked, hashMap);
                }
            }
        },

        /**
         * 
         * @param {*} node 
         * @returns 
         */
        expandConsecutively: function (node) {
            const view = this.getView(),
                deferred = new Ext.Deferred(),
                path = [],
                tmpNode = node;

            node = node.parentNode;
            while (!node.isRoot()) {
                path.unshift(node);
                node = node.parentNode;
            }

            if (!view._mask) {
                const mask = CMDBuildUI.util.Utilities.addLoadMask(view);
                view._mask = mask;
            }

            this._expandConsecutively(0, path, deferred, tmpNode);
            return deferred.promise;
        },

        /**
         * 
         * @param {*} i 
         * @param {*} path 
         * @param {*} deferred 
         * @param {*} record 
         */
        _expandConsecutively: function (i, path, deferred, record) {
            const me = this,
                view = me.getView();

            if (i < path.length) {
                this.expandCMDBuildNode(path[i]).then(function () {
                    path[i].expand();
                    me._expandConsecutively(++i, path, deferred, record);
                });
            } else {
                if (view._mask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(view._mask);
                    delete view._mask;
                    view.ensureVisible(record.getPath(), {
                        animate: true
                    });
                    deferred.resolve(record);
                }
            }
        },

        /**
         * 
         * @param {*} node 
         * @returns 
         */
        recursiveLoadParent: function (node) {
            const deferred = new Ext.Deferred();
            this._recursiveLoadParent(node).then(function () {
                deferred.resolve(node);
            });
            return deferred.promise;
        },

        /**
         * 
         * @param {*} node 
         * @returns 
         */
        _recursiveLoadParent: function (node) {
            const me = this,
                deferred = new Ext.Deferred();

            me.loadParent(node).then(function (parent) {
                if (parent) {
                    const nodeHashMap = me.getView().getNodeHashMap(),
                        treeNode = nodeHashMap.get(parent.getId());
                    if (treeNode) {
                        me.insertNodeWithChilds(treeNode, node);
                        deferred.resolve();
                    } else {
                        me._recursiveLoadParent(parent).then(function () {
                            deferred.resolve();
                        });
                    }
                } else {
                    deferred.resolve();
                }
            });

            return deferred.promise;
        },

        /**
         * 
         * @param {CMDBuildUI.model.gis.GeoValueTree} node 
         */
        loadParent: function (node) {
            //FIXME: due to bug, the navtreenode attached to the node is not congruent with the rest of the tree, making the function at #641 using the getNavTreeNode() on the "nodeToCheck" instead of "node"
            const me = this,
                deferred = new Ext.Deferred(),
                navtreedef = node.getNavTreeNode(),
                nodeHashMap = this.getView().getNodeHashMap();

            if (navtreedef.isNavRoot()) {
                var root;
                if (navtreedef.get("ecqlFilter")) {
                    root = nodeHashMap.get(CMDBuildUI.view.map.tab.cards.NavigationTree.root_id_composed);
                    this.hasChildren(root, node).then(function (root) {
                        if (root) {
                            deferred.resolve(root);
                        } else {
                            deferred.resolve(null);
                        }
                    });
                } else {
                    root = nodeHashMap.get('root_id_composed');
                    deferred.resolve(root);
                }
                return deferred.promise;
            }

            const sourceId = node.get('_id'),
                sourceTypeName = node.get('type'),
                destinationTypeName = navtreedef.getParent().get('targetClass'),
                destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName),
                advancedFilter = navtreedef.getUpFilter(sourceTypeName, sourceId, destinationTypeName),
                path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName),
                // create temp store 
                parentstore = this.getTemporaryStore(path, advancedFilter);

            parentstore.load({
                callback: function (records, operation, success) {
                    //FIXME: move the logics after the load in another component
                    var newnode;
                    if (success) {
                        //if has ecqlFilter
                        if (navtreedef.get("ecqlFilter")) {
                            if (records && records.length == 1) {
                                const record = records[0],
                                    parentNavTree = navtreedef.getParent(),
                                    description = Ext.String.htmlEncode(record.get('Description'));
                                newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                    _id_composed: Ext.String.format('{0}-{1}', record.get('_id'), parentNavTree.getId()),
                                    _id: record.get('_id'),
                                    type: record.get('_type'),
                                    navTreeNodeId: parentNavTree.getId(),
                                    description: description
                                });
                                newnode.setNavTreeNode(parentNavTree);
                                /* this piece of code is created to check if the found parent starting from the given node has children.
                                 * It could not have children because the cql filter is not applied in the call for the parent but must be called from the parent to the children
                                 */
                                me.hasChildren(newnode, node).then(function (newnode) {
                                    if (newnode) {
                                        newnode.appendChild(node);
                                        node.set('parentid', record.get('_id'));
                                        deferred.resolve(newnode);
                                    } else {
                                        deferred.resolve(null);
                                    }
                                });
                            } else {
                                deferred.resolve();
                            }
                        } else {
                            if (records && records.length == 1) {
                                const record = records[0],
                                    parentNavTree = navtreedef.getParent(),
                                    description = Ext.String.htmlEncode(record.get('Description'));
                                newnode = Ext.create('CMDBuildUI.model.gis.GeoValueTree', {
                                    _id_composed: Ext.String.format('{0}-{1}', record.get('_id'), parentNavTree.getId()),
                                    _id: record.get('_id'),
                                    type: record.get('_type'),
                                    navTreeNodeId: parentNavTree.getId(),
                                    description: description
                                });
                                newnode.setNavTreeNode(parentNavTree);
                                newnode.appendChild(node);
                                node.set('parentid', record.get('_id'));
                                deferred.resolve(newnode);
                            } else {
                                deferred.reject();
                            }
                        }
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        },

        /**
         * 
         * @param {*} node 
         * @param {*} nodeToCheck 
         * @returns 
         */
        hasChildren: function (node, nodeToCheck) {
            const deferred = new Ext.Deferred(),
                navtreedef = nodeToCheck.getNavTreeNode();
            var sourceTypeName, sourceId;

            if (!navtreedef.isNavRoot()) {
                sourceTypeName = node.get('type');
                sourceId = node.get('_id');
            }

            const destinationTypeName = nodeToCheck.get('type'),
                destinationType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationTypeName),
                advancedFilter = navtreedef.getDownFilter(sourceTypeName, sourceId, destinationTypeName),
                path = CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(destinationType, destinationTypeName),
                tmpStore = this.getTemporaryStore(path, advancedFilter);

            tmpStore.load({
                callback: function (records, operation, success) {
                    if (success) {
                        const found = Ext.Array.findBy(records, function (item, index, array) {
                            return item.get('_id') == nodeToCheck.get('_id');
                        });

                        if (found) {
                            deferred.resolve(node);
                        } else {
                            deferred.resolve(false);
                        }
                    } else {
                        deferred.resolve(false);
                    }
                    // delete item after its usage
                    Ext.asap(function () {
                        tmpStore.destroy();
                    });
                }
            });

            return deferred.promise;
        },

        /**
         * 
         * @param {*} node 
         * @param {*} child 
         */
        insertNodeWithChilds: function (node, child) {
            const a = [],
                attach_nav_tree_collection = this.getViewModel().get("attachNavTreeCollection");

            this.linearizeChilds(child, a);
            //TODO: instead of calling the handler, should add elements to 'attach_nav_tree_collection' component. Then the event will bi fired automatically and handled; 
            //like in app/view/map/MapController.js:893
            //maybe the before TODO: can't be applied due to app/view/map/tab/cards/NavigationTree.js:653.         
            // this.getController().onCollectionAdd(a);

            attach_nav_tree_collection.add(a);
        },

        /**
         * 
         * @param {*} node 
         * @param {*} arr 
         */
        linearizeChilds: function (node, arr) {
            const me = this,
                _tmpchilds = [];
            Ext.Array.forEach(node.childNodes, function (item, index, array) {
                _tmpchilds.push(item);
            });

            arr.push(node);
            Ext.Array.forEach(_tmpchilds, function (item, index, array) {
                item.remove();
                arr.push(item);
                me.linearizeChilds(item, arr);
            });
        }
    }
});
