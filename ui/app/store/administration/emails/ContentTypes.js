Ext.define('CMDBuildUI.store.administration.emails.ContentTypes', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.emails-contenttypes',

    model: 'CMDBuildUI.model.base.ComboItem',
    data: [{
        label: 'HTML',
        value: 'text/html'
    },{
        label: 'TEXT',
        value: 'text/plain'
    }],

    proxy: {
        type: 'memory'
    }
});