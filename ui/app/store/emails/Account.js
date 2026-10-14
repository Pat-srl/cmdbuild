Ext.define('CMDBuildUI.store.emails.Accounts', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.accounts',

    model: 'CMDBuildUI.model.emails.Account',

    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});