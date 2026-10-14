Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeForm', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormController',
        'CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormModel',
        'CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormHelper'
    ],
    alias: 'widget.administration-content-gisnavigationtrees-viewmodeform',
    controller: 'administration-content-gisnavigationtrees-viewmodeform',
    viewModel: {
        type: 'administration-content-gisnavigationtrees-viewmodeform'
    },
    config: {
        record: null
    },
    bind: {
        record: '{record}'
    },
    items: [
        CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormHelper.getViewModeFieldset(),
        CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormHelper.getSubclassesTreeFieldset()
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]

    
});