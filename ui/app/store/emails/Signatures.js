Ext.define('CMDBuildUI.store.emails.Signatures', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.signatures',

    model: 'CMDBuildUI.model.emails.Signature',

    sorters: ['code'],
    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});