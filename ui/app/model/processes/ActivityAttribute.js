Ext.define('CMDBuildUI.model.processes.ActivityAttribute', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'mandatory',
        type: 'boolean'
    }, {
        name: 'writable',
        type: 'boolean'
    }, {
        name: 'action',
        type: 'boolean'
    }],

    belongsTo: 'CMDBuildUI.model.processes.Activity'
});
