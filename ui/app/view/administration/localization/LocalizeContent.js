Ext.define('CMDBuildUI.view.administration.localization.LocalizeContent', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-localization-localizecontent',
    requires: [
        'CMDBuildUI.view.administration.localization.LocalizeContentController',
        'CMDBuildUI.view.administration.localization.LocalizeContentModel'
    ],

    controller: 'administration-localization-localizecontent',
    viewModel: {
        type: 'administration-localization-localizecontent'
    },
    fieldDefaults: {
        labelAlign: 'left'
    },
    config: {
        theVmObject: 'theTranslation',
        editorType: 'textfield'
    },
    constrain: true,
    width: '450px',
    buttons: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            ui: 'administration-secondary-action-small',
            bind: {
                hidden: '{actions.view}'
            },
            listeners: {
                click: 'onCancelBtnClick'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.close,
            ui: 'administration-secondary-action-small',
            bind: {
                hidden: '{!actions.view}'
            },
            listeners: {
                click: 'onCancelBtnClick'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            formBind: true, //only enabled once the form is valid
            disabled: true,
            ui: 'administration-action-small',
            bind: {
                hidden: '{actions.view}'
            },
            listeners: {
                click: 'onSaveBtnClick'
            }
        }
    ]
});
