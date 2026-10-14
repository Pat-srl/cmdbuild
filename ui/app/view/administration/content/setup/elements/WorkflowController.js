Ext.define('CMDBuildUI.view.administration.content.setup.elements.WorkflowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-workflow',
    
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.workflow);
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
    
});
