Ext.define('CMDBuildUI.view.administration.content.dashboards.homepage.tabpanel.TabPanel', {
    extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBase',
    alias: 'widget.administration-content-homepages-tabpanel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBaseController',
        'CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBaseModel',
        'CMDBuildUI.view.administration.content.dashboards.homepage.tabs.PropertiesTab',
        'CMDBuildUI.view.administration.content.dashboards.homepage.tabs.PermissionsTab'
    ],

    domainKey: 'homepages',
    addButtonText: CMDBuildUI.locales.Locales.administration.homepages.addhomepage,
    addButtonLocaleKey: 'CMDBuildUI.locales.Locales.administration.homepages.addhomepage',
    addButtonTestId: 'administration-homepage-toolbar-addHomepageBtn',
    useActiveTabFallback: true,

    viewModel: {
        data: { domainKey: 'homepages' }
    }
});
