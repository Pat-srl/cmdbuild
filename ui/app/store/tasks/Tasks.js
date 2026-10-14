Ext.define('CMDBuildUI.store.tasks.Tasks', {
    extend: 'CMDBuildUI.store.Base',
    model: "CMDBuildUI.model.tasks.Task",
    alias: 'store.tasks',
    storeId: 'tasks',

    autoLoad: false,
    autoDestroy: true,
    // remoteFilter: true,
    // remoteSort: true,
    pageSize: 0,
    sorters: [{
        property: 'code',
        direction: 'ASC'
    },{
        property: 'description',
        direction: 'ASC'
    }]
});