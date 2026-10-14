Ext.define('CMDBuildUI.view.administration.home.DashboardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-dashboard',

    stores: {
        plugins: {
            type: 'chained',
            source: 'pluginmanager.Plugins'
        }
    },

    data: {
        isWarningHidden: true,
        warningHtml: ''
    }
});
