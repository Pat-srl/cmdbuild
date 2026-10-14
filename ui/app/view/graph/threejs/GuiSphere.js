Ext.define('CMDBuildUI.graph.threejs.GuiSphere', {
    singleton: true,

    /**
     * @param {Scene} scene the THREE scene
     * @param {cytoscape} cy the array containing the new cytoscape nodes  
     */
    run: function (scene, cy, camera) {
        this.visited = {};
        this.scene = scene;
        this.color = 0;

        var cyRoot = cy.nodes('[isRoot]').toArray()[0];
        if (!cyRoot) return;

        this._recursiveChildVisit(cyRoot);

        CMDBuildUI.graph.threejs.SceneUtils.setCameraTarget();
        this.visited = {};
        delete this.color;
    },

    privates: {
        /**
         * 
         * @param {Element} cyNode the cytoscape node being analized
         */
        _recursiveChildVisit: function (cyNode) {
            const me = this;
            me.visited[cyNode.id()] = { visited: true };

            var outGoers = cyNode.outgoers('node').toArray();
            outGoers = me._removeVisited(outGoers);

            const nChilds = outGoers.length,
                stepRadius = 7;

            Ext.Array.forEach(outGoers, function (node, index, allnodes) {
                me.visited[node.id()] = { visited: true };
                const stepRadiusN = me.hasOutgoers([node]) && !Ext.isEmpty(Ext.Array.difference(node.outgoers('node').toArray(), outGoers)) ? stepRadius : (stepRadius / 2);

                if (!node) return;

                const phi = Math.acos(-1 + (2 * index) / (nChilds)),
                    theta = Math.sqrt((nChilds) * Math.PI) * phi;

                const threejsObj = node.glSprite; //TODO: modify in glSprite
                threejsObj.position.setFromSphericalCoords(stepRadiusN, phi, theta);
                threejsObj.position.addVectors(threejsObj.position, cyNode.glSprite.position);
            });

            CMDBuildUI.graph.threejs.SceneUtils.modifyLine(cyNode.glSprite);

            Ext.Array.forEach(outGoers, function (node, index, allnodes) {
                if (me.hasOutgoers([node])) {
                    me._recursiveChildVisit(node);
                }
            });
        },

        /**
         * This function removes from the given array the elements that has yet been visited
         * This information is stored in this.visited[$nodeId].visited
         * @param {[Element]} array of cy nodes
         * @returns {[Element]} the elements filtered
         */
        _removeVisited: function (array) {
            const me = this,
                dup = [];
            Ext.Array.forEach(array, function (el, index, allel) {
                if (!me.visited[el.id()] || !me.visited[el.id()].visited) {
                    dup.push(el);
                }
            });
            return dup;
        },

        /**
         * @param {[Element]} nodes array of cy nodes
         * @returns {Boolean} True if any element in nodes have outgoers
         */
        hasOutgoers: function (nodes) {
            return Ext.Array.findBy(nodes, function (item, index) {
                return item.outgoers('node').length != 0;
            });
        }
    }
});