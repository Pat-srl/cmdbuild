Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dashboards.Dashboards', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dashboards.DashboardsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dashboards.DashboardsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-dashboards-dashboards',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dashboards-dashboards',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dashboards-dashboards'
    },
    reference: 'dashboards-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {},
        bind: {
            store: '{objectTypeGrantsStore}'
        }
    }]
});