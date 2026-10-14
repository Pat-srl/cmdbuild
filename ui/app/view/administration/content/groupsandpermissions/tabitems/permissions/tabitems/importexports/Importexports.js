Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.Importexports', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.ImportexportsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.ImportexportsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports'
    },
    reference: 'etltemplate-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {}
    }]
});