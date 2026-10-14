Ext.define('CMDBuildUI.view.widgets.aiassistant.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-aiassistant-panel',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#promptAction': {
            click: 'onPromptActionClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const theWidget = view.lookupViewModel().get('theWidget');
        const items = [];

        const confActions = {
            xtype: 'container',
            padding: '10px 20px 0 20px',
            items: [
                {
                    xtype: 'button',
                    margin: '0 0 0 5px',
                    itemId: 'promptAction',
                    text: CMDBuildUI.view.widgets.aiassistant.Panel.getWidgetValue(theWidget, 'PromptLabel'),
                    ui: 'management-primary-small',
                    disabled: 'true',
                    bind: {
                        disabled: '{disablePromptAction}'
                    }
                }
            ]
        };

        const confChat = {
            xtype: 'widgets-aiassistant-chat',
            maxWidthParentRatio: 1,
            viewModel: {
                data: {
                    theWidget: theWidget,
                    theTarget: view.lookupViewModel().get('theTarget')
                }
            }
        };

        // configure grid height if needed
        if (theWidget.get('_inline')) {
            confChat.height = view.up('form').getHeight() * 0.5;
        }
        confChat.flex = 1;
        items.push(confActions, confChat);
        view.add(items);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPromptActionClick: function (button, e, eOpts) {
        const chatController = this.getView().down('widgets-aiassistant-chat').getController();
        if (chatController) {
            chatController.sendInitialPrompt();
        }
    }
});
