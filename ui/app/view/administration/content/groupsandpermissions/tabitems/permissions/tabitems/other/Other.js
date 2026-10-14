Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.other.Other', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.other.OtherController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.other.OtherModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other'
    },
    reference: 'other-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        itemId: 'administration-other-permissions',
        viewModel: {}
    }]
});