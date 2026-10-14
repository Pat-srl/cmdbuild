Ext.define('CMDBuildUI.mixins.grids.Grid', {
    mixinId: 'grids-grid-mixin',

    reserveScrollbar: true,

    /**
     * 
     * @param {CMDBuildUI.store.classes.Cards} store 
     * @param {Ext.model.Model} record
     * @param {Object} config
     * @param {Object} metaData
     */
    expandRowAfterLoadWithPosition: function (store, newid, config, metaData) {
        var me = this;
        if (!metaData) {
            metaData = store.getProxy().getReader().metaData;
        }
        var posinfo = (metaData && metaData.positions) && metaData.positions[newid] || { positionInPage: 0 };
        if (!posinfo.pageOffset) {
            Ext.asap(function () {
                me.expandSelection(posinfo.positionInPage, store, newid, config);
            });
        } else {
            me.ensureVisible(posinfo.positionInTable, {
                callback: function () {
                    Ext.asap(function () {
                        me.expandSelection(posinfo.positionInTable, store, newid, config);
                    });
                }
            });
        }
    },

    /**
     * Expands selected row, if selection is present
     * 
     * @param {Integer} index Row index
     * @param {Ext.model.Model} record
     * @param {Object} config
     */
    expandSelection: function (index, store, newid, config) {
        var expander = this.getPlugin("forminrowwidget");
        var record = store.getById(newid);
        if (expander && record) {
            if (index !== -1) {

                //if the row is not expanded
                var internalId = record.internalId;
                if (!expander.recordsExpanded[internalId]) {
                    expander.toggleRow(index, record, config);
                }
            } else {
                CMDBuildUI.util.Notifier.showWarningMessage(
                    "Card not found"
                );
            }
        }
    },

    /**
     * @return {Boolean}
     */
    isMultiSelectionEnabled: function () {
        return this.getSelectionModel().getSelectionMode() === 'MULTI';
    },

    /**
     * 
     * @param {CMDBuild.model.Base} record 
     */
    updateRowWithExpader: function (record) {
        var store = this.getStore();
        var storerecord = store.getById(record.getId());
        if (storerecord) {
            // get expander
            var expander = this.findPlugin("forminrowwidget");
            // get row
            var tableview = this.getView();
            var rownode = tableview.getNode(storerecord);
            var normalRow = Ext.fly(rownode);
            // get index
            var storeindex = store.indexOf(storerecord);

            // collapse row if expanded
            if (expander && normalRow && !normalRow.hasCls(expander.rowCollapsedCls) && storeindex !== -1) {
                expander.toggleRow(storeindex, storerecord);
            }

            // update data
            var newdata = record.getData();
            storerecord.updateDataFromObject(newdata);
            tableview.refreshNode(storerecord);

            // expand
            if (expander && storeindex !== -1) {
                expander.toggleRow(storeindex, storerecord);
            }
        }
    }
});