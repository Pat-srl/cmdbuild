
Ext.define('CMDBuildUI.view.management.chat.ConversationsList', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.management.chat.ConversationsListController',
        'CMDBuildUI.view.management.chat.ConversationsListModel'
    ],

    alias: 'widget.management-chat-conversationslist',
    controller: 'management-chat-conversationslist',
    viewModel: {
        type: 'management-chat-conversationslist'
    },

    id: CMDBuildUI.util.Chat.chatConversationsListId,
    title: CMDBuildUI.locales.Locales.chat.title,
    forceFit: true,
    collapsed: true,
    collapsible: true,
    collapseDirection: 'bottom',
    animCollapse: false,
    height: 200,
    disableSelection: true,
    hidden: true,

    bind: {
        title: '{panelTitle}',
        store: '{conversations}',
        hidden: '{hidechat}'
    },

    columns: [{
        xtype: 'templatecolumn',
        tpl: [
            '<div class="{[Ext.baseCSSPrefix]}chat-user',
                '<tpl if="_hasNewMessages">',
                    ' {[Ext.baseCSSPrefix]}chat-user-unreadmsgs',
                '</tpl>',
            '">',
            '<span class="{[Ext.baseCSSPrefix]}chat-user-avatar">',
                '<tpl if="icon">',
                    '<span style="background-image: url({icon});">&nbsp;</span>',
                '<tpl else>',
                    '<span>{[values.description[0].toUpperCase()]}</span>',
                '</tpl>',
            '</span>',
            '<span class="{[Ext.baseCSSPrefix]}chat-user-description">{description}</span>',
            '</div>'
        ]
    }],

    tools: [{
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
        itemId: 'addconversation'
    }]
});
