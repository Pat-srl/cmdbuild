Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.Views', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ViewsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ViewsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views'
    },
    reference: 'views-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {}
    }]
});