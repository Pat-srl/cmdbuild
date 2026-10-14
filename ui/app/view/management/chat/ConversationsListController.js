Ext.define('CMDBuildUI.view.management.chat.ConversationsListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-chat-conversationslist',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemclick: 'onItemClick',
            newmessagesent: 'onNewMessageSent',
            newmessagereceived: 'onNewMessageReceived',
            newmessagesread: 'onNewMessagesRead'
        },
        '#addconversation': {
            click: 'onAddConversationClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.management.chat.ConversationsList} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.mon(Ext.ComponentQuery.query('viewport')[0], 'resize', function () {
            CMDBuildUI.util.Chat.checkSpace();
            CMDBuildUI.util.Chat.reorderChatWindows();
        });
    },

    /**
     * On user click.
     *
     * @param {CMDBuildUI.view.management.chat.ConversationsList} view
     * @param {CMDBuildUI.model.messages.User} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Event} e
     * @param {Object} eOpts
     */
    onItemClick: function (view, record, item, index, e, eOpts) {
        CMDBuildUI.util.Chat.openChat(record);

        var cl = CMDBuildUI.util.Chat.getChatConversationsList();
        cl.fireEvent('newmessagesread', cl, record.get('username'));
    },

    /**
     * @param {CMDBuildUI.model.messages.Message} message
     */
    onNewMessageReceived: function (view, message) {
        var store = view.lookupViewModel().get('conversations'),
            username = message.get('sourceName'),
            conversation = store.findRecord('username', username);
        if (conversation) {
            var chatwindow = CMDBuildUI.util.Chat.getChatWindow(username),
                hasfocus = chatwindow ? chatwindow.containsFocus : false;
            conversation.set({
                _hasNewMessages: !hasfocus,
                _lastMessageTimestamp: message.get('timestamp')
            });

            if (!hasfocus) {
                CMDBuildUI.util.Utilities.playAlertNotificationSound();
            }

            if (chatwindow) {
                chatwindow.addNewMessage(message);
            }
        } else {
            store.load();
        }
    },

    /**
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onAddConversationClick: function (tool, e, owner, eOpts) {
        if (!tool._menu) {
            tool._menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                alignTarget: tool.el,
                defaultAlign: 'l-r',
                items: [{
                    xtype: 'management-chat-userslist',
                    width: owner.getWidth(),
                    height: Ext.max([owner.getHeight(), owner.height])
                }]
            });
        } else {
            tool._menu.show();
        }
    },

    /**
     * @param {CMDBuildUI.view.management.chat.ConversationsList} view
     * @param {String} username
     */
    onNewMessageSent: function (view, username) {
        var store = view.lookupViewModel().get('conversations'),
            conversation = store.findRecord('username', username);
        if (conversation) {
            store.setRemoteSort(false);
            conversation.set({
                _lastMessageTimestamp: new Date()
            });
            store.setRemoteSort(true);
        } else {
            store.load();
        }
    },

    /**
     * @param {CMDBuildUI.view.management.chat.ConversationsList} view
     * @param {String} username
     */
    onNewMessagesRead: function (view, username) {
        var store = view.lookupViewModel().get('conversations'),
            conversation = store.findRecord('username', username);
        if (conversation) {
            conversation.set({
                _hasNewMessages: false
            });
        }
    },

    /**
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.User[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onConversationsStoreLoad: function (store, records, successful, operation, eOpts) {
        this.updateUnreadCounter(store);
    },

    /**
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.User} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {Object} details
     * @param {Object} eOpts
     */
    onConversationsStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        this.updateUnreadCounter(store);
    },

    privates: {
        updateUnreadCounter: function (store) {
            this.getViewModel().set('chatconversations.count', store.query('_hasNewMessages', true).getCount());
        }
    }
});