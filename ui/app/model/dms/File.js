Ext.define('CMDBuildUI.model.dms.File', {
    extend: 'Ext.data.Model',

    statics: {
        statuses: {
            empty: 'empty',
            error: 'error',
            extensionNotAllowed: 'extensionNotAllowed',
            loaded: 'loaded',
            ready: 'ready',
            tooLarge: 'tooLarge',
            fileAlreadyPresent: 'fileAlreadyPresent'
        }
    },

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'size',
        type: 'string'
    }, {
        name: 'file'
    }, {
        name: 'status',
        type: 'string'
    }],

    proxy: {
        type: 'memory'
    }
});