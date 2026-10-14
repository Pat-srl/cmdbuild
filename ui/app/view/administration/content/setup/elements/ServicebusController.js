Ext.define('CMDBuildUI.view.administration.content.setup.elements.ServicebusController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-servicebus',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {administration-content-setup-elements-servicebus} view 
     */
    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.servicebus + ' - ' + CMDBuildUI.locales.Locales.administration.navigation.settings);
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
});