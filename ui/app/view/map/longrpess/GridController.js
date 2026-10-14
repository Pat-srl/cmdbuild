Ext.define('CMDBuildUI.view.map.longpress.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-longpress-grid',

    control: {
        '#': {
            itemdblclick: 'onItemDblClick'
        }
    },

    /**
     * 
     * @param {Ext.view.View} view 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} item 
     * @param {Number} index 
     * @param {Ext.event.Event} e 
     * @param {eOpts} eOpts 
     */
    onItemDblClick: function (view, record, item, index, e, eOpts) {
        var className = record.get('_type'),
            cardId = record.getId();

        this.cardRedirect(className, cardId);
    },

    /**
     * 
     * @param {Ext.view.Table} view 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Object} item 
     * @param {Event} e 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} row 
     */
    onActionColumnClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var className = record.get('_type'),
            cardId = record.getId();

        this.cardRedirect(className, cardId);
    },

    privates: {

        /**
         * 
         * @param {String} className 
         * @param {String} cardId 
         */
        cardRedirect: function (className, cardId) {
            CMDBuildUI.util.Utilities.redirectTo(
                Ext.String.format(
                    "classes/{0}/cards/{1}",
                    className,
                    cardId
                )
            );

            CMDBuildUI.util.Utilities.closePopup(CMDBuildUI.view.map.longpress.Grid.longpressPopupId);
        }

    }

});
