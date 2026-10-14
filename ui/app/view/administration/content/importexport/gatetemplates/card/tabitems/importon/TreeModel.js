Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.TreeModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-gatetemplates-card-tabitems-importon-tree',
    data: {
        name: 'CMDBuildUI'
    },
    stores: {
        classesStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description','enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        }
    }
});
