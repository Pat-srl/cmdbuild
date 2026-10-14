Ext.define('CMDBuildUI.view.joinviews.configuration.permissions.Permissions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.joinviews-configuration-permissions-permissions',
    requires: [
        'CMDBuildUI.view.joinviews.configuration.permissions.PermissionsController',
        'CMDBuildUI.view.joinviews.configuration.permissions.PermissionsModel'
    ],

    controller: 'joinviews-configuration-permissions-permissions',
    viewModel: {
        type: 'joinviews-configuration-permissions-permissions'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view + 's',
        gridType: 'object',
        bind: {
            store: '{grantsChainedStore}'
        }
    }],
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: {
                disabled: true,
                bind: {
                    hidden: '{!actions.view}',
                    disabled: '{toolPermissionAction._canUpdate === false}'
                }
            }
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