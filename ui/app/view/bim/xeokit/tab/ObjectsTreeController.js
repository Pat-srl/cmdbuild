Ext.define('CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-tab-objectstree',

    control: {
        '#': {
            checkchange: 'onCheckChange',
            objectselected: 'onObjectSelected',
            selectelementtree: 'onSelectElementTree'
        }
    },

    /**
     * Fired when checkbox's checked property changes on a node changes and used to set visible or not the entity in canvas
     * @param {Ext.data.TreeModel} node
     * @param {Boolean} checked
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onCheckChange: function (node, checked, e, eOpts) {
        var scene = this.getView().getContainer().getViewer().scene,
            id = node.get('entityId');
        scene.setObjectsVisible(id, checked);
    },

    /**
     * Select on the tree the entity picked in the canvas
     * @param {Object} entity
     */
    onObjectSelected: function (entity) {
        var view = this.getView(),
            vm = this.getViewModel(),
            recordsStore = view.getStore().getRange(),
            rowTree = recordsStore[vm.get('depth')];

        if (entity) {
            rowTree = Ext.Array.findBy(recordsStore, function (item, index, allitems) {
                return item.get('depth') == 1 && item.get('idModel') == entity.model.id;
            });
        }

        var record = this.expandTree(rowTree, entity);
        if (record) {
            view.ensureVisible(record, {
                select: true
            });
        }
    },

    /**
     * Fired when click on arrow on tree
     *
     * @param {Ext.tree.View} tool
     * @param {Ext.data.TreeModel} record
     */
    onSelectElementTree: function (tool, record) {
        var view = this.getView(),
            container = view.getContainer(),
            viewCard = container.getCardTab(),
            propertiesTab = container.getPropertiesTab(),
            viewer = container.getViewer(),
            scene = viewer.scene,
            objectsSelected = scene.selectedObjects;

        tool.setSelection(record);

        Ext.Array.forEach(Ext.Object.getValues(objectsSelected), function (item, index, allitems) {
            item.selected = false;
        });

        if (record.get('leaf')) {
            scene.setObjectsSelected(record.get('entityId'), true);
            var entity = Ext.Array.findBy(Ext.Object.getValues(scene.objects), function (entity, index) {
                return entity.id == record.get('entityId');
            });
            if (entity) {
                if (!entity.mappingInfo) {
                    CMDBuildUI.util.Bim.getRelatedCard(viewer.projectId, entity.id, function (data) {
                        entity.mappingInfo = data;
                        viewCard.setEntity(entity);
                    });
                } else {
                    viewCard.setEntity(entity);
                }
                propertiesTab.setEntity(entity);
                container.getLayerTab().fireEvent('objectselected', entity);
                viewer.cameraFlight.flyTo(entity);
            } else {
                propertiesTab.setEntity();
            }
        } else {
            var entitiesId = [];
            this.getEntityIdElements(record.childNodes, entitiesId);
            scene.setObjectsSelected(entitiesId, true);
            propertiesTab.setEntity();
            viewCard.setEntity();
            container.getCanvasPanel().fireEvent('resetView');
        }
    },

    privates: {
        /**
         * Search in recursive mode the record to select on the tree
         * @param {Object} elements the root from which to start the search
         * @param {Object} entity
         * @returns the record represented by the entity
         */
        expandTree: function (elements, entity) {
            var me = this,
                view = this.getView(),
                vm = this.getViewModel();

            if (!entity) {
                if (elements) {
                    if (elements.childNodes.length === 1) {
                        vm.set('depth', vm.get('depth') + 1);
                        me.expandTree(elements.childNodes[0], null);
                    }
                    view.expandNode(elements);
                }
            } else {
                var record = null;

                Ext.Array.each(elements, function (item, index, allitems) {
                    Ext.Array.each(item.childNodes, function (elem, index, allitems) {
                        if (elem.data.leaf) {
                            if (entity.id === elem.data.entityId) {
                                record = elem;
                                return false;
                            }
                        } else {
                            record = me.expandTree(elem, entity);
                            if (record) {
                                return false;
                            }
                        }
                    });

                    if (record) {
                        view.expandNode(item);
                        return false;
                    }
                });

                return record;
            }
        },

        /**
         * Get the Id for all entities that are children of the record
         *
         * @param {Ext.data.TreeModel} records
         * @param {Array} idEntities
         */
        getEntityIdElements: function (records, idEntities) {
            var me = this;
            Ext.Array.forEach(records, function (item, index, allitems) {
                if (item.get('leaf')) {
                    idEntities.push(item.get('entityId'));
                } else {
                    me.getEntityIdElements(item.childNodes, idEntities);
                }
            });
        }
    }
});
