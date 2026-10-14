Ext.define('CMDBuildUI.model.administration.NodeStatus', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'active_session',
        type: 'string'
    }, {
        name: 'date',
        type: 'number',
        calculate: function (data) {
            return new Date(data.timestamp).getTime();
        }
    }, {
        name: 'disk_free',
        type: 'string'
    }, {
        name: 'disk_total',
        type: 'string'
    }, {
        name: 'disk_used',
        type: 'string'
    }, {
        name: 'java_memory_free',
        type: 'string'
    }, {
        name: 'java_memory_max',
        type: 'string'
    }, {
        name: 'java_memory_total',
        type: 'string'
    }, {
        name: 'java_memory_used',
        type: 'string'
    }, {
        name: 'process_memory_used',
        type: 'string'
    }, {
        name: 'system_load',
        type: 'string'
    }, {
        name: 'system_memory_free',
        type: 'string'
    }, {
        name: 'system_memory_total',
        type: 'string'
    }, {
        name: 'system_memory_used',
        type: 'string'
    }, {
        name: 'timestamp',
        type: 'string'
    }]

});