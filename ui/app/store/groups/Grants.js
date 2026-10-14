Ext.define('CMDBuildUI.store.groups.Grants', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.users.Grant'
    ],

    alias: 'store.grants',

    model: 'CMDBuildUI.model.users.Grant',
    pageSize: 0,
    autoLoad: false,
    autoDestroy:true
});