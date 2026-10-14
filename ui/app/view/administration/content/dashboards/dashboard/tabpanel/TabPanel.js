Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.tabpanel.TabPanel', {
    extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBase',
    alias: 'widget.administration-content-dashboards-tabpanel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBaseController',
        'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBaseModel',
        'CMDBuildUI.view.administration.content.dashboards.dashboard.tabs.PropertiesTab',
        'CMDBuildUI.view.administration.content.dashboards.dashboard.tabs.PermissionsTab'
    ],

    domainKey: 'dashboards',
    addButtonText: CMDBuildUI.locales.Locales.administration.dashboards.adddashboard,
    addButtonLocaleKey: 'CMDBuildUI.locales.Locales.administration.dashboards.adddashboard',
    addButtonTestId: 'administration-dashboard-toolbar-addDashboardBtn',
    extraToolbarItems: [
        {
            xtype: 'admin-globalsearchfield',
            objectType: 'dashboards'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tbtext',
            dock: 'right',
            itemId: 'dashboardGridCounter'
        }
    ],

    viewModel: {
        data: { domainKey: 'dashboards' }
    }
});
