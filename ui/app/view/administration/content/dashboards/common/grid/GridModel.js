Ext.define('CMDBuildUI.view.administration.content.dashboards.common.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-grid',

    stores: {
        allDashboards: {
            source: 'dashboards.Dashboards',
            autoload: true,
            autoDestroy: true,
            listeners: {
                datachanged: 'onAllDashboardsStoreDatachanged'
            },
            sorters: ['description']
        }
    }
});
