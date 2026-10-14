Ext.define('CMDBuildUI.view.administration.content.dependencytrees.PermissionsTab', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-dependencytrees-permissionstab',
    requires: [
        'CMDBuildUI.view.administration.content.dependencytrees.PermissionsTabController',
        'CMDBuildUI.view.administration.content.dependencytrees.PermissionsTabModel'
    ],

    controller: 'administration-content-dependencytrees-permissionstab',
    viewModel: {
        type: 'administration-content-dependencytrees-permissionstab'
    },

    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },

    items: [
        {
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.dependencytree,
            gridType: 'object',
            bind: {
                store: '{grantsChainedStore}'
            }
        }
    ],

    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            })
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
        }
    ]
});
