Ext.define('CMDBuildUI.view.administration.MainContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-maincontainer',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        Ext.getStore('localizations.Languages').setAutoLoad(true);
    }

});