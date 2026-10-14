/**
 * @file CMDBuildUI.util.Chat
 * @module CMDBuildUI.util.Chat
 * @author PAT srl
 * @access public
 */

Ext.define('CMDBuildUI.util.Chat', {
    singleton: true,

    height: 400,
    width: 275,
    margin: 20,

    chatConversationsListId: 'chatConversationsList',

    /**
     * Open chat with user.
     *
     * @param {CMDBuildUI.model.messages.User} user
     */
    openChat: function (user) {
        var username = user.get('_id'),
            chatid = 'chatwindow-' + CMDBuildUI.util.Utilities.stringToHex(username),
            chatWindow = Ext.getCmp(chatid);
        if (!chatWindow) {
            // create chat window
            chatWindow = Ext.create({
                xtype: 'management-chat-window',
                id: chatid,
                viewModel: {
                    data: {
                        user: user
                    }
                },
                listeners: {
                    close: function () {
                        var item = Ext.Array.findBy(CMDBuildUI.util.Chat._openedchats, function (item, index) {
                            return item.user === username;
                        });
                        if (item) {
                            Ext.Array.remove(CMDBuildUI.util.Chat._openedchats, item);
                            CMDBuildUI.util.Chat.reorderChatWindows();
                        }
                    }
                }
            });

            // update opened chat list
            this._openedchats.push({
                user: user.get('_id'),
                chatwindow: chatWindow
            });

            // set position
            CMDBuildUI.util.Chat.checkSpace();
            CMDBuildUI.util.Chat.reorderChatWindows();
        }

        // set focus on send text input
        var textfield = chatWindow.down('#sendmsgtext');
        if (textfield) {
            textfield.focus();
        }

        return chatWindow;
    },

    /**
     * Get chat window.
     *
     * @param {String} username
     * @returns {CMDBuildUI.view.management.chat.Window}
     */
    getChatWindow: function (username) {
        var item = Ext.Array.findBy(this._openedchats, function (item, index) {
            return item.user === username;
        });
        return item ? item.chatwindow : null;
    },

    /**
     * Set chat window position based on its index.
     *
     * @param {CMDBuildUI.view.management.chat.Window} win
     * @param {Number} index
     */
    setPosition: function (win, index) {
        var w = window.innerWidth - (CMDBuildUI.util.Chat.width + CMDBuildUI.util.Chat.margin) * index,
            x = w - (CMDBuildUI.util.Chat.width + CMDBuildUI.util.Chat.margin),
            y = window.innerHeight - CMDBuildUI.util.Chat.height;
        win.setPosition(x, y);
    },

    /**
     * Reorder chat windows.
     */
    reorderChatWindows: function () {
        this._openedchats.forEach(function (chat, index) {
            CMDBuildUI.util.Chat.setPosition(chat.chatwindow, index);
        });
    },

    /**
     * Check space to add new chat.
     * @returns {Boolean}
     */
    checkSpace: function () {
        var chatwidth = CMDBuildUI.util.Chat.width + CMDBuildUI.util.Chat.margin,
            occupied = chatwidth * this._openedchats.length;
        if (this._openedchats.length && window.innerWidth < occupied) {
            this._openedchats[0].chatwindow.close();
            this.checkSpace();
        }
        return true;
    },

    /**
     * Returns the chat conversations list.
     *
     * @returns {CMDBuildUI.view.management.chat.ConversationsList}
     */
    getChatConversationsList: function () {
        return Ext.getCmp(this.chatConversationsListId);
    },

    privates: {
        _openedchats: []
    }
});
