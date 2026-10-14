(function () {
    var elementId = 'CMDBuildAdministrationContentHomepagesView';
    Ext.define('CMDBuildUI.view.administration.content.dashboards.homepage.tabs.PropertiesTab', {
        extend: 'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBase',
        alias: 'widget.administration-content-homepages-propertiestab',
        requires: [
            'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseController',
            'CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseModel'
        ],
        id: elementId,
        statics: {
            elementId: elementId
        },
        entityXtype: 'view-administration-content-dashboards-homepage-card',
        entityIdVmKey: 'homepageId',
        listRoute: 'administration/homepages',
        viewModel: {
            data: { modifyPrivilegeKey: 'admin_homepages_modify' }
        }
    });
})();
