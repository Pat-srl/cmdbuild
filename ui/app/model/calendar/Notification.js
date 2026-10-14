Ext.define('CMDBuildUI.model.calendar.Notification', {
    extend: 'Ext.data.Model',

    fields: [{
        name: "templateId",
        type: "string",
        defaultValue: ''
    }, {
        name: "reportId",
        type: "string"
    }],

    validators: ['presence'],
    belongsTo: 'CMDBuildUI.model.calendar.Trigger',
    
    proxy: {
        type: 'memory'
    }
});