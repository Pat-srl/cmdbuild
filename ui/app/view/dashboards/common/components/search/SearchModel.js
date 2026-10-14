Ext.define('CMDBuildUI.view.dashboards.common.components.search.SearchModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dashboards-components-search-search',

    data: {
        model: null,

        search: {
            value: null,
            disabled: true,
            hidden: false
        }
    }
});
