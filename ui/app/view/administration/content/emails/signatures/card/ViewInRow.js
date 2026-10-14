Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: ['CMDBuildUI.view.administration.content.emails.signatures.card.ViewInRowController'],

    alias: 'widget.administration-content-emails-signatures-card-viewinrow',
    controller: 'administration-content-emails-signatures-card-viewinrow',

    cls: 'administration',
    ui: 'administration-tabandtools',

    items: [
        {
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [
                {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    bind: {
                        value: '{theSignature.code}'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateDescriptionClick',
                    items: [
                        {
                            xtype: 'displayfield',
                            bind: {
                                value: '{theSignature.description}'
                            }
                        }
                    ]
                },
                {
                    columnWidth: 1,
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.signature,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.signature'
                    },
                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateSignatureContentClick',
                    items: [
                        {
                            xtype: 'displayfield',
                            bind: {
                                value: '{theSignature.content_html}'
                            }
                        }
                    ]
                }
            ]
        }
    ],
    tools: [
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tool',
            itemId: 'signaturesEditBtn',
            reference: 'signaturesEditBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-editBtn'
            },
            bind: {
                disabled: '{!toolAction._canUpdate}'
            }
        },
        {
            xtype: 'tool',
            itemId: 'signaturesOpenBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.open,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.open'
            },
            callback: 'onOpenBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-openBtn'
            }
        },
        {
            xtype: 'tool',
            itemId: 'signaturesDeleteBtn',
            reference: 'signaturesDeleteBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
            },
            disabled: true,
            bind: {
                disabled: '{theSignature._default ||!toolAction._canDelete}'
            },
            callback: 'onDeleteBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-editBtn'
            }
        },
        {
            xtype: 'tool',
            itemId: 'enableBtn',
            hidden: true,
            disabled: true,
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
            },
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-enableBtn'
            },
            callback: 'onToggleActiveBtnClick',
            bind: {
                hidden: '{theSignature.active}',
                disabled: '{!toolAction._canActiveToggle}'
            },
            listeners: {
                disable: function (tool) {
                    this.setTooltip(
                        CMDBuildUI.locales.Locales.administration.common.messages.youarenotabletochangeactive
                    );
                },
                enable: function (tool) {
                    this.setTooltip(CMDBuildUI.locales.Locales.administration.common.actions.enable);
                }
            }
        },
        {
            xtype: 'tool',
            itemId: 'disableBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ban', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
            },
            hidden: true,
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-disableBtn'
            },
            callback: 'onToggleActiveBtnClick',
            bind: {
                hidden: '{!theSignature.active}',
                disabled: '{!toolAction._canActiveToggle}'
            },
            listeners: {
                disable: function (tool) {
                    this.setTooltip(
                        CMDBuildUI.locales.Locales.administration.common.messages.youarenotabletochangeactive
                    );
                },
                enable: function (tool) {
                    this.setTooltip(CMDBuildUI.locales.Locales.administration.common.actions.disable);
                }
            }
        },
        {
            xtype: 'tool',
            itemId: 'defaultSignature',
            reference: 'defaultSignature',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.emails.unsetasdefault,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.emails.unsetasdefault'
            },
            cls: 'administration-tool',
            bind: {
                hidden: '{!theSignature._default}',
                disabled: '{!toolAction._canUpdate}'
            },
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-defaultSignature'
            }
        },
        {
            xtype: 'tool',
            itemId: 'setDefaultSignature',
            reference: 'setDefaultSignature',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.emails.setasdefault,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.emails.setasdefault'
            },
            cls: 'administration-tool',
            bind: {
                hidden: '{theSignature._default}',
                disabled: '{!toolAction._canUpdate}'
            },
            autoEl: {
                'data-testid': 'administration-emails-signatures-card-viewInRow-defaultSignature'
            }
        }
    ]
});
