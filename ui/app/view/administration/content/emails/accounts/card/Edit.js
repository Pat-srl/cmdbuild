Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.card.EditController',
        'CMDBuildUI.view.administration.content.emails.accounts.card.EditModel'
    ],

    alias: 'widget.administration-content-emails-accounts-card-edit',
    controller: 'administration-content-emails-accounts-card-edit',
    viewModel: {
        type: 'administration-content-emails-accounts-card-edit'
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
        layout: 'column',
        defaults: {
            columnWidth: 0.5
        },
        items: [{
            xtype: 'fieldcontainer',
            columnWidth: 1,
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textfield',
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                disabled: true,
                name: 'name',
                bind: {
                    value: '{theAccount.name}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.authtype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.authtype'
                },
                name: 'auth_type',
                valueField: 'value',
                displayField: 'label',
                queryMode: 'local',
                forceSelection: true,
                bind: {
                    store: '{authTypesStore}',
                    value: '{theAccount.auth_type}'
                }
            }]
        }, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [{
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-username'
                            });
                        }
                    },
                    columnWidth: 1,
                    name: 'username',
                    bind: {
                        value: '{theAccount.username}'
                    }
                }, {
                    xtype: 'textfield',
                    allowBlank: false,
                    columnWidth: 1,
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.address,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.address'
                    },
                    hidden: true,
                    bind: {
                        value: '{theAccount.address}',
                        hidden: '{theAccount.auth_type !== "ms_oauth"}'
                    },
                    listeners: {
                        hide: function (input) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                        },
                        show: function (input) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                        }
                    }
                }]
            }, {
                xtype: 'passwordfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                },
                columnWidth: 0.5,
                itemId: 'password',
                reference: 'password',
                name: 'password',
                hidden: true,
                bind: {
                    hidden: '{theAccount.auth_type !== "default"}',
                    value: '{theAccount.password}'
                }
            }, {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.token,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.token'
                },
                columnWidth: 0.5,
                hidden: true,
                grow: true,
                bind: {
                    hidden: '{theAccount.auth_type !== "google_oauth2"}',
                    value: '{theAccount.password}'
                },
                listeners: {
                    afterrender: function (textarea, eOpts) {
                        textarea.getEl().dom.querySelector('textarea').setAttribute('spellcheck', 'false');
                    },
                    hide: function (input) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                    },
                    show: function (input) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                    }
                }
            }, {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.token,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.token'
                },
                columnWidth: 0.5,
                hidden: true,
                bind: {
                    hidden: '{theAccount.auth_type !== "ms_oauth"}',
                    value: '{theAccount.password}'
                },
                grow: true,
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('magic', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.emails.generatetokenschema,
                labelToolIconClick: 'onGenerateTokenSchemaClick',
                listeners: {
                    afterrender: function (textarea, eOpts) {
                        textarea.getEl().dom.querySelector('textarea').setAttribute('spellcheck', 'false');
                    },
                    hide: function (input) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                    },
                    show: function (input) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                    }
                }
            }]
        }, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                active: {
                    bind: {
                        readOnly: '{theAccount.default}',
                        value: '{theAccount.active}'
                    }
                }
            })]
        }]

    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        layout: 'column',
        defaults: {
            columnWidth: 1,
            layout: 'column'
        },
        hidden: true,
        bind: {
            hidden: '{theAccount.auth_type === "ms_oauth"}'
        },
        title: CMDBuildUI.locales.Locales.administration.emails.outgoing,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.outgoing'
        },
        items: [{
            xtype: 'fieldcontainer',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'textfield',
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.address,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.address'
                },
                name: 'address',
                bind: {
                    value: '{theAccount.address}'
                }
            }]
        }, {
            xtype: 'fieldcontainer',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpserver,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpserver'
                },
                name: 'smtp_server',
                bind: {
                    value: '{theAccount.smtp_server}'
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpport'
                },
                name: 'smtp_port',
                bind: {
                    value: '{theAccount.smtp_port}'
                }
            }]
        }, {
            xtype: 'fieldcontainer',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.sentfolder,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.sentfolder'
                },
                name: 'imap_output_folder',
                bind: {
                    value: '{theAccount.imap_output_folder}'
                }
            }, {
                xtype: 'numberfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail,
                emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail',
                    emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                },
                name: 'maxAttachmentSizeForEmail',
                minValue: 0,
                bind: {
                    value: '{theAccount.maxAttachmentSizeForEmail}'
                }
            }]
        }, {
            xtype: 'fieldcontainer',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
                },
                itemId: 'smtp_ssl',
                name: 'smtp_ssl',
                bind: {
                    value: '{theAccount.smtp_ssl}'
                },
                listeners: {
                    afterrender: function () {
                        this.inputEl.dom.dataset.testid = 'administration-email-accounts-smtp_ssl';
                    }
                },
                getErrors: function (value) {
                    var me = this;
                    var errors = [];
                    if (value && me.up('form').down('#smtp_starttls').getValue()) {
                        errors.push(CMDBuildUI.locales.Locales.administration.emails.cannotchoosebothssltlsmessage);
                    }
                    if (errors.length) {
                        me.displayEl.addCls('x-form-invalid-field');
                    } else {
                        me.displayEl.removeCls('x-form-invalid-field');
                    }
                    return errors;
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
                },
                itemId: 'smtp_starttls',
                name: 'smtp_starttls',
                bind: {
                    value: '{theAccount.smtp_starttls}'
                },
                listeners: {
                    afterrender: function () {
                        this.inputEl.dom.dataset.testid = 'administration-email-accounts-smtp_starttls';
                    }
                },
                getErrors: function (value) {
                    var me = this;
                    var errors = [];
                    if (value && me.up('form').down('#smtp_ssl').getValue()) {
                        errors.push(CMDBuildUI.locales.Locales.administration.emails.cannotchoosebothssltlsmessage);
                    }
                    if (errors.length) {
                        me.displayEl.addCls('x-form-invalid-field');
                    } else {
                        me.displayEl.removeCls('x-form-invalid-field');
                    }
                    return errors;
                }
            }]
        }]

    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.emails.incoming,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.incoming'
        },
        hidden: true,
        bind: {
            hidden: '{theAccount.auth_type === "ms_oauth"}'
        },
        layout: 'column',
        defaults: {
            columnWidth: 0.5
        },
        items: [{
            xtype: 'textfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapserver,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapserver'
            },
            name: 'imap_server',
            bind: {
                value: '{theAccount.imap_server}'
            }
        }, {
            xtype: 'textfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapport,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapport'
            },
            name: 'imap_port',
            bind: {
                value: '{theAccount.imap_port}'
            }
        }, {
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
            },
            itemId: 'imap_ssl',
            name: 'imap_ssl',
            bind: {
                value: '{theAccount.imap_ssl}'
            },
            listeners: {
                afterrender: function () {
                    this.inputEl.dom.dataset.testid = 'administration-email-accounts-imap_ssl';
                }
            },
            getErrors: function (value) {
                var me = this;
                var errors = [];
                if (value && me.up('form').down('#imap_starttls').getValue()) {
                    errors.push(CMDBuildUI.locales.Locales.administration.emails.cannotchoosebothssltlsmessage);
                }
                if (errors.length) {
                    me.displayEl.addCls('x-form-invalid-field');
                } else {
                    me.displayEl.removeCls('x-form-invalid-field');
                }
                return errors;
            }
        }, {
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
            },
            itemId: 'imap_starttls',
            name: 'imap_starttls',
            bind: {
                value: '{theAccount.imap_starttls}'
            },
            listeners: {
                afterrender: function () {
                    this.inputEl.dom.dataset.testid = 'administration-email-accounts-imap_starttls';
                }
            },
            getErrors: function (value) {
                var me = this;
                var errors = [];
                if (value && me.up('form').down('#imap_ssl').getValue()) {
                    errors.push(CMDBuildUI.locales.Locales.administration.emails.cannotchoosebothssltlsmessage);
                }
                if (errors.length) {
                    me.displayEl.addCls('x-form-invalid-field');
                } else {
                    me.displayEl.removeCls('x-form-invalid-field');
                }
                return errors;
            }
        }]
    }],
    buttons: Ext.Array.merge([], ['->', {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.emails.testconfiguration,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.testconfiguration'
        },
        formBind: true,
        autoEl: {
            'data-testid': 'administration-email-testBtn'
        },
        itemId: 'testBtn',
        ui: 'administration-action-small'
    }], Ext.Array.removeAt(CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(), 0))
});