Ext.define('CMDBuildUI.view.graph.tab.cards.ListClassController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-listclass',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectionchange: 'onSelectionChange'
        }
    },

    listen: {
        global: {
            acquisitionend: 'onAcquisitionEnd'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.graph.tab.cards.ListClass} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().bind("{selectedNode}", function (selectedNodes) {
            if (!selectedNodes || selectedNodes.length === 0) return;

            const classTarget = selectedNodes[0].type,
                selectionModel = view.getSelectionModel();

            //tells if all the selected nodes have the same class
            for (var i = 1; i < selectedNodes.length; i++) {
                if (selectedNodes[i].type != classTarget) {
                    selectionModel.deselectAll(true);
                    return;
                }
            }

            const record = view.getStore().findRecord('classTarget', classTarget);
            //not all nodes of that class are selected
            if (record) {
                if (record.get('qt') != selectedNodes.length) {
                    selectionModel.deselectAll(true);
                    return;
                }

                selectionModel.select(record, false, true); //This doesn't fire selectionchange event
            }
        });
    },

    /**
     * This function handles the event in which the store finisced loading.
     * To fill it's information in the store it refers to listClassStore
     */
    onAcquisitionEnd: function () {
        const view = this.getView(),
            store = view.getStore(),
            selectionModel = this.getView().getSelectionModel(),
            selection = selectionModel.getSelection()[0],
            tabPanelView = view.up('graph-tab-tabpanel'),
            listCardStore = tabPanelView.down('graph-tab-cards-listcard').getViewModel().get("listCardStore");

        var selected;

        if (selection) {
            selected = {
                classTarget: selection.get('classTarget'),
                qt: selection.get('qt')
            };
        }

        store.removeAll(); //doesn't fire event

        Ext.Array.forEach(listCardStore.getRange(), function (record, index, allrecords) {
            const classTarget = record.get('_destinationType'),
                recordIndex = store.find('classTarget', classTarget);

            if (recordIndex == -1) {//insert new record
                const newRecord = Ext.create('CMDBuildUI.model.graph.ListClass', {
                    classTarget: classTarget,
                    qt: 1,
                    destTypeDescription: record.get('destTypeDescription')
                });

                store.insert(0, newRecord);
            } else {
                //increment quantity
                const oldRecord = store.getAt(recordIndex);
                oldRecord.set('qt', oldRecord.get('qt') + 1);
            }
        });

        store.commitChanges(); //avoid dirty records

        if (selected) {
            const record = store.findRecord('classTarget', selected.classTarget);

            if (record && record.get('qt') == selected.qt) {
                selectionModel.select(record, false, true);
            }
        }
    },

    /**
     * This function handles the selection from GRID -> CANVAS
     * @param {Ext.selection.Model} selectionModel
     * @param {[Ext.data.Model]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        if (!selected.length) return;
        const ids = [],
            store = this.getViewModel().get('relationStore'),
            classTarget = selected[0].get('classTarget');

        Ext.Array.forEach(store.getRange(), function (record, index, allrecords) {
            if (record.get('_destinationType') == classTarget) {
                ids.push(record.get('_destinationId'));
            }
        });

        CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(ids, false);
    }
});