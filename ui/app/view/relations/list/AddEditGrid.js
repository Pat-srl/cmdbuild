Ext.define('CMDBuildUI.view.relations.list.AddEditGrid', {
    mixinId: 'relations-list-addeditgrid-mixin',

    /**
     * 
     * @param {Ext.data.Model} record 
     */
    selectItemAfterCreation: function (record) {
        var me = this,
            store = this.getStore(),
            extraparams = store.getProxy().getExtraParams(),
            domainName = this.getViewModel().get("theDomain").get("name");

        // add extra params
        extraparams.positionOf = record.get("_id");
        extraparams.positionOf_goToPage = false;
        store.load({
            callback: function () {
                // remove position parameters
                delete extraparams.positionOf;
                delete extraparams.positionOf_goToPage;

                // select item
                var metadata = store.getProxy().getReader().metaData,
                    posinfo = (metadata && metadata.positions) && metadata.positions[record.get("_id")] || {};

                if (posinfo.positionInTable !== undefined) {
                    me.ensureVisible(posinfo.positionInTable, {
                        callback: function () {
                            var item = store.getById(record.get("_id"));
                            if (item.get("_" + domainName + "_available")) {
                                me.setSelection(item);
                            }
                        }
                    });
                }
            },
            scope: this
        });
    },

    /**
     * 
     * @param {Ext.data.Store} store 
     * @param {Number|String} recordId 
     */
    selectItemAfterLoadWithPosition: function (store, recordId) {

    }
});