Ext.define('CMDBuildUI.view.administration.ContentModel', {
    requires: ['CMDBuildUI.locales.Locales'],
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content',

    data: {
        title: CMDBuildUI.locales.Locales.administration.title,
        activeTabs: {
            classes: 0,
            processes: 0,
            lookuptypes: 0,
            dmsmodels: 0,
            gatetemplates: 0,
            joinView: 0,
            systemAuthentication: 0,
            dmssettings: 0,
            busdescriptors: 0,
            emailstemplate: 0,
            logs: 0,
            dashboards: 0,
            reports: 0,
            custompages: 0,
            views: 0,
            searchfilters: 0,
            dependencytrees: 0,
            importexport: 0
        },
        grantHierarchicalView: {
            classes: false,
            processes: false
        }
    }
});
