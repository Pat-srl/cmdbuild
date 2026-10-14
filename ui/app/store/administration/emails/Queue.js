Ext.define('CMDBuildUI.store.administration.emails.Queue', {
    extend: 'CMDBuildUI.store.Base',  
    requires:['CMDBuildUI.model.emails.Email'],
    alias: 'store.administration-email-queue',
    model: 'CMDBuildUI.model.emails.Email',
    autoLoad: false,
    fields: ['from', 'to', 'subject', 'status', 'date'],
    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.Config.baseUrl + '/email/queue/outgoing'
    },
    pageSize: 0
});