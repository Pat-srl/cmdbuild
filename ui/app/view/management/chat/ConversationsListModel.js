Ext.define('CMDBuildUI.view.management.chat.ConversationsListModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-chat-conversationslist',

    formulas: {
        panelTitle: {
            bind: {
                newconv: '{chatconversations.count}'
            },
            get: function (data) {
                var title = CMDBuildUI.locales.Locales.chat.title;
                if (data.newconv) {
                    title += ' <span class="chat-title-counter">' + data.newconv + '</span>';
                }
                return title;
            }
        },

        hidechat: {
            get: function() {
                return !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.chat.enabled);
            }
        }
    },

    stores: {
        conversations: {
            model: 'CMDBuildUI.model.messages.User',
            advancedFilter: {
                attributes: {
                    _hasMessages: [{
                        operator: 'equal',
                        value: [true]
                    }]
                }
            },
            sorters: [{
                property: '_lastMessageTimestamp',
                direction: 'DESC'
            }],
            listeners: {
                load: 'onConversationsStoreLoad',
                update: 'onConversationsStoreUpdate'
            },
            remoteSort: true,
            autoLoad: '{!hidechat}',
            autoDestroy: true
        }
    }
});