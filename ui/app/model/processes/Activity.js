Ext.define('CMDBuildUI.model.processes.Activity', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_definition',
        type: 'string',
        persist: false,
        critical: false
    },{
        name: 'description',
        type: 'string'
    }, {
        name: 'writable',
        type: 'boolean'
    }, {
        name: 'instructions',
        type: 'string'
    }, {
        name: 'attributes',
        type: 'auto'
    }, {
        name: 'formStructure',
        type: 'auto',
        critical: true
    },{
        name: 'addFormBtnSorter',
        calculate: function(data){
            return data._definition === 'DUMMY_TASK_FOR_CLOSED_PROCESS' ? 'zzzzzzzz': data.description;
        }
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.processes.ActivityAttribute',
        name: 'attributes',
        associationKey: 'attributes'
    }, {
        model: 'CMDBuildUI.model.WidgetDefinition',
        name: 'widgets',
        associationKey: 'widgets'
    }],

    proxy: {
        type: 'baseproxy'
    }
});
