Ext.define('CMDBuildUI.components.grid.rowcounter.BufferedGridCounter', {
    extend: 'Ext.toolbar.Item',
    alias: 'widget.bufferedgridcounter',

    config: {
        store: null
    },

    publishes: ['store'], // make store bindable     

    autoEl: {
        'data-testid': 'buffered-grid-counter'
    },

    /**
     * Get the store on update
     * @param {Ext.data.Store} newValue
     * @param {Ext.data.Store} oldValue
     */
    updateStore: function (newValue, oldValue) {
        if (newValue) {
            newValue.addListener("load", this.onStoreLoaded, this);
        }
    },

    /**
     * Update totalCount when the store is loaded
     * @param {Ext.data.Store} store
     */
    onStoreLoaded: function (store) {
        if (store) {
            var totalCount = store.getTotalCount(),
                formatNumber = totalCount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, CMDBuildUI.util.helper.UserPreferences.getThousandsSeparator()),
                text = totalCount !== 1 ? CMDBuildUI.locales.Locales.common.grid.rows : CMDBuildUI.locales.Locales.common.grid.row;

            this.setHtml(Ext.String.format("{0} {1}", formatNumber, text));
        }
    }
});
