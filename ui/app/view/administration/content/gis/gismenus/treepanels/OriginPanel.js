Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.OriginPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.gismenus.treepanels.OriginPanelController',
        'CMDBuildUI.view.administration.content.gis.gismenus.treepanels.OriginPanelModel'
    ],

    alias: 'widget.administration-content-gismenus-treepanels-originpanel',
    controller: 'administration-content-gismenus-treepanels-originpanel',
    viewModel: {
        type: 'administration-content-gismenus-treepanels-originpanel'
    },

    itemId: 'treepanelorigin',
    flex: 1,
    cls: 'tree-noborder',
    ui: 'administration-navigation-tree',
    reference: 'menuTreeViewOrigin',
    align: 'start',
    scrollable: true,
    useArrows: true,
    expanded: true,

    bind: {
        store: '{originStore}'
    },

    rootVisible: false,
    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'objectdescription',
        flex: 0.8,
        align: 'start'
    }],

    viewConfig: {
        plugins: {
            id: 'treeviewdragdroporigin',
            ptype: 'treeviewdragdrop',
            ddGroup: 'TreeDD',
            nodeHighlightOnRepair: false,
            appendOnly: false,
            sortOnDrop: false,
            containerScroll: true,
            allowContainerDrops: true,
            dragZone: {
                animRepair: false
            }
        }
    },
    listeners: {
        beforedrop: 'onBeforeDrop'
    }
});
