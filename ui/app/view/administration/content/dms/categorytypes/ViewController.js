Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-view',

    control: {
        '#':{
            beforerender: 'onBeforeRender'
        }
    },
    onBeforeRender: function(view){       
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.dmscategories);
    }
});
