Ext.define('CMDBuildUI.view.administration.content.emails.queue.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-queue-grid',
    data: {
        storeConfig: {
            autoLoad: true
        },
        queueEnabled: false
    },
    stores: {
        gridDataStore: {        
            type: 'administration-email-queue',
            autoLoad: '{storeConfig.autoLoad}',
            autoDestroy: true
        }
    }

});