Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addlookuptype': {
            click: 'onAddLookupTypeClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.lookuptypes.Topbar} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getView().lookupViewModel().set("lookupLabel", CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.classLabel);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} e
     * @param {Object} eOpts
     */
    onAddLookupTypeClick: function (button, e, eOpts) {
        this.redirectTo('administration/lookup_types', true);
    }
});