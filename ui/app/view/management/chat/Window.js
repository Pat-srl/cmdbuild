Ext.define('CMDBuildUI.view.management.chat.Window', {
    extend: 'Ext.window.Window',

    requires: [
        'CMDBuildUI.view.management.chat.WindowController',
        'CMDBuildUI.view.management.chat.WindowModel'
    ],

    alias: 'widget.management-chat-window',
    controller: 'management-chat-window',
    viewModel: {
        type: 'management-chat-window'
    },

    autoShow: true,
    layout: 'fit',

    alwaysOnTop: 100,
    height: CMDBuildUI.util.Chat.height,
    width: CMDBuildUI.util.Chat.width,
    resizable: false,
    minimizable: false,
    maximizable: true,
    constrain: true,
    draggable: false,

    bind: {
        title: '{user.description} {titlenewmessage} {titleloader}'
    },

    items: [{
        xtype: 'grid',
        itemId: 'chatgrid',
        forceFit: true,
        hideHeaders: true,
        scrollable: true,
        rowLines: false,
        disableSelection: true,
        cls: Ext.baseCSSPrefix + 'chat-grid',
        columns: [{
            xtype: 'templatecolumn',
            variableRowHeight: true,
            tpl: [
                '<div class="{[Ext.baseCSSPrefix]}chat-message {[Ext.baseCSSPrefix]}chat-message-{type} {[Ext.baseCSSPrefix]}selectable',
                '<tpl if="values.isfirst == true">',
                ' {[Ext.baseCSSPrefix]}chat-message-first',
                '</tpl>',
                '">',
                '<span class="{[Ext.baseCSSPrefix]}chat-message-text">',
                '{content}',
                '</span>',
                '<small class="{[Ext.baseCSSPrefix]}chat-message-date" data-qtip="{[CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(values.timestamp)]}">',
                '{[CMDBuildUI.util.Utilities.getRelativeDate(values.timestamp)]}',
                '</small>',
                '</div>'
            ]
        }],
        bind: {
            store: '{messages}'
        }
    }],

    fbar: [{
        xtype: 'textarea',
        itemId: 'sendmsgtext',
        flex: 1,
        grow: true,
        growMin: 20,
        growMax: 120,
        enterIsSpecial: true,
        bind: {
            value: '{msg.text}'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('paper-plane', 'solid'),
        itemId: 'sendmsgbtn',
        minWidth: 0
    }],

    /**
     * Add new chat message.
     * @param {*} message
     */
    addNewMessage: function (message) {
        var vm = this.lookupViewModel();
        vm.get('messages').addSorted(message);
        this.scrollToBottom();
        if (!this.containsFocus) {
            vm.set('hasnewmessages', true);
        }
    },

    /**
     * Scroll grid to bottom.
     */
    scrollToBottom: function () {
        this.down('grid').getView().getScrollable().scrollTo(Infinity, Infinity, false);
    }
});