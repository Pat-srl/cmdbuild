Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowModel'
    ],

    alias: 'widget.administration-content-emails-accounts-card-viewinrow',
    controller: 'administration-content-emails-accounts-card-viewinrow',
    viewModel: {
        type: 'administration-content-emails-accounts-card-viewinrow'
    },
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
                columnWidth: 1
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'displayfield',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            name: 'name',
                            bind: {
                                value: '{theAccount.name}'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.authtype,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.authtype'
                            },
                            name: 'name',
                            bind: {
                                value: '{theAccount.auth_type}'
                            },
                            renderer: function (value) {
                                if (value) {
                                    var vm = this.lookupViewModel();
                                    return vm.get('authTypesStore').findRecord('value', value).get('label');
                                }
                                return value;
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            layout: 'column',
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                                    },
                                    name: 'username',
                                    bind: {
                                        value: '{theAccount.username}'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    padding: '0 15 0 0',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.address,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.address'
                                    },
                                    hidden: true,
                                    bind: {
                                        value: '{theAccount.address}',
                                        hidden: '{theAccount.auth_type !== "ms_oauth"}'
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'displayfield',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                            },
                            name: 'password',
                            bind: {
                                hidden: '{theAccount.auth_type !== "default"}',
                                value: '{hiddenPassword}'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.token,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.token'
                            },
                            grow: true,
                            columnWidth: 0.5,
                            readOnly: true,
                            hidden: true,
                            bind: {
                                hidden: '{theAccount.auth_type === "default"}',
                                value: '{theAccount.password}'
                            }
                        }
                    ]
                },
                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                    active: {
                        readOnly: true,
                        columnWidth: 1,
                        bind: {
                            value: '{theAccount.active}'
                        }
                    }
                })
            ]
        },
        {
            title: CMDBuildUI.locales.Locales.administration.emails.outgoing,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.outgoing'
            },
            xtype: 'fieldset',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            layout: 'column',
            bind: {
                disabled: '{theAccount.auth_type === "ms_oauth"}'
            },
            defaults: {
                columnWidth: 1,
                layout: 'column'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.address,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.address'
                            },
                            name: 'address',
                            bind: {
                                value: '{theAccount.address}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpserver,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpserver'
                            },
                            name: 'smtp_server',
                            bind: {
                                value: '{theAccount.smtp_server}'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpport,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpport'
                            },
                            name: 'smtp_port',
                            bind: {
                                value: '{theAccount.smtp_port}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.sentfolder,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.sentfolder'
                            },
                            name: 'imap_output_folder',
                            bind: {
                                value: '{theAccount.imap_output_folder}'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail,
                            emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail',
                                emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                            },
                            bind: {
                                value: '{theAccount.maxAttachmentSizeForEmail}'
                            },
                            renderer: function (value) {
                                return value && value !== '0'
                                    ? value
                                    : value == '0'
                                      ? CMDBuildUI.locales.Locales.administration.systemconfig.nolimit
                                      : CMDBuildUI.locales.Locales.administration.common.labels.default;
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
                            },
                            readOnly: true,
                            name: 'smtp_ssl',
                            bind: {
                                value: '{theAccount.smtp_ssl}'
                            }
                        },
                        {
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
                            },
                            readOnly: true,
                            name: 'smtp_starttls',
                            bind: {
                                value: '{theAccount.smtp_starttls}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            title: CMDBuildUI.locales.Locales.administration.emails.incoming,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.incoming'
            },
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            layout: 'column',

            bind: {
                disabled: '{theAccount.auth_type === "ms_oauth"}'
            },
            defaults: {
                columnWidth: 0.5
            },
            items: [
                {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapserver,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapserver'
                    },
                    name: 'imap_server',
                    bind: {
                        value: '{theAccount.imap_server}'
                    }
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapport,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapport'
                    },
                    name: 'imap_port',
                    bind: {
                        value: '{theAccount.imap_port}'
                    }
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
                    },
                    readOnly: true,
                    name: 'imap_ssl',
                    bind: {
                        value: '{theAccount.imap_ssl}'
                    }
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
                    },
                    readOnly: true,
                    name: 'imap_starttls',
                    bind: {
                        value: '{theAccount.imap_starttls}'
                    }
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
            itemId: 'accountsEditBtn',
            reference: 'accountsEditBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-editBtn'
            },
            bind: {
                disabled: '{!toolAction._canUpdate}'
            }
        },
        {
            xtype: 'tool',
            itemId: 'accountsOpenBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.open,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.actions.open'
            },
            callback: 'onOpenBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-openBtn'
            }
        },
        {
            xtype: 'tool',
            itemId: 'accountsDeleteBtn',
            reference: 'accountsDeleteBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.emails.removeaccount,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.removeaccount'
            },
            disabled: true,
            bind: {
                disabled: '{isDefault ||!toolAction._canDelete}'
            },
            callback: 'onDeleteBtnClick',
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-removeBtn'
            }
        },
        {
            xtype: 'tool',
            itemId: 'enableBtn',
            hidden: true,
            disabled: true,
            cls: 'administration-tool',
            callback: 'onToggleActiveBtnClick',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
            },
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-enableBtn'
            },
            bind: {
                hidden: '{theAccount.active}',
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
            callback: 'onToggleActiveBtnClick',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
            },
            hidden: true,
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-disableBtn'
            },
            bind: {
                hidden: '{!theAccount.active}',
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
            itemId: 'defaultAccount',
            reference: 'defaultAccount',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
            },
            cls: 'administration-tool',
            bind: {
                hidden: '{!theAccount.default}',
                disabled: '{!toolAction._canUpdate}'
            },
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-defaultAccount'
            }
        },
        {
            xtype: 'tool',
            itemId: 'setDefaultAccount',
            reference: 'setDefaultAccount',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.emails.setdefaultaccount,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.setdefaultaccount'
            },
            cls: 'administration-tool',
            bind: {
                hidden: '{theAccount.default}',
                disabled: '{!toolAction._canUpdate}'
            },
            autoEl: {
                'data-testid': 'administration-emails-accounts-card-viewInRow-defaultAccount'
            }
        }
    ]
});
