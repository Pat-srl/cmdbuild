Ext.define('CMDBuildUI.view.mobile.config.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.mobile-config-panel',
    data: {
        values: {
            serverurl: null,
            customercode: null,
            devicename: null
        },
        regeneratebtn: {
            disabled: true
        }
    }
});
