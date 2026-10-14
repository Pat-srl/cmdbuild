Ext.define('CMDBuildUI.model.messages.User', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'username',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: '_hasMessages',
        type: 'boolean'
    }, {
        name: '_hasNewMessages',
        type: 'boolean'
    }, {
        name: '_newMessagesCount',
        type: 'number'
    }, {
        name: '_lastMessageTimestamp',
        type: 'date'
    }, {
        name: 'icon',
        type: 'string'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/sessions/current/peers'
    }
});
