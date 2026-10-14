Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBase', {
    extend: 'Ext.panel.Panel',

    controller: 'administration-content-dashboards-common-permissionstab',
    viewModel: {
        type: 'administration-content-dashboards-common-permissionstab'
    },

    config: {
        objectType: null,
        ownerTabPanelXtype: null
    },

    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },

    initComponent: function () {
        this.items = [
            {
                xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
                objectType: this.getObjectType(),
                gridType: 'object',
                bind: {
                    store: '{grantsChainedStore}'
                }
            }
        ];

        this.dockedItems = [
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
                    hidden: '{actions.view }'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
            }
        ];

        this.callParent(arguments);
    }
});
