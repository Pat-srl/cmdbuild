Ext.define('CMDBuildUI.store.domains.Domains', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.domains.Domain'
    ],

    alias: 'store.domains',

    model: 'CMDBuildUI.model.domains.Domain',
    pageSize: 0 // disable pagination

});