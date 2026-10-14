Ext.define('CMDBuildUI.store.base.SearchFilters', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.model.base.Filter'],
    alias: 'store.base-filters',

    model: 'CMDBuildUI.model.base.Filter',

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.Config.baseUrl + '/filters'
    },
    autoLoad: false,
    autoDestroy: true

});