Ext.define('CMDBuildUI.view.administration.content.lookuptypes.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-view',

    control: {
        '#':{
            afterlayout: 'onAfterLayout',
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.lookuptypes.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function(view, eOpts){
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.lookuptypes);
    },

    /**
     *
     * @param {Ext.container.Container} container
     * @param {Ext.layout.container.Container} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (container, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    }
});
