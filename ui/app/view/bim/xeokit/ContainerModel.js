Ext.define('CMDBuildUI.view.bim.xeokit.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.bim-xeokit-container',

    data: {
        eye: null,
        look: null,
        up: [0, 1, 0],
        vision2D: false,
        cutEmpty: true,
        duration: 2,
        enabledTabs: {
            properties: false,
            card: false
        },
        idBimCanvas: null,
        modelsData: {}
    },

    stores: {
        objectsTreeStore: {
            type: 'tree',
            root: {
                text: CMDBuildUI.locales.Locales.bim.tree.root,
                children: []
            },
            proxy: 'memory'
        },

        layersStore: {
            proxy: 'memory',
            sorters: {
                direction: 'ASC',
                property: 'text'
            }
        }
    }

});