Ext.define('CMDBuildUI.model.processes.ProcessVersion', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'provider',
        type: 'string',
        critical: true
    }, {
        name: 'version',
        type: 'number',
        critical: true
    }, {
        name: 'plainId',
        type: 'string',
        critical: true
    }, {
        name: 'default',
        type: 'boolean',
        critical: true
    }, {
        name: 'lastUpdate',
        type: 'date',
        critical: true
    }],

    belongsTo: 'CMDBuildUI.model.processes.Process'

    
});
