Ext.define('CMDBuildUI.view.emails.email.Mixin', {
    mixinId: 'email-mixin',

    config: {
        modelValidation: true,

        /**
         * @cfg {String} storeurl
         */
        storeurl: null,

        // create/edit form
        items: [
            {
                xtype: 'container',
                hidden: true,
                bind: {
                    hidden: '{hideWarningMessage}',
                    html: '{warningMessage}'
                },
                margin: 10,
                ui: 'messagewarning'
            },
            {
                xtype: 'fieldcontainer',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                forceFit: true,
                layout: 'fit',
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [
                    {
                        layout: 'column',
                        items: [
                            {
                                xtype: 'combobox',
                                itemId: 'templatecombo',
                                fieldLabel: CMDBuildUI.locales.Locales.emails.composefromtemplate,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.composefromtemplate'
                                },
                                queryMode: 'local',
                                forceSelection: true,
                                displayField: '_description_translation',
                                valueField: '_id',
                                bind: {
                                    store: '{templates}',
                                    value: '{theEmail.template}',
                                    disabled: '{disabled.templatechoice}'
                                },
                                columnWidth: 0.4
                            },
                            {
                                xtype: 'checkboxfield',
                                fieldLabel: CMDBuildUI.locales.Locales.emails.keepsynchronization,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.keepsynchronization'
                                },
                                padding: '0 0 0 10',
                                bind: {
                                    value: '{theEmail.keepSynchronization}',
                                    disabled: '{disabled.keepsync}'
                                },
                                columnWidth: 0.2
                            },
                            {
                                xtype: 'combobox',
                                reference: 'delay',
                                fieldLabel: CMDBuildUI.locales.Locales.emails.delay,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.delay'
                                },
                                displayField: 'label',
                                valueField: 'value',
                                bind: {
                                    store: '{delays}',
                                    value: '{theEmail.delay}'
                                },
                                columnWidth: 0.4
                            }
                        ]
                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.emails.from,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.from'
                        },
                        bind: {
                            value: '{theEmail.from}'
                        }
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.emails.to,
                        vtype: 'multiemail',
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.to'
                        },
                        bind: {
                            value: '{theEmail.to}'
                        },
                        listeners: {
                            change: function (field, newValue, oldValue, eOpts) {
                                field.setValue(newValue.replace(/[\s\u200B-\u200D\uFEFF]/g, ''));
                            }
                        }
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.emails.cc,
                        vtype: 'multiemail',
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.cc'
                        },
                        bind: {
                            value: '{theEmail.cc}'
                        },
                        listeners: {
                            change: function (field, newValue, oldValue, eOpts) {
                                field.setValue(newValue.replace(/[\s\u200B-\u200D\uFEFF]/g, ''));
                            }
                        }
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.emails.bcc,
                        vtype: 'multiemail',
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.bcc'
                        },
                        bind: {
                            value: '{theEmail.bcc}'
                        },
                        listeners: {
                            change: function (field, newValue, oldValue, eOpts) {
                                field.setValue(newValue.replace(/[\s\u200B-\u200D\uFEFF]/g, ''));
                            }
                        }
                    },
                    {
                        xtype: 'textfield',
                        allowBlank: false,
                        fieldLabel: CMDBuildUI.locales.Locales.emails.subject,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.subject'
                        },
                        bind: {
                            value: '{theEmail.subject}'
                        }
                    },
                    CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                        reference: 'body',
                        allowBlank: false,
                        fieldLabel: CMDBuildUI.locales.Locales.emails.message,
                        enableSignature: true,
                        updateSignature: true,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.message'
                        },
                        hidden: true,
                        bind: {
                            value: '{theEmail._content_html}',
                            disabled: '{theEmail.contentType !== "text/html" && !!theEmail.contentType }',
                            hidden: '{theEmail.contentType !== "text/html" && !!theEmail.contentType }'
                        }
                    }),
                    {
                        xtype: 'textareafield',
                        resizable: {
                            handles: 's'
                        },
                        allowBlank: false,
                        fieldLabel: CMDBuildUI.locales.Locales.emails.message,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.message'
                        },
                        hidden: true,
                        bind: {
                            value: '{theEmail._content_plain}',
                            disabled: '{theEmail.contentType !== "text/plain"}',
                            hidden: '{theEmail.contentType !== "text/plain"}'
                        }
                    },
                    {
                        xtype: 'combobox',
                        itemId: 'signaturefield',
                        fieldLabel: CMDBuildUI.locales.Locales.emails.signature,
                        displayField: '_description_translation',
                        valueField: '_id',
                        queryMode: 'local',
                        forceSelection: true,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.emails.signature'
                        },
                        triggers: {
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function (combo, trigger, eOpts) {
                                    combo.setValue();
                                }
                            }
                        },
                        bind: {
                            value: '{theEmail.signature}',
                            store: '{signatures}'
                        }
                    },
                    {
                        padding: '15 0 15 0',
                        layout: 'column',
                        hidden: true,
                        bind: {
                            hidden: '{addAttachmentsHidden}'
                        },
                        items: [
                            {
                                xtype: 'filefield',
                                buttonOnly: true,
                                itemId: 'addfileattachment',
                                ui: 'secondary-action',
                                buttonConfig: {
                                    text: CMDBuildUI.locales.Locales.emails.attachfile,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.emails.attachfile'
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                margin: '0 0 0 15',
                                itemId: 'addattachmentsfromdocumentarchive',
                                reference: 'addattachmentsfromdocumentarchive',
                                text: CMDBuildUI.locales.Locales.emails.addattachmentsfromdocumentarchive,
                                ui: 'secondary-action',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.emails.addattachmentsfromdocumentarchive'
                                }
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'panel',
                padding: '0 10 0 10',
                bind: {
                    hidden: '{!attachmentsSizeLimit.error}'
                },
                items: [
                    {
                        xtype: 'formvalidatorfield',
                        itemId: 'validationField',
                        bind: {
                            value: '{attachmentsSizeLimit.error}',
                            hidden: '{!attachmentsSizeLimit.error}',
                            errorMessage: '{attachmentsSizeLimit.error}'
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                hidden: true,
                bind: {
                    hidden: '{hideEmailTplAttachmentsMsg}'
                },
                html: CMDBuildUI.locales.Locales.emails.templateattachments,
                localized: {
                    html: 'CMDBuildUI.locales.Locales.emails.templateattachments'
                },
                margin: 10,
                ui: 'messageinfo'
            },
            {
                xtype: 'panel',
                layout: 'fit',
                hidden: true,
                bind: {
                    hidden: '{!attachmentsTotalCount}'
                },
                listeners: {
                    show: function () {
                        var popup = this.up('panel');
                        Ext.asap(function () {
                            popup.updateLayout();
                        });
                    }
                },
                items: [
                    {
                        xtype: 'grid',
                        reference: 'attachmentsgrid',
                        itemId: 'attachmentsgrid',
                        forceFit: true,
                        columns: [
                            {
                                text: CMDBuildUI.locales.Locales.attachments.filename,
                                dataIndex: 'name',
                                align: 'left',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.attachments.filename'
                                },
                                flex: 1
                            },
                            {
                                text: CMDBuildUI.locales.Locales.attachments.description,
                                dataIndex: 'description',
                                align: 'left',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.attachments.description'
                                },
                                flex: 1
                            },
                            {
                                text: CMDBuildUI.locales.Locales.emails.size,
                                align: 'left',
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.emails.size'
                                },
                                width: 100,
                                renderer: function (value, grid, record) {
                                    return Ext.util.Format.fileSize(
                                        record.get('_file') ? record.get('_file').size : record.get('Size')
                                    );
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.attachments.preview,
                                width: 95,
                                menuDisabled: true,
                                xtype: 'widgetcolumn',
                                widget: {
                                    xtype: 'dms-preview',
                                    bind: {
                                        attachmentId: '{record._id}',
                                        fileName: '{record.name}',
                                        fileMimeType: '{record.MimeType}',
                                        proxyUrl: '{attachmentsstore.urlPreview}'
                                    },
                                    alt: CMDBuildUI.locales.Locales.attachments.preview,
                                    localized: {
                                        alt: 'CMDBuildUI.locales.Locales.attachments.preview'
                                    }
                                },
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.attachments.preview'
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                minWidth: 60,
                                maxWidth: 60,
                                align: 'right',
                                items: [
                                    {
                                        iconCls:
                                            'attachments-grid-action ' +
                                            CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                                        getTip: function () {
                                            return CMDBuildUI.locales.Locales.attachments.download;
                                        },
                                        handler: function (grid, rowIndex, colIndex) {
                                            var attachmentStore = grid.getStore(),
                                                record = attachmentStore.getAt(rowIndex),
                                                url = Ext.String.format(
                                                    '{0}/{1}/{2}',
                                                    attachmentStore.getProxy().getUrl(), // base url
                                                    record.getId(), // attachment id
                                                    record.get('name') // file name
                                                );
                                            CMDBuildUI.util.File.download(url, record.get('name'));
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return record.get('newAttachment');
                                        }
                                    },
                                    {
                                        iconCls:
                                            'attachments-grid-action ' +
                                            CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                                        getTip: function () {
                                            return CMDBuildUI.locales.Locales.attachments.deleteattachment;
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, event, record) {
                                            grid.getStore().remove(record);
                                        }
                                    }
                                ]
                            }
                        ],
                        bind: {
                            store: '{attachments}'
                        }
                    }
                ]
            }
        ],

        buttons: [
            {
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                itemId: 'cancelBtn',
                ui: 'secondary-action',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                }
            },
            {
                text: CMDBuildUI.locales.Locales.common.actions.save,
                itemId: 'saveBtn',
                disabled: true,
                bind: {
                    disabled: '{disableSaveButton}'
                },
                ui: 'management-primary',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            }
        ]
    },

    /**
     * Add attachment from file
     *
     * @param {Ext.form.field.File} filefield
     */
    addFileAttachment: function (filefield) {
        var store = this.lookupViewModel().getStore('attachments');

        if (filefield.fileInputEl.dom.files.length) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            Ext.Array.from(filefield.fileInputEl.dom.files).forEach(function (file) {
                if (store.findRecord('name', file.name)) {
                    var w = Ext.create('Ext.window.Toast', {
                        title: CMDBuildUI.locales.Locales.notifier.warning,
                        html: CMDBuildUI.locales.Locales.emails.alredyexistfile,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-circle', 'solid'),
                        align: 'br',
                        alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++
                    });
                    w.show();
                } else {
                    store.add([
                        {
                            name: file.name,
                            _modified: file.lastModifiedDate,
                            _file: file,
                            DMSAttachment: false,
                            newAttachment: true
                        }
                    ]);
                }
            });

            filefield.suspendEvent('change');
            filefield.reset();
            filefield.resumeEvent('change');
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        }
    },

    /**
     * Add attacmhent from database
     */
    addDmsAttachment: function () {
        var vm = this.lookupViewModel(),
            objectTypeName = vm.get('objectdata._type');

        CMDBuildUI.util.Utilities.openPopup(
            'popup-add-attachmentfromdms-panel',
            CMDBuildUI.locales.Locales.emails.dmspaneltitle,
            {
                xtype: 'emails-dmsattachments-panel',
                store: vm.getStore('attachments'),
                viewModel: {
                    data: {
                        objectType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName),
                        objectTypeName: objectTypeName,
                        objectId: vm.get('isAsync') ? null : vm.get('objectdata._id')
                    }
                }
            }
        );
    },

    /**
     *
     * @param {CMDBuildUI.model.base.ComboItem} template
     */
    updateEmailFromTemplate: function (template) {
        const vm = this.lookupViewModel();

        if (template) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            const theEmail = vm.get('theEmail');

            // update card data
            const objectData = vm.get('objectdata');
            if (vm.get('isAsync')) {
                const id = objectData._id;
                delete objectData._id;
                Ext.Array.forEach(vm.get('fileAttributesOfProcess'), function (item, index, allitems) {
                    delete objectData[item];
                });
                const url = theEmail.getProxy().getUrl().replace(id, '_ANY');
                theEmail.getProxy().setUrl(url);
            }
            theEmail.set('_card', objectData);

            // update and save email from template
            theEmail.save({
                params: {
                    apply_template: true,
                    template_only: true
                },
                success: function (record) {
                    record.phantom = true;
                },
                callback: function (record, operation, success) {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            });
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.emails.Template} record
     */
    fetchAttachmentsSizeLimit: function (record) {
        var vm = this.lookupViewModel(),
            defaultSizeLimit =
                Number(
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.email.maxattachmentsize)
                ) *
                1024 *
                1024;

        if (Ext.isEmpty(record.get('account'))) {
            vm.set('attachmentsSizeLimit.maxAttachmentSize', defaultSizeLimit);
        } else {
            CMDBuildUI.model.emails.Account.load(record.get('account'), {
                scope: this,
                failure: function (record, operation) {
                    vm.set('attachmentsSizeLimit.maxAttachmentSize', defaultSizeLimit);
                },
                success: function (record, operation) {
                    var maxAttachmentSizeForEmail = Ext.isEmpty(record.get('maxAttachmentSizeForEmail'))
                        ? 0
                        : record.get('maxAttachmentSizeForEmail') * 1024 * 1024;
                    vm.set(
                        'attachmentsSizeLimit.maxAttachmentSize',
                        maxAttachmentSizeForEmail && maxAttachmentSizeForEmail > defaultSizeLimit
                            ? maxAttachmentSizeForEmail
                            : defaultSizeLimit
                    );
                }
            });
        }
    }
});
