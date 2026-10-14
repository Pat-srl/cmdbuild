Ext.define('CMDBuildUI.view.widgets.aiassistant.ChatController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-aiassistant-chat',
    mixins: ['CMDBuildUI.mixins.ai.Chat'],

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#chatgrid': {
            openurl: 'onOpenUrl',
            viewcard: 'onViewCardClick',
            viewattachment: 'onViewAttachmentClick',
            copyanswer: 'onCopyAnswerClick',
            sendanswer: 'onSendAnswerClick',
            itemadd: 'onItemAdd'
        }
    },

    onAfterRender: function () {
        if (this.getWidgetValue('PromptAuto')) {
            this.sendInitialPrompt();
        }
    },

    /**
     * Send first prompt to the AI service based on the initial 'Prompt' widget value.
     */
    sendInitialPrompt: function () {
        const basePrompt = this.getWidgetValue('Prompt');
        const prompt = this.resolveCardVariables(basePrompt);
        const sendChatPrompt = this.sendChatPrompt.bind(this);
        const vm = this.getViewModel();
        vm.set('disablePromptAction', true);

        if (!Ext.isEmpty(prompt)) {
            this.sendRequest(prompt, false, sendChatPrompt);
        } else {
            sendChatPrompt();
        }
    },

    /**
     * Send the current chat prompt to the AI service if chat is enabled and the prompt is not empty.
     */
    sendChatPrompt: function () {
        const promptChat = this.getWidgetValue('PromptChat');
        if (this.getViewModel().get('chatEnabled') && !Ext.isEmpty(promptChat)) {
            this.sendRequest(promptChat, true);
        }
    },

    /**
     * Sends a request to the AI service with the given question.
     * @param {String} question The question to be sent to the AI service.
     * @param {Boolean} addUserMessage Indicates if the message should be added as a user message.
     * @param {Function} [callback] Optional callback function to be executed after the request is completed.
     * @returns {void}
     */
    sendRequest: function (question, addUserMessage, callback) {
        const vm = this.getViewModel();
        const store = vm.get('messagesStore');
        if (vm.get('loading')) {
            return;
        }

        if (addUserMessage) {
            store.add({
                timestamp: new Date(),
                type: CMDBuildUI.model.ai.Message.types.user,
                message: Ext.util.Format.nl2br(Ext.String.htmlEncode(question)),
                rawMessage: question
            });
        }
        CMDBuildUI.model.ai.Message.addLoadingMessage(store);
        vm.set('loading', true);

        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Ai.getAiUrl(),
            method: 'POST',
            jsonData: JSON.stringify({ query: question, context: this.getWidgetValue('PromptContext') }),
            success: function (response) {
                const responseData = (Ext.decode(response.responseText) || {}).data || {};
                const rawAnswer = responseData.response || '';
                CMDBuildUI.model.ai.Message.removeLoadingMessage(store);
                store.add({
                    timestamp: new Date(),
                    type: CMDBuildUI.model.ai.Message.types.ai,
                    message: Ext.util.Format.nl2br(rawAnswer),
                    rawMessage: rawAnswer,
                    showSendAction: true,
                    resources: responseData.resources || []
                });
                vm.set('loading', false);
                Ext.callback(callback);
            },
            failure: function () {
                CMDBuildUI.model.ai.Message.removeLoadingMessage(store);
                store.add({
                    timestamp: new Date(),
                    type: CMDBuildUI.model.ai.Message.types.ai,
                    message: CMDBuildUI.locales.Locales.ai.erroranswer,
                    rawMessage: CMDBuildUI.locales.Locales.ai.erroranswer,
                    showSendAction: false
                });
                vm.set('loading', false);
                Ext.callback(callback);
            },
            scope: this
        });
    },

    /**
     * Sends the AI message to the specified attribute of the target object.
     * @param {Ext.data.Model} record  The record containing the AI message to be sent.
     * @param {HTMLElement} action  The action element that triggered the send operation.
     */
    onSendAnswerClick: function (record, action) {
        const vm = this.getViewModel();
        const attribute = this.getWidgetValue('PromptAttributeToCopy');
        const theTarget = vm.get('theTarget');
        const objName = theTarget.get('_type');
        const type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objName);
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objName, type);
        const conversationController = this.getView().down('management-ai-conversation').getController();
        obj.getAttributes().then(function (attributes) {
            const attributeRecord = attributes.findRecord('_id', attribute);
            if (!attributeRecord) {
                CMDBuildUI.util.Notifier.showWarningMessage(
                    Ext.String.format(CMDBuildUI.locales.Locales.ai.errors.attributenotfound, attribute)
                );
                return;
            }
            if (
                attributeRecord.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string ||
                attributeRecord.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
            ) {
                const attributeId = attributeRecord.get('_id');
                const updateValueFn = function () {
                    theTarget.set(attributeId, record.get('message') || record.get('rawMessage'));

                    conversationController.restoreAllActions();
                    action.disabled = true;
                    action.classList.add('ai-message-action-sent');
                    action.querySelector('span').classList.replace('fa-file-import', 'fa-check-circle');

                    CMDBuildUI.util.Notifier.showMessage(
                        Ext.String.format(
                            CMDBuildUI.locales.Locales.widgets.ai.copiedtoattribute,
                            attributeRecord.get('_description_translation')
                        ),
                        {
                            title: CMDBuildUI.locales.Locales.notifier.copied
                        }
                    );
                };
                if (theTarget.get(attributeId)) {
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.notifier.attention,
                        CMDBuildUI.locales.Locales.widgets.ai.overrideattribute,
                        function (action) {
                            if (action === 'yes') {
                                updateValueFn();
                            }
                        }
                    );
                } else {
                    updateValueFn();
                }
            } else {
                CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.widgets.ai.errors.wrongtype);
            }
        });
    },

    privates: {
        /**
         * Retrieves the value of the specified widget.
         * @param {String} name The name of the widget whose value is to be retrieved.
         * @returns {String| Boolean} The value of the specified widget.
         */
        getWidgetValue: function (name) {
            const widget = this.getViewModel().get('theWidget');
            return CMDBuildUI.view.widgets.aiassistant.Panel.getWidgetValue(widget, name);
        },

        resolveCardVariables: function (prompt) {
            const vm = this.getViewModel();
            const theTarget = vm.get('theTarget');

            return prompt.replace(/\{card:(\w+)\}/g, function (match, p1) {
                return theTarget.get(p1) || '';
            });
        }
    }
});
