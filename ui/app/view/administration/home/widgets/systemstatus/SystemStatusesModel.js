Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatusesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-systemstatus-systemstatuses',
    data: {
        _isReady: false,
        systemStatusGridData: [],
        title: null
    },

    stores: {
        systemStatusGridStore: {
            proxy: 'memory',
            groupField: 'hostname',
            data: '{systemStatusGridData}'
        }
    }

});