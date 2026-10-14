Ext.define('CMDBuildUI.store.History', {
    extend: 'Ext.data.BufferedStore',

    alias: 'store.history',

    pageSize: 100,
    remoteFilter: true,
    remoteSort: true
});