Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.Classes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesModel'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes'
    },
    reference: 'classes-tab',

    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',        
        bind: {
            hidden: '{hierarchicalView}'
        }
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-treegrid',        
        bind: {
            hidden: '{!hierarchicalView}'
        }
    }]
});