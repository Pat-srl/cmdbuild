Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-globalsearchfield-globalsearchpanel',

    stores: {
        searchPanelStore: {
            type: 'tree',
            defaultRootProperty: 'items',
            fields: [{
                name: 'text',
                mapping: 'description'
            }, {
                name: 'iconCls',
                defaultValue: 'empty'
            }, {
                name: 'leaf',
                defaultValue: false
            }, {
                name: 'expanded',
                defaultValue: true
            }, {
                name: 'isChild',
                type: 'boolean'
            }, {
                name: 'subType',
                defaultValue: 'empty'
            },
                'name',
                'description',
                'type',
                'items',
                'url', 'tab'
            ],
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            root: {
                expanded: true,
                items: '{responseData}'
            }
        }
    }
});