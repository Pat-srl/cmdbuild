Ext.define('CMDBuildUI.view.widgets.aiassistant.Chat', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.widgets.aiassistant.ChatController', 'CMDBuildUI.view.management.ai.ConversationPanel'],

    alias: 'widget.widgets-aiassistant-chat',
    controller: 'widgets-aiassistant-chat',
    viewModel: {
        type: 'widgets-aiassistant-chat'
    },

    cls: 'ai-chat-style',
    padding: '0 20px 0 20px',
    margin: '0',
    layout: 'fit',

    items: [
        {
            xtype: 'fieldset',
            ui: 'formpagination',
            title: 'Chat',
            items: [
                {
                    xtype: 'management-ai-conversation',
                    itemId: 'chatgrid',
                    flex: 1
                }
            ]
        }
    ]
});
