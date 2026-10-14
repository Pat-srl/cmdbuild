Ext.define('CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeForm', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormController',
        'CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormModel',
        'CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormHelper'
    ],
    alias: 'widget.administration-content-menunavigationtrees-viewmodeform',
    controller: 'administration-content-menunavigationtrees-viewmodeform',
    viewModel: {
        type: 'administration-content-menunavigationtrees-viewmodeform'
    },
    config: {
        record: null
    },
    bind: {
        record: '{record}'
    },
    items: [
        CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormHelper.getViewModeFieldset(),
        CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormHelper.getSubclassesTreeFieldset()
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