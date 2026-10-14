Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.Reports', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.ReportsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.ReportsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports'
    },
    reference: 'reports-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {}
    }]
});