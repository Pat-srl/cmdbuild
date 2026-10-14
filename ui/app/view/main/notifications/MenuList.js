
Ext.define('CMDBuildUI.view.main.notifications.MenuList', {
    extend: 'Ext.view.View',

    requires: [
        'CMDBuildUI.view.main.notifications.MenuListController',
        'CMDBuildUI.view.main.notifications.MenuListModel'
    ],

    alias: 'widget.main-notifications-menulist',
    controller: 'main-notifications-menulist',
    viewModel: {
        type: 'main-notifications-menulist'
    },

    itemTpl: [
        '<div data-role="notification-item" class="{[Ext.baseCSSPrefix]}notification-item <tpl if="_isNew">{[Ext.baseCSSPrefix]}notification-item-new</tpl> {[Ext.baseCSSPrefix]}selectable">',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-title">{subject}</div>',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-text">{content}</div>',
            '<tpl if="meta.action">',
                '<a href="#{meta.action}" class="{[Ext.baseCSSPrefix]}notification-item-button" role="button" aria-hidden="false" aria-disabled="false">{meta.actionLabel}</a>',
            '</tpl>',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-date">',
                '<span data-qtip="{[CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(values.timestamp)]}">',
                    '{[CMDBuildUI.util.Utilities.getRelativeDate(values.timestamp)]}',
                '</span>',
            '</div>',
        '</div>'
    ],

    itemSelector: 'div[data-role=notification-item]',

    width: 250,
    maxHeight: 400,
    scrollable: true,

    bind: {
        store: '{notificationStore}',
        emptyText: '{emptyTextMessage}'
    }
});
