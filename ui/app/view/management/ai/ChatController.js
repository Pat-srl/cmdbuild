Ext.define('CMDBuildUI.view.management.ai.ChatController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-ai-chat',
    mixins: ['CMDBuildUI.mixins.ai.Chat'],

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#sendmsgtextarea': {
            specialkey: 'onSendMsgTextAreaSpecialKey'
        },
        '#sendmsgbtn': {
            click: 'onSendMsgBtnClick'
        },
        '#newchatbtn': {
            click: 'onNewChatBtnClick'
        },
        '#closeWindow': {
            click: 'onCloseWindowClick'
        },
        '#chatgrid': {
            openurl: 'onOpenUrl',
            viewcard: 'onViewCardClick',
            viewattachment: 'onViewAttachmentClick',
            copyanswer: 'onCopyAnswerClick',
            itemadd: 'onItemAdd'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.management.ai.Chat} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const parent = view.up();
        view.setMaxWidth(parent.getWidth() * 0.5);
    },

    /**
     * On send message button click.
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onSendMsgBtnClick: function (btn, eOpts) {
        const vm = btn.lookupViewModel();
        let text = vm.get('msg.text');

        if (Ext.isEmpty(text)) {
            return;
        }

        // encode html
        text = Ext.String.htmlEncode(text);
        text = Ext.util.Format.nl2br(text);

        const store = vm.get('messagesStore');
        store.add({
            timestamp: new Date(),
            type: CMDBuildUI.model.ai.Message.types.user,
            message: text
        });

        const rawQuery = Ext.String.trim(vm.get('msg.text'));
        vm.set('msg.text', '');

        this.makeRequest(vm, rawQuery);
    },

    /**
     * Handle Enter / Shift+Enter on the textarea.
     *
     * @param {Ext.form.field.TextArea} field
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onSendMsgTextAreaSpecialKey: function (field, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        if (vm.get('loading')) {
            return;
        }
        if (e.getKey() === e.ENTER && !e.shiftKey) {
            me.onSendMsgBtnClick(field);
            e.stopEvent();
        }
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onCloseWindowClick: function (tool, e, owner, eOpts) {
        CMDBuildUI.util.Navigation.getMainContainer().getViewModel().set('hideChatBot', true);
    },

    /**
     * Make request to PowerFinder backend.
     *
     * @param {Ext.app.ViewModel} vm
     * @param {String} question
     */
    makeRequest: function (vm, question) {
        const store = vm.get('messagesStore');
        vm.set('loading', true);
        CMDBuildUI.model.ai.Message.addLoadingMessage(store);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Ai.getAiUrl(),
            method: 'POST',
            jsonData: JSON.stringify({ query: question, context: CMDBuildUI.model.ai.Message.contexts.default }),
            success: function (response) {
                const responseData = JSON.parse(response.responseText).data;
                const answer = Ext.util.Format.nl2br((responseData && responseData.response) || '');
                const resources = (responseData && responseData.resources) || [];

                CMDBuildUI.model.ai.Message.removeLoadingMessage(store);
                store.add({
                    timestamp: new Date(),
                    type: CMDBuildUI.model.ai.Message.types.ai,
                    message: answer,
                    rawMessage: responseData && responseData.response,
                    resources: resources
                });

                vm.set('loading', false);
            },
            failure: function () {
                CMDBuildUI.model.ai.Message.removeLoadingMessage(store);
                store.add({
                    timestamp: new Date(),
                    type: CMDBuildUI.model.ai.Message.types.ai,
                    message: CMDBuildUI.locales.Locales.ai.erroranswer,
                    rawMessage: CMDBuildUI.locales.Locales.ai.erroranswer
                });
                vm.set('loading', false);
            },
            error: function () {
                CMDBuildUI.model.ai.Message.removeLoadingMessage(store);
                store.add({
                    timestamp: new Date(),
                    type: CMDBuildUI.model.ai.Message.types.ai,
                    message: CMDBuildUI.locales.Locales.ai.erroranswer,
                    rawMessage: CMDBuildUI.locales.Locales.ai.erroranswer
                });
                vm.set('loading', false);
            }
        });
    },

    /**
     * Start a brand new chat (immediate, without confirmation).
     */
    onNewChatBtnClick: function (btn, eOpts) {
        const vm = this.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.ai.clearconversation,
            CMDBuildUI.locales.Locales.ai.clearconversationmessage,
            function (buttonId) {
                if (buttonId === 'yes') {
                    if (vm) {
                        vm.get('messagesStore').removeAll();
                        vm.set('loading', false);
                    }
                }
            }
        );
    }
});
