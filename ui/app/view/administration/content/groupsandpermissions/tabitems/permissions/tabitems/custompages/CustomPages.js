Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPages', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPagesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPagesModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages'
    },
    reference: 'custompages-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {}
    }]
});