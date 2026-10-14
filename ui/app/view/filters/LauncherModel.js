Ext.define('CMDBuildUI.view.filters.LauncherModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-launcher',

    data: {
        appliedfilter: {
            id: null,
            description: null
        }
    },

    formulas: {
        showClearBtn: {
            bind: {
                filterid: '{appliedfilter.id}'
            },
            get: function (data) {
                return !Ext.isEmpty(data.filterid);
            }
        }
    }

});
