Ext.define('CMDBuildUI.view.history.ItemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.history-item',

    data: {
        record: null
    },

    formulas: {
        title: function (get) {
            return null; // return null to hide header
        }
    }

});
