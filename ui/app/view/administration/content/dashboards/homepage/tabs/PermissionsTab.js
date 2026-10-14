Ext.define('CMDBuildUI.view.administration.content.dashboards.homepage.tabs.PermissionsTab', {
    extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBase',
    alias: 'widget.administration-content-homepages-permissionstab',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBaseController',
        'CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBaseModel'
    ],

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.homepage,
    ownerTabPanelXtype: 'administration-content-homepages-tabpanel',

    viewModel: {
        data: { targetType: 'homepage' }
    }
});
