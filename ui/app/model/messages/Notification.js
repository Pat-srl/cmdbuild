Ext.define('CMDBuildUI.model.messages.Notification', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'status',
        type: 'string'
    }, {
        name: 'timestamp',
        type: 'date'
    }, {
        name: 'subject',
        type: 'string'
    }, {
        name: 'content',
        type: 'string'
    }, {
        name: 'meta',
        type: 'auto'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/sessions/current/messages',
        extraParams: {
            detailed: true
        }
    }
});
