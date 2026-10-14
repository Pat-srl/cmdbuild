(function () {

    var elementId = 'CMDBuildAdministrationPermissions';
    Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.Permissions', {
        extend: 'Ext.tab.Panel',

        requires: [
            'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsController',
            'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsModel'
        ],
        alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-permissions',
        reference: 'administration-content-groupsandpermissions-tabitems-permissions-permissions',
        controller: 'administration-content-groupsandpermissions-tabitems-permissions-permissions',
        viewModel: {
            type: 'administration-content-groupsandpermissions-tabitems-permissions-permissions'
        },
        id: elementId,
        tabPosition: 'top',
        tabRotation: 0,
        cls: 'administration-mainview-subtabpanel',
        ui: 'administration-tabandtools',
        scrollable: true,
        forceFit: true,
        layout: 'fit',
        config: {
            disabledTabs: {},
            theGroup: {}
        },
        bind: {
            theGroup: '{theGroup}'
        },

        defaults: {
            height: 25
        },

        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
        }]
    });
})();