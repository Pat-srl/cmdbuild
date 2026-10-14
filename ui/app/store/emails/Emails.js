Ext.define('CMDBuildUI.store.emails.Emails', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.emails',

    model: 'CMDBuildUI.model.emails.Email',

    sorters: ['date'],
    groupField: 'status',
    autoLoad: false,
    pageSize: 0 // disable pagination
});