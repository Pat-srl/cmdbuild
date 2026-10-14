
Ext.define('CMDBuildUI.model.users.UserGroup', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [
        {
            name: '_id',
            type: 'number',
            persist: false
            // critical: true // This field is allways sent to server even if it has hot changed
        }, {
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


