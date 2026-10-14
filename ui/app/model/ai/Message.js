Ext.define('CMDBuildUI.model.ai.Message', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        contexts: {
            search: 'search',
            default: 'default'
        },
        types: {
            user: 'user',
            ai: 'ai',
            loading: 'loading'
        },

        addLoadingMessage: function (store) {
            store.add({
                timestamp: new Date(),
                type: this.types.loading,
                message: ''
            });
        },

        removeLoadingMessage: function (store) {
            const loadingRecord = store.findRecord('type', this.types.loading);
            if (loadingRecord) {
                store.remove(loadingRecord);
            }
        }
    },

    fields: [
        {
            name: 'timestamp',
            type: 'date',
            defaultValue: null
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'user'
        },
        {
            name: 'message',
            type: 'string'
        },
        {
            name: 'rawMessage',
            type: 'string'
        },
        {
            name: 'showSendAction',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'resources',
            defaultValue: []
        }
    ]
});
