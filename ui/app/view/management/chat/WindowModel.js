Ext.define('CMDBuildUI.view.management.chat.WindowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-chat-window',

    data: {
        titleloader: null,
        hasnewmessages: false,
        msg: {
            text: null
        }
    },

    formulas: {
        titlenewmessage: {
            bind: {
                newmsg: '{hasnewmessages}'
            },
            get: function(data) {
                return data.newmsg ? '&bull;' : null;
            }
        }
    },

    stores: {
        /**
         * Chat messages
         */
        messages: {
            model: 'CMDBuildUI.model.messages.Message',
            proxy: 'memory',
            sorters: [{
                property: 'timestamp',
                direction: 'ASC'
            }],
            autoDestroy: true
        },
        /**
         * Server messages
         */
        serverMessages: {
            model: 'CMDBuildUI.model.messages.Message',
            proxy: {
                type: 'baseproxy',
                url: '/sessions/current/messages',
                extraParams: {
                    detailed: true
                }
            },
            sorters: [{
                property: 'timestamp',
                direction: 'DESC'
            }],
            advancedFilter: {
                custom: {
                    attribute: {
                        and: [{
                            or: [{
                                simple: {
                                    attribute: 'target',
                                    operator: 'equal',
                                    value: ['{user.username}']
                                }
                            }, {
                                simple: {
                                    attribute: 'sourceName',
                                    operator: 'equal',
                                    value: ['{user.username}']
                                }
                            }]
                        }, {
                            simple: {
                                attribute: 'sourceType',
                                operator: 'equal',
                                value: 'user'
                            }
                        }]
                    }
                }
            },
            remoteSort: true,
            autoLoad: true,
            autoDestroy: true,
            listeners: {
                beforeload: 'onServerMessagesBeforeLoad',
                load: 'onServerMessagesLoad'
            }
        }
    }
});