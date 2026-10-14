Ext.define('CMDBuildUI.view.dependencytree.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dependencytree-panel',

    data: {
        selectedNode: {},
        disableTabCard: true,
        isGroupNode: false,
        initialDepth: 1,
        hideRefreshButton: false
    }
});
