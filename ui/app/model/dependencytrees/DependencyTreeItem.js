Ext.define('CMDBuildUI.model.DependencyTreeItem', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [
        {
            name: 'direction',
            type: 'string'
        },
        {
            name: 'domain',
            type: 'string'
        },
        {
            name: 'filter',
            type: 'string'
        },
        {
            name: 'nodes',
            type: 'auto'
        },
        {
            name: 'parent',
            type: 'string'
        },
        {
            name: 'borderSource',
            type: 'string'
        },
        {
            name: 'borderColor',
            type: 'string'
        },
        {
            name: 'iconSource',
            type: 'string'
        },
        {
            name: 'iconFont',
            type: 'string'
        },
        {
            name: 'iconColorMode',
            type: 'string'
        },
        {
            name: 'iconColor',
            type: 'string'
        },
        {
            name: 'sourceLookup',
            type: 'string'
        },
        {
            name: 'sourceLookupAttribute',
            type: 'string'
        },
        {
            name: 'targetClass',
            type: 'string'
        }
    ],

    proxy: {
        type: 'memory'
    }
});
