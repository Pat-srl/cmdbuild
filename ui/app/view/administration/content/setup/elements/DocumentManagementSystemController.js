Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-documentmanagementsystem',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#redirectToPlugin': {
            click: 'onRedirectToPlugin'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.navigation.dms, CMDBuildUI.locales.Locales.administration.navigation.settings));
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        view.down('#previewlimit_input').labelToolIconQtip = CMDBuildUI.locales.Locales.administration.systemconfig.maxdmsattachmentspreview;
        view.down('#previewlimit_display').labelToolIconQtip = CMDBuildUI.locales.Locales.administration.systemconfig.maxdmsattachmentspreview;
    },

    onRedirectToPlugin: function () {
        const me = this;
        const vm = me.getViewModel();
        const services = vm.get('dmsServicesData');
        const type = vm.get('theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type');
        const pluginURL = CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl(services[type].pluginName);

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.savebeforeexit,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousureredirectplugin,
            function (action, param1, param2) {
                if (action === "yes") {
                    const view = me.getView();
                    CMDBuildUI.util.Utilities.redirectTo(pluginURL);
                    view.up('administration-content-setup-view').getController().onAsyncSave(view)
                }
            }
        );
    }

});