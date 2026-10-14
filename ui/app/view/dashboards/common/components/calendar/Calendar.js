Ext.define('CMDBuildUI.view.dashboards.common.components.calendar.Calendar', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.calendar.CalendarController',
        'CMDBuildUI.view.dashboards.common.components.calendar.CalendarModel'
    ],

    alias: 'widget.dashboards-components-calendar-calendar',
    controller: 'dashboards-components-calendar-calendar',
    viewModel: {
        type: 'dashboards-components-calendar-calendar'
    },

    layout: 'fit',

    customActions: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.reload,
            handler: 'menuItemReloadHandler',
            scope: 'controller'
        }
    ]
});
