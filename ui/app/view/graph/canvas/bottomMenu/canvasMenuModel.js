Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-canvas-bottommenu-canvasmenu',

    data: {
        nodesNumber: 0,
        edgesNumber: 0
    },

    formulas: {
        getNumberNodesAndEdges: {
            bind: '{cy}',
            get: function (cy) {
                if (cy) {
                    const me = this;

                    cy.on('add', function (event) {
                        const target = event.target;
                        if (target.isNode()) {
                            me.set('nodesNumber', cy.nodes().length);
                        } else if (target.isEdge()) {
                            me.set('edgesNumber', cy.edges().length);
                        }
                    });
                }
            }
        }
    }
});
