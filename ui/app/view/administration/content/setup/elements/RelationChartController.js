Ext.define('CMDBuildUI.view.administration.content.setup.elements.RelationChartController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-relationchart',
    
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.common.actions.relationchart);
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
    
    
});
