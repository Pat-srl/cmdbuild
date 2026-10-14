
Ext.define('CMDBuildUI.view.graph.GraphContainer', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.graph.GraphContainerController',
        'CMDBuildUI.view.graph.GraphContainerModel',
        'CMDBuildUI.graph.threejs.SceneUtils'
    ],

    alias: 'widget.graph-graphcontainer',
    controller: 'graph-graphcontainer',
    viewModel: {
        type: 'graph-graphcontainer'
    },

    layout: 'border',

    items: [{
        xtype: 'panel',
        region: 'west',
        width: '33%',
        layout: 'fit',
        split: true,
        collapsible: false,
        items: [{
            xtype: 'graph-tab-tabpanel',
            ui: 'managementlighttabpanel'
        }]
    }, {
        xtype: 'graph-canvas-canvaspanel',
        region: 'center',
        layout: 'fit'
    }]
});