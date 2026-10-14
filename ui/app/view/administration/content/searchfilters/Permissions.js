Ext.define('CMDBuildUI.view.administration.content.searchfilters.Permissions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-searchfilters-permissions',
    requires: [
        'CMDBuildUI.view.administration.content.searchfilters.PermissionsController',
        'CMDBuildUI.view.administration.content.searchfilters.PermissionsModel'
    ],

    controller: 'administration-content-searchfilters-permissions',
    viewModel: {
        type: 'administration-content-searchfilters-permissions'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.searchfilter,
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