Ext.define('CMDBuildUI.view.graph.tab.cards.ListCardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-listcard',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectionchange: 'onSelectionChange'
        }
    },

    listen: {
        store: {
            '#relationStore': {
                add: 'onRelationStoreAdd',
                remove: 'onRelationStoreRemove'
            }
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.graph.tab.cards.ListCard} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().bind("{selectedNode}", function (selectedNodes) {
            if (!selectedNodes || selectedNodes.length === 0) return;

            const store = view.getStore(),
                selectionModel = view.getSelectionModel(),
                records = [];

            Ext.Array.forEach(selectedNodes, function (item, index, allitems) {
                records.push(store.findRecord('_destinationId', item.id));
            });

            selectionModel.select(records, false, true); //This doesn't fire selectionchange event
        });
    },

    /**
     * This function handles the selection from GRID -> CANVAS
     * @param {Ext.selection.Model} selectionModel
     * @param {[Ext.data.Model]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        const ids = [];
        Ext.Array.forEach(selected, function (item, index, allitems) {
            ids.push(item.get('_destinationId'));
        });

        CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(ids, false);
    },

    /**
     * This function handles the addition of records in this store
     * @param {Ext.data.store} relationStore 
     * @param {Ext.data.Model} records 
     * @param {Number} index
     * @param {Object} eOpts 
     */
    onRelationStoreAdd: function (store, records, index, eOpt) {
        const listCardStore = this.getViewModel().get('listCardStore');

        Ext.Array.forEach(records, function (record, index, allrecords) {
            if (!listCardStore.findRecord('_destinationId', record.get('_destinationId'))) {
                const newRecord = record.clone();

                //handles compound name in destination. Used to get class description from class.Class store
                var destinationType = newRecord.get('_destinationType');

                if (destinationType.includes('compound_')) {
                    destinationType = destinationType.replace('compound_', '');
                    newRecord.set('destTypeDescription', Ext.String.format('{0} Compound', destinationType));
                    newRecord.set('_destinationDescription', Ext.String.format('{0} {1}', destinationType, record.nodes().getRange().length));
                } else {
                    //get class description and set it
                    const CMDBClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType),
                        destTypeDescription = CMDBClass.getTranslatedDescription();
                    newRecord.set('destTypeDescription', destTypeDescription);
                }

                listCardStore.insert(0, newRecord);
            }
        });

        listCardStore.commitChanges();
    },

    /**
     * This function handles this store
     * @param {Ext.data.store} store 
     * @param {[Ext.data.Model]} records 
     * @param {Number} index
     * @param {Boolean} isMove
     * @param {Object} eOpts 
     */
    onRelationStoreRemove: function (store, records, index, isMove, eOpts) {
        const listCardStore = this.getViewModel().get('listCardStore');
        Ext.Array.forEach(records, function (record, index, allrecords) {
            const foundRecord = listCardStore.findRecord('_destinationId', record.get('_destinationId'));
            if (foundRecord) {
                listCardStore.remove(foundRecord);
            }
        });
    }
});