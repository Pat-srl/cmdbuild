Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.servicesbus.ServicesBus', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.servicesbus.ServicesBusController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.servicesbus.ServicesBusModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-servicesbus-servicesbus',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-servicesbus-servicesbus',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-servicesbus-servicesbus'
    },
    reference: 'etlgate-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {}
    }]
});