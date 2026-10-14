

Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.permissions.Permissions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-permissions-permissions',
    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.permissions.PermissionsController',
        'CMDBuildUI.view.administration.content.classes.tabitems.permissions.PermissionsModel'
    ],

    controller: 'administration-content-classes-tabitems-permissions-permissions',
    viewModel: {
        type: 'administration-content-classes-tabitems-permissions-permissions'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },

    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        objectType: CMDBuildUI.model.menu.MenuItem.types.klass,
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