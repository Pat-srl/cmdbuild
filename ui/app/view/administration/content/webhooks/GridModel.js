Ext.define('CMDBuildUI.view.administration.content.webhooks.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-webhooks-grid',

    stores: {
        gridStore: {
            model: 'CMDBuildUI.model.webhooks.Webhook',
            proxy: {
                type: 'baseproxy',
                url: '/etl/webhook',
                extraParams: {
                    detailed: true
                }
            },
            autoLoad: true
        }
    }

});