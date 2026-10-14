Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.tabs.PermissionsTab', {
    extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBase',
    alias: 'widget.administration-content-dashboards-permissionstab',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBaseController',
        'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBaseModel'
    ],

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard,
    ownerTabPanelXtype: 'administration-content-dashboards-tabpanel',

    viewModel: {
        data: { targetType: 'dashboard' }
    }
});
