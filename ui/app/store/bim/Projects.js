Ext.define('CMDBuildUI.store.bim.Projects', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.model.bim.Projects'],
    alias: 'store.bim-projects',

    model: 'CMDBuildUI.model.bim.Projects',

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.Config.baseUrl + '/bim/projects'
    }
});