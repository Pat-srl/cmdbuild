(function () {
    var elementId = 'CMDBuildAdministrationContentDashboardsView';
    Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.tabs.PropertiesTab', {
        extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBase',
        alias: 'widget.administration-content-dashboards-propertiestab',
        requires: [
            'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseController',
            'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseModel'
        ],
        id: elementId,
        statics: {
            elementId: elementId
        },
        entityXtype: 'view-administration-content-dashboards-card',
        entityIdVmKey: 'dashboardId',
        listRoute: 'administration/dashboards',
        viewModel: {
            data: { modifyPrivilegeKey: 'admin_dashboards_modify' }
        }
    });
})();
