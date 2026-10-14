Ext.define('CMDBuildUI.view.administration.content.dms.models.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-models-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterlayout: 'onAfterLayout'
        }
    },

    onBeforeRender: function (view) {
        Ext.getStore('importexports.Templates').load();        
    },
    onAfterLayout: function (panel) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    }
});
