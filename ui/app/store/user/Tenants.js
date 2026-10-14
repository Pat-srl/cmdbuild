Ext.define('CMDBuildUI.store.users.Tenants', {
    extend: 'Ext.data.Store',

    model : 'CMDBuildUI.model.users.Tenant',
    autoLoad: false,

    proxy: {
        type: 'memory'
    }
});