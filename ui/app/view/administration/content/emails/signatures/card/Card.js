Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.Card', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.signatures.card.CardController',
        'CMDBuildUI.view.administration.content.emails.signatures.card.CardModel'
    ],

    alias: 'widget.administration-content-emails-signatures-card-card',
    controller: 'administration-content-emails-signatures-card-card',
    viewModel: {
        type: 'administration-content-emails-signatures-card-card'
    },

    modelValidation: true,
    scrollable: true,

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            layout: 'column',
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                    name: {
                        vtype: 'nameInputValidation',
                        allowBlank: false,
                        bind: {
                            value: '{theSignature.code}'
                        }
                    }
                }, true, '[name="description"]'),

                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                    description: {
                        allowBlank: false,
                        bind: {
                            value: '{theSignature.description}'
                        },
                        style: {
                            paddingRight: 0
                        },
                        fieldcontainer: {
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateDescriptionClick'
                        }
                    }
                })
            ]
        }, {
            layout: 'column',
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                active: {
                    bind: {
                        disabled: '{actions.view}',
                        value: '{theSignature.active}'
                    }
                }
            })]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 1,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.signature,
                layout: 'card',
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.signature'
                },
                allowBlank: false,
                cls: 'ignore-first-type-rule',
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateSignatureContentClick',
                items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                        hidden: true,
                        allowBlank: false,
                        bind: {
                            value: '{theSignature.content_html}',
                            hidden: '{actions.view}'
                        }
                    }),
                    {
                        // view
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theSignature.content_html}',
                            hidden: '{!actions.view}'
                        }
                    }
                ]
            }]
        }]

    }],
    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
});