
Ext.define('CMDBuildUI.view.main.notifications.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.main.notifications.GridController',
        'CMDBuildUI.view.main.notifications.GridModel'
    ],

    alias: 'widget.main-notifications-grid',
    controller: 'main-notifications-grid',
    viewModel: {
        type: 'main-notifications-grid'
    },

    bind: {
        store: '{notifications}'
    },

    cls: Ext.baseCSSPrefix + 'notification-grid',
    hideHeaders: true,
    disableSelection: true,
    forceFit: true,
    trackRemoved: true,

    columns: [{
        xtype: 'templatecolumn',
        tpl: [
            '<div class="{[Ext.baseCSSPrefix]}notification-item {[Ext.baseCSSPrefix]}selectable">',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-title">{subject}</div>',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-text">{content}</div>',
            '<tpl if="meta.action">',
            '<a href="#{meta.action}" class="{[Ext.baseCSSPrefix]}notification-item-button" role="button" aria-hidden="false" aria-disabled="false">{meta.actionLabel}</a>',
            '</tpl>',
            '<div class="{[Ext.baseCSSPrefix]}notification-item-date"><span data-qtip="{[CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(values.timestamp)]}">{[CMDBuildUI.util.Utilities.getRelativeDate(values.timestamp)]}</span></div>',
            '</div>'
        ],
        flex: 1
    }, {
        xtype: 'actioncolumn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
        tooltip: CMDBuildUI.locales.Locales.notifications.delete,
        width: 22,
        style: {
            'vertical-align': 'middle'
        },
        handler: function (grid, rowIndex, colIndex) {
            var store = grid.getStore(),
                record = store.getAt(rowIndex);
            record.erase({
                callback: function (record, operation, success) {
                    store.load();
                    Ext.GlobalEvents.fireEventArgs("notificationdeleted", [record.get("_id")]);
                }
            });
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.notifications.delete'
        }
    }]
});
