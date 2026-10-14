Ext.define('CMDBuildUI.store.importexports.Gates', {
    requires: ['CMDBuildUI.model.importexports.Gate'],
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.importexports-gates',

    model: 'CMDBuildUI.model.importexports.Gate',

    proxy: {
        type: 'baseproxy',
        url: '/etl/gates',
        extraParams: {
            detailed: true
        }
    },
    advancedFilter: {
        attributes: {
            _has_single_handler: [{
                operator: 'equal',
                value: [true]
            }],
            _handler_type: [{
                operator: 'in',
                value: ['cad', 'database', 'ifc']
            }]
        }
    },

    sorters: ['description'],

    autoLoad: false,
    pageSize: 0 // disable pagination
});