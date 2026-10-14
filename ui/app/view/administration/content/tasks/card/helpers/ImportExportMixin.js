Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.ImportExportMixin', {
    mixinId: 'administration-task-importexportmixin',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    importexport: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getNameInput(theVmObject, 'code'),
                    ctx.getDescriptionInput(theVmObject, 'description')
                ]),
                ctx.getRowFieldContainer([
                    ctx.getTypeInput(theVmObject, 'type'),
                    ctx.getTemplateInput(theVmObject, 'config.template')
                ])
            ];

            return items;
        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                /**
                 * Step 2 (per Type Import):
                 * Source type: combo con i valori File on server, Url.
                 * Directory: stringa visibile solo se Source type è File on server. Obbligatorio se visibile.
                 * File name: stringa visibile solo se Source type è File on server. Obbligatorio se visibile. Può essere una RegExp.
                 * Url: stringa visibile solo se Source type è Url. Obbligatorio se visibile.
                 **/
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer([ctx.getImportExportSourceInput(theVmObject, 'config.source')]),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getDirectoryInput(theVmObject, 'config.directory'),
                                ctx.getFilepatternInput(theVmObject, 'config.filePattern')
                            ],
                            {
                                bind: {
                                    hidden: '{!isSourceFile}'
                                }
                            }
                        ),
                        ctx.getRowFieldContainer([ctx.getUrlInput(theVmObject, 'config.url')], {
                            bind: {
                                hidden: '{!isSourceUrl}'
                            }
                        }),

                        ctx.getRowFieldContainer([
                            ctx.getPostImportActionInput(theVmObject, 'config.postImportAction', {
                                fieldcontainer: {
                                    bind: {
                                        hidden: '{!isSourceFile}'
                                    }
                                }
                            }),
                            ctx.getTargetDirectoryInput(theVmObject, 'config.targetDirectory', {
                                fieldcontainer: {
                                    bind: {
                                        hidden: '{!isMoveFiles}'
                                    }
                                }
                            })
                        ])
                    ],
                    {
                        bind: {
                            hidden: '{!isImport}'
                        }
                    }
                ),
                /**
                 * Step 2 (per Type Export):
                 * Directory: stringa. Obbligatorio.
                 * File name: stringa. Obbligatorio.
                 **/
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer([
                            ctx.getDirectoryInput(theVmObject, 'config.directory'),
                            ctx.getFilenameInput(theVmObject, 'config.fileName')
                        ])
                    ],
                    {
                        bind: {
                            hidden: '{!isExport}'
                        }
                    }
                )
            ];

            return items;
        },
        getCronPanel: function (theVmObject, step, data, ctx) {
            var items = [
                /**
                 * Cron: combo con valori: Every hour, Every day, Every month, Every year, Custom.
                 * Se Cron è Custom allora compariranno i campi per impostare il cron, come per i task asincroni in CMDBuild 2.5.
                 */
                ctx.getRowFieldContainer([ctx.getBasicCronInput(theVmObject, 'config.cronExpression')]),
                ctx.getRowFieldContainer([ctx.getAdvancedCronInput(theVmObject, 'config.cronExpression')], {
                    layout: 'fit'
                })
            ];

            return items;
        },

        getNotificationPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getNotificationInput(theVmObject, 'config.notificationMode'),
                    ctx.getAttachFileInput(theVmObject, 'config._attach_file')
                ]),
                ctx.getRowFieldContainer(
                    [
                        /*
                         * Error email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                         * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                         */
                        ctx.getRowFieldContainer(
                            [
                                ctx.getErrorEmailTemplateInput(theVmObject, 'config.errorTemplate'),
                                ctx.getErrorEmailAccountInput(theVmObject, 'config.errorAccount')
                            ],
                            {
                                bind: {
                                    hidden: '{!isImport}'
                                }
                            }
                        ),
                        /**
                         * email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                         * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                         */

                        ctx.getRowFieldContainer(
                            [
                                ctx.getNotificationEmailTemplateInput(theVmObject, 'config.notificationTemplate'),
                                ctx.getAttachImportReport(theVmObject, 'config.attachImportReport')
                            ],
                            {
                                bind: {
                                    hidden: '{!isImport}'
                                }
                            }
                        ),
                        /**
                         * email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                         * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                         */

                        ctx.getRowFieldContainer(
                            [
                                ctx.getEmailTemplateInput(theVmObject, 'config.emailTemplate'),
                                ctx.getEmailAccountInput(theVmObject, 'config.emailAccount')
                            ],
                            {
                                bind: {
                                    hidden: '{!isExport}'
                                }
                            }
                        )
                    ],
                    {
                        bind: {
                            hidden: '{isNeverNotification}'
                        },
                        hidden: true,
                        listeners: {
                            hide: function (component, eOpts) {
                                var input = component.down('#emailTemplate_input');
                                input.setValue('');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    true,
                                    input.up('form')
                                );
                            },
                            show: function (component, eOpts) {
                                var input = component.down('#emailTemplate_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    false,
                                    input.up('form')
                                );
                            }
                        }
                    }
                )
            ];

            return items;
        },

        validateForm: function (form) {
            var me = this,
                _form = form.form,
                vm = form.lookupViewModel(),
                invalid = [];
            _form.getFields().items.forEach(function (field) {
                var type = form.down('[name="type"]').getValue();
                var fieldName = field.getName();
                var isNeverNotification = form.getViewModel().get('isNeverNotification');

                switch (fieldName) {
                    case 'config.errorTemplate':
                        // case 'config.errorAccount':

                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    field,
                                    isNeverNotification,
                                    _form
                                );
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;
                    case 'config.source':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, false, _form);
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;

                    case 'config.postImportAction':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                if (form.down('[name="config.source"]').getValue() === 'file') {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        false,
                                        _form
                                    );
                                } else {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        true,
                                        _form
                                    );
                                }
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;

                    case 'config.url':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                if (form.down('[name="config.source"]').getValue() === 'url') {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        false,
                                        _form
                                    );
                                } else {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        true,
                                        _form
                                    );
                                }
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;
                    case 'config.directory':
                    case 'config.filePattern':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                if (form.down('[name="config.source"]').getValue() === 'url') {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        true,
                                        _form
                                    );
                                } else {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        false,
                                        _form
                                    );
                                }
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;

                    case 'config.targetDirectory':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                if (form.down('[name="config.postImportAction"]').getValue() === 'move_files') {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        false,
                                        _form
                                    );
                                } else {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        field,
                                        true,
                                        _form
                                    );
                                }
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;

                            default:
                                break;
                        }
                        break;

                    case 'config.fileName':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, false, _form);
                                break;

                            default:
                                break;
                        }
                        break;
                    case 'config.emailTemplate':
                        // case 'config.emailAccount':
                        switch (type) {
                            case CMDBuildUI.model.tasks.Task.types.import_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                                break;
                            case CMDBuildUI.model.tasks.Task.types.export_file:
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    field,
                                    isNeverNotification,
                                    _form
                                );
                                break;

                            default:
                                break;
                        }
                        break;

                    case 'advancedcron_minute':
                    case 'advancedcron_hour':
                    case 'advancedcron_day':
                    case 'advancedcron_month':
                    case 'advancedcron_dayofweek':
                        if (!form.getViewModel().get('isAdvancedCron')) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, _form);
                        }
                        break;
                    default:
                        break;
                }

                if (!field.validate()) {
                    invalid.push(field);
                }
            });
            Ext.resumeLayouts(true);

            invalid = CMDBuildUI.util.administration.helper.CronValidatorHelper.taskCronValidation(vm, invalid);
            if (invalid.length) {
                CMDBuildUI.util.administration.helper.FormHelper.showInvalidFieldsMessage({ items: invalid });
            }

            return invalid;
        },
        getFieldcontainerLabel: function (item) {
            var itemUp = item.up('fieldcontainer');
            var label = itemUp.getFieldLabel();
            if (!label) {
                return this.getFieldcontainerLabel(itemUp);
            }
            return label;
        }
    }
});
