
Ext.define('CMDBuildUI.view.graph.canvas.CanvasPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.graph.canvas.CanvasPanelController'
    ],

    controller: 'graph-canvas-canvaspanel',

    alias: 'widget.graph-canvas-canvaspanel',
    html: '<div id="cy"></div>',

    dockedItems: [{
        xtype: 'graph-canvas-topmenu-topmenu',
        dock: 'top'
    }, {
        xtype: 'graph-canvas-bottommenu-canvasmenu',
        dock: 'bottom'
    }]
});