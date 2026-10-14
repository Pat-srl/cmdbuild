Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper', {
    singleton: true,

    getGeneralPropertyFieldset: function (viewType) {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            layout: 'column',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            defaults: {
                columnWidth: 0.5
            },
            items: [
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getNameInput(
                        'theTemplate',
                        'name'
                    ),
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getDescriptionInput(
                        'theTemplate',
                        'description'
                    )
                ]),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getKeepSyncInput(
                            'theTemplate',
                            'keepSynchronization'
                        ),
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getPromptSyncInput(
                            'theTemplate',
                            'promptSynchronization'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getDelayInput(
                            'theTemplate',
                            'delay'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getActiveInput(
                        'theTemplate',
                        'active'
                    )
                ])
            ]
        };
    },

    getTemplateFieldset: function (viewType) {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            layout: 'column',
            defaults: {
                columnWidth: 1
            },
            title: CMDBuildUI.locales.Locales.administration.emails.template,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.template'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getAccountInput(
                            'theTemplate',
                            'account'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getContentTypeInput(
                            'theTemplate',
                            'contentType'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getFromInput(
                            'theTemplate',
                            'from'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getToInput(
                        'theTemplate',
                        'to'
                    )
                ]),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getCCInput(
                            'theTemplate',
                            'cc'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getBCCInput(
                            'theTemplate',
                            'bcc'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getSubjectInput(
                        'theTemplate',
                        'subject'
                    )
                ]),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getBodyInput(
                            'theTemplate',
                            'body',
                            viewType
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider || !isTemplateHTML}'
                        }
                    }
                ),

                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getBodyTextareaInput(
                            'theTemplate',
                            'body',
                            viewType
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{isEmailProvider && isTemplateHTML}'
                        }
                    }
                ),
                CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow(
                    [
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getSignatureInput(
                            'theTemplate',
                            'signature'
                        )
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!isEmailProvider}'
                        }
                    }
                )
            ]
        };
    },
    getNameInput: function (vmKeyObject, property) {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
            {
                name: {
                    vtype: 'nameInputValidationWithDash',
                    allowBlank: false,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
                    }
                }
            },
            true,
            '[name="description"]'
        );
    },

    getDescriptionInput: function (vmKeyObject, property) {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
            description: {
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
                },
                fieldcontainer: {
                    userCls: 'with-tool',
                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateDescriptionClick'
                }
            }
        });
    },

    getKeepSyncInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.keepsync,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.keepsync'
                }
            },
            bind: {
                readOnly: '{actions.view}',
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(property, config);
    },

    getPromptSyncInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.promptsync,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.promptsync'
            },
            name: 'promptSynchronization',
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(property, config);
    },

    getDelayInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.delay,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.delay'
                }
            },
            combofield: {
                displayField: 'label',
                valueField: 'value',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property),
                    store: '{delaylist}'
                }
            },
            displayfield: {
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
                },
                renderer: function (value) {
                    if (typeof value != 'undefined') {
                        var store = this.lookupViewModel().get('delaylist');
                        if (store) {
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                    }
                    return value;
                }
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(property, config);
    },

    getActiveInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                }
            },
            bind: {
                readOnly: '{actions.view}',
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(property, config);
    },

    getAccountInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
                }
            },
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property),
                store: '{allEmailAccounts}'
            },
            displayField: 'name',
            valueField: '_id',
            triggers: {
                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(property, config);
    },
    getContentTypeInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.contenttype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.contenttype'
                }
            },
            displayField: 'label',
            valueField: 'value',
            store: 'administration.emails.ContentTypes',
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(property, config);
    },

    getFromInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.from,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.from'
                }
            },

            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getToInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.to,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.to'
                }
            },

            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },
    getCCInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.cc,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.cc'
                }
            },

            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },
    getBCCInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.bcc,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.bcc'
                }
            },

            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getSubjectInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.subject,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                },
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateSubjectClick'
            },

            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getBodyInput: function (vmKeyObject, property, viewType) {
        var editor;
        if (!viewType) {
            editor = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                padding: '0 15 0 0',
                minHeight: 500,
                columnWidth: 1,
                enableSignature: true,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property),
                    readOnly: '{actions.view}'
                }
            });
        } else {
            editor = {
                // view
                xtype: 'displayfield',
                name: 'defaultexporttemplate',
                hidden: true,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property),
                    hidden: '{!actions.view}'
                }
            };
        }

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.body'
            },
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
            labelToolIconClick: 'onTranslateBodyClick',
            items: [editor]
        };
    },

    getBodyTextareaInput: function (vmKeyObject, property, viewType) {
        var editor = CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(property, {
            body: {
                columnWidth: 1,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, property),
                    readOnly: '{actions.view}'
                }
            }
        });

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.body'
            },
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
            labelToolIconClick: 'onTranslateBodyClick',
            items: [editor]
        };
    },

    getSignatureInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.emails.signature,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.signature'
                }
            },
            displayField: 'description',
            valueField: '_id',
            bind: {
                store: '{signaturesStore}',
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };

        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(property, config);
    },

    getLangExprInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.languageexpression,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.languageexpression'
                }
            },
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getActionInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.action,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.action'
                },
                hidden: true,
                bind: {
                    hidden: '{isEmailProvider}'
                }
            },
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getActionLabelInput: function (vmKeyObject, property) {
        var config = {};
        config[property] = {
            fieldcontainer: {
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.actionLabel,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.actionLabel'
                },
                hidden: true,
                bind: {
                    hidden: '{!isInAppNotificationProvider}'
                },
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateActionLabelClick'
            },
            bind: {
                value: Ext.String.format('{{0}.{1}}', vmKeyObject, property)
            }
        };
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(property, config);
    },

    getMetadataFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.emails.metadata,
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.metadata'
            },
            items: [
                this.getRow([this.getLangExprInput('theTemplate', 'cm_lang_expr')]),
                this.getRow([
                    this.getActionInput('theTemplate', 'action'),
                    this.getActionLabelInput('theTemplate', 'actionLabel')
                ]),
                {
                    xtype: 'grid',
                    columnWidth: 1,
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'keyValueGrid',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    viewConfig: {
                        markDirty: false
                    },
                    plugins: {
                        ptype: 'actionColumnRowEditing',
                        id: 'actionColumnRowEditing',
                        hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                        clicksToEdit: 10,
                        buttonsUi: 'button-like-tool',
                        errorSummary: false
                    },
                    controller: {
                        control: {
                            '#': {
                                edit: function (editor, context, eOpts) {
                                    context.record.set('key', editor.editor.items.items[0].getValue());
                                    context.record.set('value', editor.editor.items.items[1].getValue());
                                },
                                beforeedit: function (editor, context, eOpts) {
                                    if (editor.view.lookupViewModel().get('actions.view')) {
                                        return false;
                                    }
                                    Ext.defer(function () {
                                        if (editor.editor && editor.editor.el) {
                                            editor.editor.el.setStyle('background-color', '#ffffff');
                                        }
                                    }, 10);
                                }
                            }
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-content-notificationtemplates-metadata-grid'
                    },

                    forceFit: true,
                    loadMask: true,

                    labelWidth: 'auto',
                    bind: {
                        store: '{keyvaluedataStore}'
                    },
                    columns: [
                        {
                            flex: 1,
                            text: CMDBuildUI.locales.Locales.administration.emails.key,
                            dataIndex: 'key',
                            align: 'left',
                            editor: {
                                xtype: 'textfield',
                                height: 19,
                                minHeight: 19,
                                maxHeight: 19,
                                padding: 0,
                                ui: 'reordergrid-editor-combo'
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.emails.value,
                            flex: 1,
                            dataIndex: 'value',
                            align: 'left',
                            editor: {
                                xtype: 'textfield',
                                height: 19,
                                minHeight: 19,
                                maxHeight: 19,
                                padding: 0,
                                ui: 'reordergrid-editor-combo'
                            }
                        },
                        {
                            xtype: 'actioncolumn',
                            itemId: 'actionColumnEdit',
                            bind: {
                                hidden: '{actions.view}'
                            },
                            width: 30,
                            minWidth: 30, // width property not works. Use minWidth.
                            maxWidth: 30,
                            align: 'center',
                            items: [
                                {
                                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                                        grid.editingPlugin.startEdit(record, 1);
                                    },
                                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                        return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        if (record.get('editing')) {
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                        }
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'actioncolumn',
                            itemId: 'actionColumnCancel',
                            bind: {
                                hidden: '{actions.view}'
                            },
                            width: 30,
                            minWidth: 30, // width property not works. Use minWidth.
                            maxWidth: 30,
                            align: 'center',
                            items: [
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove, // Remove
                                    localized: {
                                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                                    },
                                    handler: function (grid, rowIndex, colIndex) {
                                        var store = grid.getStore();
                                        var record = store.getAt(rowIndex);
                                        store.remove(record);
                                        grid.refresh();
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        metadata.value = Ext.String.insert(
                                            metadata.value,
                                            Ext.String.format(
                                                ' data-testid="administration-content-notificationtemplates-metadata-grid-removeBtn-{0}"',
                                                rowIndex
                                            ),
                                            -7
                                        );
                                        if (record.get('editing')) {
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                                        }
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    margin: '20 0 20 0',
                    xtype: 'grid',
                    columnWidth: 1,
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    hidden: true,
                    cls: 'administration-reorder-grid',
                    itemId: 'importExportAttributeGridNew',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    layout: 'hbox',
                    autoEl: {
                        'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
                    },

                    forceFit: true,
                    loadMask: true,

                    labelWidth: 'auto',
                    bind: {
                        store: '{newKeyvaluedataStore}',
                        hidden: '{actions.view}'
                    },

                    columns: [
                        {
                            xtype: 'widgetcolumn',
                            dataIndex: 'key',
                            flex: 1,
                            widget: {
                                xtype: 'textfield',
                                queryMode: 'local',
                                itemId: 'newKey',
                                autoEl: {
                                    'data-testid': 'administration-content-importexport-datatemplates-grid'
                                },
                                height: 19,
                                minHeight: 19,
                                maxHeight: 19,
                                padding: 0,
                                ui: 'reordergrid-editor-combo'
                            }
                        },
                        {
                            xtype: 'widgetcolumn',
                            dataIndex: 'value',
                            flex: 1,
                            widget: {
                                xtype: 'textfield',
                                itemId: 'newValue',
                                height: 19,
                                minHeight: 19,
                                maxHeight: 19,
                                padding: 0,
                                ui: 'reordergrid-editor-combo',
                                autoEl: {
                                    'data-testid': 'administration-content-importexport-datatemplates-grid'
                                }
                            }
                        },
                        {
                            xtype: 'actioncolumn',
                            itemId: 'actionColumnMoveDownNew',
                            width: 30,
                            minWidth: 30,
                            maxWidth: 30,
                            align: 'center',
                            items: [
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                    disabled: true
                                }
                            ]
                        },
                        {
                            xtype: 'actioncolumn',
                            itemId: 'actionColumnAddNew',
                            width: 30,
                            minWidth: 30,
                            maxWidth: 30,
                            align: 'center',
                            items: [
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
                                    localized: {
                                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-importexport-attribute-addBtn'
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        metadata.value = Ext.String.insert(
                                            metadata.value,
                                            ' data-testid="administration-content-importexport-datatemplates-grid-addBtn"',
                                            -7
                                        );
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid');
                                    },

                                    handler: function (button, rowIndex, colIndex) {
                                        var key = button.up('grid').down('#newKey');
                                        var value = button.up('grid').down('#newValue');

                                        if (Ext.isEmpty(key.getValue())) {
                                            key.focus();
                                            return false;
                                        }
                                        if (Ext.isEmpty(value.getValue())) {
                                            value.focus();
                                            return false;
                                        }
                                        Ext.suspendLayouts();
                                        var mainGrid = button.up('form').down('#keyValueGrid');
                                        var vm = button.lookupViewModel();
                                        var keyvaluedataStore = vm.get('keyvaluedataStore');
                                        var newKeyValue = CMDBuildUI.model.base.KeyValue.create({
                                            key: key.getValue(),
                                            value: value.getValue(),
                                            index: keyvaluedataStore.getRange().length
                                        });
                                        keyvaluedataStore.add(newKeyValue);
                                        key.reset();
                                        value.reset();
                                        Ext.resumeLayouts();
                                        mainGrid.getView().refresh();
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getShowOnClassesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.emails.availability,
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.availability'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('reportFormat', {
                            reportFormat: {
                                fieldcontainer: {}, // config for fieldcontainer
                                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.availableon,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.availableon'
                                },
                                displayField: 'label',
                                valueField: 'value',

                                bind: {
                                    value: '{showOnAllClasses}',
                                    store: '{showOnClassesStore}'
                                },
                                listeners: {
                                    change: function (combo, newValue, oldValue) {
                                        var vm = combo.lookupViewModel();
                                        if (newValue === 'noone') {
                                            vm.set('theTemplate.showOnClasses', 'noone');
                                        } else if (newValue === 'true') {
                                            vm.set('theTemplate.showOnClasses', '');
                                        }
                                    }
                                }
                            }
                        })
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    hidden: true,
                    bind: {
                        hidden: '{showOnAllClasses != "false"}'
                    },
                    items: [
                        {
                            xtype: 'panel',
                            columnWidth: 1,
                            hidden: true,
                            bind: {
                                hidden: '{!actions.view || theTemplate.showOnClasses === null || theTemplate.showOnClasses == "noone"}'
                            },
                            html: '',
                            controller: {
                                control: {
                                    '#': {
                                        beforerender: function (view) {
                                            var vm = view.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: '{showOnClassesViewStore}'
                                                },
                                                function (showOnClassesViewStore) {
                                                    var html = '';
                                                    showOnClassesViewStore.each(function (item) {
                                                        html += Ext.String.format(
                                                            '<li>{0}</li>',
                                                            item.get('description')
                                                        );
                                                    });
                                                    html = Ext.String.format('<ul>{0}</ul>', html);
                                                    view.setHtml(html);
                                                }
                                            );
                                        }
                                    }
                                }
                            }
                        },
                        {
                            columnWidth: 1,
                            xtype: 'treepanel',
                            hidden: true,
                            controller: {
                                control: {
                                    '#': {
                                        afterrender: 'onAfterRender'
                                    }
                                },

                                onAfterRender: function (treepanel) {
                                    var vm = treepanel.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: '{theTemplate}'
                                        },
                                        function (theTemplate) {
                                            CMDBuildUI.model.classes.Class.load(
                                                CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                                                {
                                                    success: function (classModel) {
                                                        CMDBuildUI.model.processes.Process.load(
                                                            CMDBuildUI.util.helper.ModelHelper.systemClasses.process,
                                                            {
                                                                success: function (processModel) {
                                                                    var processTree = [];
                                                                    processTree = processModel.getChildrenAsTree(
                                                                        true,
                                                                        function (item) {
                                                                            item.set(
                                                                                'enabled',
                                                                                theTemplate
                                                                                    .get('showOnClasses')
                                                                                    .indexOf(item.get('name')) !== -1
                                                                            );
                                                                            return item;
                                                                        },
                                                                        false
                                                                    );
                                                                    // generate the destination tree
                                                                    var processRoot = {
                                                                        expanded: true,
                                                                        text: CMDBuildUI.locales.Locales.administration
                                                                            .navigation.processes,
                                                                        name: processModel.get('name'),
                                                                        leaf: processTree.length >= 1 ? false : true,
                                                                        children:
                                                                            processTree.length >= 1
                                                                                ? processTree
                                                                                : false,
                                                                        enabled: false
                                                                    };
                                                                    var classTree = [];
                                                                    classTree = classModel.getChildrenAsTree(
                                                                        true,
                                                                        function (item) {
                                                                            item.set(
                                                                                'enabled',
                                                                                theTemplate
                                                                                    .get('showOnClasses')
                                                                                    .indexOf(item.get('name')) !== -1
                                                                            );
                                                                            return item;
                                                                        },
                                                                        false
                                                                    );
                                                                    // generate the destination tree
                                                                    var classRoot = {
                                                                        expanded: true,
                                                                        text: CMDBuildUI.locales.Locales.administration
                                                                            .navigation.classes,
                                                                        name: classModel.get('name'),
                                                                        leaf: classTree.length > 1 ? false : true,
                                                                        children:
                                                                            classTree.length > 1 ? classTree : false,
                                                                        enabled: false
                                                                    };

                                                                    var root = {
                                                                        expanded: true,
                                                                        text: root,
                                                                        name: 'Root',
                                                                        leaf: false,
                                                                        children: [classRoot, processRoot],
                                                                        enabled: false
                                                                    };
                                                                    treepanel.getStore().setRoot(root);
                                                                }
                                                            }
                                                        );
                                                    }
                                                }
                                            );
                                        }
                                    );
                                }
                            },
                            layout: 'fit',
                            viewConfig: {
                                markDirty: false
                            },
                            ui: 'administration-navigation-tree',
                            bind: {
                                store: '{classesStore}',
                                hidden: '{actions.view}'
                            },
                            rootVisible: false,
                            columns: [
                                {
                                    xtype: 'checkcolumn',
                                    width: 75,
                                    dataIndex: 'enabled',
                                    bind: {
                                        disabled: '{actions.view}'
                                    },
                                    listeners: {
                                        beforecheckchange: function (checkbox, rowIndex, checked, record, e, eOpts) {
                                            return !(record.get('children') && record.get('children').length);
                                        },
                                        checkchange: function (column, recordIndex, checked) {
                                            var theTemplate = this.getView().lookupViewModel().get('theTemplate');
                                            var showOnClasses =
                                                theTemplate.get('showOnClasses').length &&
                                                theTemplate.get('showOnClasses') !== 'noone'
                                                    ? theTemplate.get('showOnClasses').split(',')
                                                    : [];
                                            var storeRecordName = this.getView()
                                                .getStore()
                                                .getAt(recordIndex)
                                                .get('name');
                                            var showOnClassesIndex = showOnClasses.indexOf(storeRecordName);
                                            if (checked && showOnClassesIndex === -1) {
                                                showOnClasses.push(storeRecordName);
                                            } else if (!checked && showOnClassesIndex > -1) {
                                                showOnClasses.splice(showOnClassesIndex, 1);
                                            }
                                            theTemplate.set('showOnClasses', showOnClasses);
                                        }
                                    },
                                    renderer: function (value, cell, record, rowIndex, colIndex, store, view) {
                                        var config = this.defaultRenderer(value, cell);
                                        if (record.get('children') && record.get('children').length) {
                                            return;
                                        }
                                        return config;
                                    }
                                },
                                {
                                    xtype: 'treecolumn',
                                    text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                                    },
                                    dataIndex: 'text',
                                    align: 'left',
                                    flex: 1
                                }
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getRow: function (items, config) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'container',
                layout: 'column',
                cls: 'row-container',
                columnWidth: 1
            },
            config || {}
        );
        if (items && items.length) {
            fieldcontainer.items = items;
        }
        return fieldcontainer;
    }
});
