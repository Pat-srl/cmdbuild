Ext.define('CMDBuildUI.model.administration.BusDescriptor', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'code',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'notes',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'version',
        type: 'number',
        persist: true,
        critical: true
    }, {
        name: 'enabled',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'valid',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'disabled',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'params',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'tag',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'data',
        type: 'string',
        persist: true,
        critical: true
    }],


    proxy: {
        type: 'baseproxy',
        url: '/etl/configs',
        extraParams: {
            detailed: true
        }
    }
});