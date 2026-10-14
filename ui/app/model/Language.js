Ext.define('CMDBuildUI.model.Language', {
    extend: 'Ext.data.Model',

    fields: [{
            name: 'code',
            type: 'string'
        }, {
            name: 'description',
            type: 'string'
        }, {
            name: 'default',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: false
        }
    ],

    idProperty: "code",

    proxy: {
        url: '/configuration/languages/',
        type: 'baseproxy'
    }
});