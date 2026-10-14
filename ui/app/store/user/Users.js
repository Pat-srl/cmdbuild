Ext.define('CMDBuildUI.store.users.Users', {
    extend: 'Ext.data.BufferedStore',
    model: "CMDBuildUI.model.users.User",
    alias: 'store.users',
    storeId: 'users',

    proxy: {
        url: '/users',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    },
    autoLoad: false,
    // autoDestroy: false,
    remoteFilter: true,
    remoteSort: true,
    pageSize: 100,
    sorters: [{
        property: 'Username',
        direction: 'ASC'
    }]
});