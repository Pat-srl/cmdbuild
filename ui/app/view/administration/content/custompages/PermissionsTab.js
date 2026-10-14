

Ext.define('CMDBuildUI.view.administration.content.custompages.PermissionsTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-custompages-permissionstab',
    requires: [
        'CMDBuildUI.view.administration.content.custompages.PermissionsTabController',
        'CMDBuildUI.view.administration.content.custompages.PermissionsTabModel'
    ],

    controller: 'administration-content-custompages-permissionstab',
    viewModel: {
        type: 'administration-content-custompages-permissionstab'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },


    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
        gridType: 'object',
        bind: {
            store: '{grantsChainedStore}'
        }
    }],
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true
        })
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view }'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
    }]
});