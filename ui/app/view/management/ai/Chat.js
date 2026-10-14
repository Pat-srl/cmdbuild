Ext.define('CMDBuildUI.view.management.ai.Chat', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.management.ai.ChatController',
        'CMDBuildUI.view.management.ai.ChatModel',
        'CMDBuildUI.view.management.ai.ConversationPanel'
    ],

    alias: 'widget.management-ai-chat',
    controller: 'management-ai-chat',
    viewModel: {
        type: 'management-ai-chat'
    },

    cls: 'ai-chat-style',
    scrollable: true,
    layout: 'fit',

    tbar: [
        {
            xtype: 'component',
            bind: {
                html: '{chatTitleHtml}'
            },
            cls: 'titleChat',
            margin: '10 0 0 0'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'button',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
            ui: 'management-primary',
            itemId: 'newchatbtn',
            userCls: 'newChatButton',
            tooltip: CMDBuildUI.locales.Locales.ai.newchattooltip,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.ai.newchattooltip'
            },
            disabled: true,
            bind: {
                disabled: '{!messagesStore.count}'
            }
        },
        {
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
            itemId: 'closeWindow',
            cls: 'management-tool'
        }
    ],

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'panel',
                    itemId: 'intro',
                    bind: {
                        html: '{introPanelHtml}',
                        hidden: '{messagesStore.count}'
                    }
                },
                {
                    xtype: 'management-ai-conversation',
                    itemId: 'chatgrid',
                    flex: 1
                }
            ]
        }
    ],

    fbar: [
        {
            xtype: 'container',
            flex: 1,
            cls: 'sendMessageContainer',
            margin: '5 10 5 5',
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            items: [
                {
                    xtype: 'textarea',
                    itemId: 'sendmsgtextarea',
                    cls: 'sendMsgTextArea',
                    emptyText: CMDBuildUI.locales.Locales.ai.questionemptytext,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.ai.questionemptytext'
                    },
                    padding: 5,
                    flex: 1,
                    grow: true,
                    growMin: 20,
                    growMax: 110,
                    enterIsSpecial: true,
                    bind: {
                        value: '{msg.text}'
                    }
                },
                {
                    xtype: 'button',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                    cls: 'sendButton',
                    hidden: true,
                    margin: '5 10',
                    ui: 'management-primary',
                    itemId: 'sendmsgbtn',
                    minWidth: 0,
                    bind: {
                        disabled: '{loading}',
                        hidden: '{!msg.text}'
                    }
                }
            ]
        }
    ]
});
