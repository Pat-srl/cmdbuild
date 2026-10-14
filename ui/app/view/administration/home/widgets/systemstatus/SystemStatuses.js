Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatuses', {
    extend: 'Ext.panel.Panel',

    statics: {
        statskeys: {
            stime: 'server_time',
            stimezone: 'server_timezone',
            uptime: 'uptime',
            disktotal: 'disk_total',
            diskused: 'disk_used',
            jmemtotal: 'java_memory_total',
            jmemused: 'java_memory_used',
            smemtotal: 'system_memory_total',
            smemused: 'system_memory_used',
            dbconnactive: 'datasource_active_connections',
            dbconnmax: 'datasource_max_active_connections',
            dbconnidle: 'datasource_idle_connections',
            dbconnidlemax: 'datasource_max_idle_connections',
            version: 'version',
            build: 'build_info',
            sload: 'system_load'
        }
    },

    requires: [
        'CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatusesController',
        'CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatusesModel'
    ],

    controller: 'administration-home-widgets-systemstatus-systemstatuses',
    viewModel: {
        type: 'administration-home-widgets-systemstatus-systemstatuses'
    },

    alias: 'widget.administration-home-widgets-systemstatus-systemstatuses',
    defaults: {
        layout: 'hbox',
        style: {
            marginBottom: "15px"
        }
    },
    title: CMDBuildUI.locales.Locales.administration.home.systemstatus,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.systemstatus'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    minHeight: '200',
    ui: 'admindashboard',
    tools: [{
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
        itemId: 'serverManagementRefreshTool',
        tooltip: CMDBuildUI.locales.Locales.administration.home.refresh,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.home.refresh'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
        itemId: 'serverManagementTool',
        tooltip: CMDBuildUI.locales.Locales.administration.home.gotoservermanagementpage,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.home.gotoservermanagementpage'
        },
        hidden: true,
        bind: {
            hidden: '{!theSession.rolePrivileges.admin_all}'
        }
    }],
    items: []
});