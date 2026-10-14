Ext.define('CMDBuildUI.view.graph.GraphContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-graphcontainer',

    data: {
        /*
        * this is the starting node 
        */
        node: {
            _type: null,
            _id: null,
            _code: null,
            _description: null
        },
        /**
         * @type {ListNodeDataStructure}
         */
        memoryNodeDataStructure: null,
        cy: null,
        /**
         * this is the selected node
         * Contains an array of objects
         * {[Object]}
         *  {
         *   id: null,
         *   type: null
         *  }
         */
        selectedNode: null,
        navTreesLoaded: false,
        /**
         * This object maps the nodes id to the ids of its compound nodes
         * {
         *  nodeId: 'Compound_node_...',
         *  nodeId2: 'Compound_node_...'
         * }
         */
        _map_Node_CompoundName: {},
        pointerExternalCanvas: false
    },

    stores: {
        relationStore: {
            model: "CMDBuildUI.model.domains.Relation",
            id: 'relationStore',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            listeners: {
                clear: 'onRelationStoreClear'
            }
        }
    }
});