Ext.define('CMDBuildUI.model.setup.GovernanceData', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: ['Ext.data.validator.Presence'],

    fields: [
        {
            name: 'list',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
        },
        {
            name: 'read',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
        },
        {
            name: 'create',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
        },
        {
            name: 'update',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
        },
        {
            name: 'delete',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
        }
    ],

    proxy: {
        type: 'memory'
    }
});
