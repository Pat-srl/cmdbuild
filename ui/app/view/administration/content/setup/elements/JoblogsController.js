Ext.define('CMDBuildUI.view.administration.content.setup.elements.JoblogsController', {
    extend: 'CMDBuildUI.view.administration.content.bus.messages.GridController',
    alias: 'controller.administration-content-setup-elements-joblogs',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }        
    },

    onBeforeRender: function (view) {
        var setupVm = view.up('administration-content-setup-view').getViewModel();
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.jobmessages);
        setupVm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);        
    }


});