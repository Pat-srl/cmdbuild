Ext.define('CMDBuildUI.store.processes.ProcessVersions', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.processes.ProcessVersion'
    ],

    alias: 'store.process-versions',
    storeId: 'process-versions',

    model: 'CMDBuildUI.model.processes.ProcessVersion',

    sorters: ['version'],
    pageSize: 0
});