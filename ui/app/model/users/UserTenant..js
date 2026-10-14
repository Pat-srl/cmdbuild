
Ext.define('CMDBuildUI.model.users.UserTenant', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: '_description_translation',
        type: 'string'
    }, {
        name: 'active',
        type: 'boolean'
    }],

    proxy: {
        type: 'memory'
    }
});


