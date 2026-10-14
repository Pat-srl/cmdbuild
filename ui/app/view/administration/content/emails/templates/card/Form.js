Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.FormController',
        'CMDBuildUI.view.administration.content.emails.templates.card.FormModel',
        'CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper'
    ],

    alias: 'widget.administration-content-emails-templates-card-form',
    controller: 'administration-content-emails-templates-card-form',
    viewModel: {
        type: 'administration-content-emails-templates-card-form'
    },

    modelValidation: true,
    scrollable: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    bind: {
        userCls: '{formModeCls}'
    },
    items: [],
    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            bind: {
                hidden: '{!actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true,
                activeToggle: true,
                clone: true,
                delete: true
            }, 'template', 'theTemplate')
        }]
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }]
});