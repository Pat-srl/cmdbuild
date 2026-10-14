Ext.define('CMDBuildUI.model.messages.Message', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        statuses: {
            archived: 'archived',
            new: 'new'
        }
    },

    fields: [{
        name: 'messageId',
        type: 'string'
    }, {
        name: 'status',
        type: 'string'
    }, {
        name: 'timestamp',
        type: 'date'
    }, {
        name: 'content',
        type: 'string'
    }, {
        name: 'target',
        type: 'string'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/sessions/current/messages',
        extraParams: {
            detailed: true
        }
    }
});
