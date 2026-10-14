

Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.Permissions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-permissions',
    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.properties.PermissionsController',
        'CMDBuildUI.view.administration.content.processes.tabitems.properties.PermissionsModel'
    ],

    controller: 'administration-content-processes-tabitems-properties-permissions',
    viewModel: {
        type: 'administration-content-processes-tabitems-properties-permissions'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
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