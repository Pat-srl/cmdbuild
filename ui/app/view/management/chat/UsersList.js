Ext.define('CMDBuildUI.view.management.chat.UsersList',{
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.management.chat.UsersListController',
        'CMDBuildUI.view.management.chat.UsersListModel'
    ],

    alias: 'widget.management-chat-userslist',
    controller: 'management-chat-userslist',
    viewModel: {
        type: 'management-chat-userslist'
    },

    /**
     * @property {CMDBuildUI.view.management.chat.ConversationsList}
     */
    ownerList: null,

    header: false,
    forceFit: true,
    hideHeaders: true,
    disableSelection: true,

    bind: {
        store: '{users}'
    },

    autoEl: {
        'data-testid': 'management-chat-userslist'
    },

    tbar: [{
        xtype: 'textfield',
        name: 'search',
        flex: 1,
        emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
        itemId: 'searchuser',
        autoEl: {
            'data-testid': 'management-chat-userslist-searchtext'
        },
        bind: {
            value: '{usersearch}'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        },
        localized: {
            emptyText: "CMDBuildUI.locales.Locales.common.actions.searchtext"
        }
    }],

    columns: [{
        xtype: 'templatecolumn',
        tpl: [
            '<div class="{[Ext.baseCSSPrefix]}chat-user">',
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

    /**
     * @returns {CMDBuildUI.view.management.chat.ConversationsList}
     */
    getOwnerList: function() {
        return this.ownerList;
    }
});
