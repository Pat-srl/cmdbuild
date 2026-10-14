Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogViewerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-logviewer',
    data: {
        loggeractive: false,
        autoscrollenabled: true
        
    },
    stores: {
        messagesStore: {
            fields: ['_id', 'level', 'line', 'message', 'timestamp', '_event']            
        }
    }

});
