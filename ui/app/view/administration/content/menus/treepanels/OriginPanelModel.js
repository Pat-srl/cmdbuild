Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.OriginPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-menus-treepanels-originpanel',
    stores: {
        originStore: {
            type: 'tree',
            model: 'CMDBuildUI.model.menu.MenuItem',
            root: {
                text: 'Root',
                expanded: true,
                children: []
            },
            proxy: {
                type: 'memory'
            },
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }],
            autoLoad: true,
            autoDestroy: true
        }
    }
});
