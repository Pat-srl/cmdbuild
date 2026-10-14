Ext.define('CMDBuildUI.view.graph.canvas.CanvasPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-canvaspanel',

    control: {
        '#': {
            afterrender: 'onAfterRenderPanel'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.graph.canvas.CanvasPanel} panel 
     * @param {Object} eOpts 
     */
    onAfterRenderPanel: function (panel, eOpts) {
        const div = document.getElementById('cy');
        div.style.height = 'inherit';
    }
});