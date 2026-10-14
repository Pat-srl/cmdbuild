Ext.define('CMDBuildUI.view.administration.content.emails.errors.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-errors-grid',
    data: {
        storeConfig: {
            autoLoad: true,
            url: '/email/error'
        },
        queueEnabled: false
    },
    stores: {
        gridDataStore: {
            model: 'CMDBuildUI.model.emails.Email',
            proxy: {
                type: 'baseproxy',
                url: '{storeConfig.url}',
                extraParams: {
                    detailed: true
                }
            },
            pageSize: 0,
            autoLoad: '{storeConfig.autoLoad}',
            autoDestroy: true
        }
    }

});