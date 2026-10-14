Ext.define('CMDBuildUI.view.management.chat.WindowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-chat-window',

    control: {
        '#': {
            focusenter: 'onFocusEnter'
        },
        '#chatgrid': {
            beforerender: 'onChatGridBeforeRender',
            beforedestroy: 'onChatGridBeforeDestroy'
        },
        '#sendmsgtext': {
            specialkey: 'onSendMsgTextSpecialKey'
        },
        '#sendmsgbtn': {
            click: 'onSendMsgBtnClick'
        }
    },

    /**
     * @param {Ext.grid.Panel} grid
     * @param {Object} eOpts
     */
    onChatGridBeforeRender: function (grid, eOpts) {
        var scroller = grid.getView().getScrollable();
        grid.mon(scroller, 'scrollend', this.onChatGridScrollEnd, grid);

        // initialize refresh task
        grid._refreshtask = {
            run: function () {
                grid.getView().refresh();
            },
            interval: 15000 // every 15 seconds
        };
        Ext.TaskManager.start(grid._refreshtask);
    },

    /**
     * @param {Ext.grid.Panel} grid
     * @param {Object} eOpts
     */
    onChatGridBeforeDestroy: function (grid, eOpts) {
        Ext.TaskManager.stop(grid._refreshtask);
    },

    /**
     * @param {Ext.data.Store} store
     * @param {Ext.data.Operation} operation
     * @param {Object} eOpts
     */
    onServerMessagesBeforeLoad: function (store, operation, eOpts) {
        // show loader
        this.getViewModel().set('titleloader', CMDBuildUI.util.Navigation.defaultManagementContentTitle);
    },

    /**
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.Message} messages
     * @param {Boolean} successful
     * @param {Ext.data.Operation} operation
     * @param {Object} eOpts
     */
    onServerMessagesLoad: function (messagesstore, messages, successful, operation, eOpts) {
        var vm = this.getViewModel(),
            store = vm.get('messages');
        messages.forEach(function (message) {
            store.addSorted(message);
            if (message.get('_isNew')) {
                message.set('status', CMDBuildUI.model.messages.Message.statuses.archived);
            }
        });
        if (messagesstore.loadCount === 1) {
            this.getView().scrollToBottom();
        }
        if (messagesstore.getTotalCount() < messagesstore.getPageSize()) {
            messagesstore._loadedallmessages = true;
        }

        // synchronyze the store
        messagesstore.sync();

        // remove loader
        vm.set('titleloader', null);
    },

    /**
     * @param {Ext.form.field.TextArea} field
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onSendMsgTextSpecialKey: function (field, e, eOpts) {
        var me = this;
        if (e.getKey() == e.ENTER) {
            if (e.ctrlKey) {
                field.setValue(field.getValue() + '\n');
            } else {
                me.onSendMsgBtnClick(field);
                e.stopEvent();
            }
        }
    },

    /**
     * On send message button click.
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onSendMsgBtnClick: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var vm = btn.lookupViewModel(),
            text = vm.get('msg.text');

        if (Ext.isEmpty(text)) {
            return;
        }

        // encode html
        text = Ext.String.htmlEncode(text);
        text = Ext.util.Format.nl2br(text);

        var message = Ext.create('CMDBuildUI.model.messages.Message', {
            content: text,
            target: vm.get('user.username'),
            timestamp: new Date(),
            type: 'outgoing'
        });

        vm.get('messages').addSorted(message);
        this.getView().scrollToBottom();
        message.save({
            callback: function (record, operation, success) {
                // fire new message sent on conversations list
                var cl = CMDBuildUI.util.Chat.getChatConversationsList();
                cl.fireEvent('newmessagesent', cl, record.get('target'));
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });

        vm.set('msg.text', '');
    },

    /**
     * @param {Ext.scroll.Scroller} scroller
     * @param {Number} x
     * @param {Number} y
     * @param {Object} eOpts
     */
    onChatGridScrollEnd: function (scroller, x, y, eOpts) {
        if (y === 0) {
            var grid = this,
                vm = grid.lookupViewModel(),
                messages = vm.get('messages'),
                serverMessages = vm.get('serverMessages'),
                firstmessage = messages.getAt(0);

            if (!this._messagesfilter) {
                this._messagesfilter = Ext.JSON.decode(serverMessages.getAdvancedFilter().encode());
            }

            messages.query('isfirst', true).getRange().forEach(function (r) {
                r.set('isfirst', false);
            });

            if (!serverMessages._loadedallmessages && firstmessage) {
                var newfilter = Ext.clone(this._messagesfilter);
                newfilter.attribute.and.push({
                    simple: {
                        attribute: 'timestamp',
                        operator: CMDBuildUI.util.helper.FiltersHelper.operators.less,
                        value: [firstmessage.get('timestamp')]
                    }
                });

                firstmessage.set('isfirst', true);

                serverMessages.getAdvancedFilter().addCustomFilter(newfilter);
                serverMessages.load(function () {
                    grid.ensureVisible(firstmessage);
                });
            }
        }
    },

    /**
     * @param {CMDBuildUI.view.management.chat.Window} win 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onFocusEnter: function (win, e, eOpts) {
        var vm = win.lookupViewModel(),
            store = vm.get("messages");
        store.query('_isNew', true).getRange().forEach(function (m) {
            m.set({
                status: CMDBuildUI.model.messages.Message.statuses.archived,
                _isNew: false
            });
        });
        if (store.needsSync) {
            // fire new messages read on conversations list
            var cl = CMDBuildUI.util.Chat.getChatConversationsList();
            cl.fireEvent('newmessagesread', cl, vm.get('user.username'));
        }
        store.sync();

        vm.set('hasnewmessages', false);
    }
});