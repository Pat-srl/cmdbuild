Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.SendEmailMixin', {
    mixinId: 'administration-task-emailservicemixin',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    sendemail: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getNameInput(theVmObject, 'code'),
                    ctx.getDescriptionInput(theVmObject, 'description')
                ])
            ];
            return items;
        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getEmailAccountInput(theVmObject, 'config.email_account', {
                        allowBlank: false,
                        displayField: 'name',
                        valueField: 'name'
                    }),
                    ctx.getEmailTemplateInput(theVmObject, 'config.email_template')
                ]),

                ctx.getRowFieldContainer([ctx.getEmailContextVariablesGrid()], {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.emailvariables,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.emailvariables'
                    },
                    buttons: undefined,
                    layout: 'fit'
                }),
                // attach report
                ctx.getRowFieldContainer([ctx.getAttachReportInput(theVmObject, 'config.attach_report_enabled')]),
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer([
                            ctx.getReportInput(theVmObject, 'config.attach_report_code'),
                            ctx.getReportFormatInput(theVmObject, 'config.attach_report_format')
                        ]),
                        ctx.getRowFieldContainer([ctx.getReportParametersGrid()], {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.reportparameters,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.reportparameters'
                            },
                            buttons: undefined,
                            layout: 'fit'
                        })
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{!attachReport}'
                        },
                        listeners: {
                            hide: function (fieldcontainer) {
                                var attachReportCode = fieldcontainer.down('#attach_report_code_input');
                                attachReportCode.setValue(null);
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    attachReportCode,
                                    true,
                                    attachReportCode.up('form')
                                );
                                var attachReportFormat = fieldcontainer.down('#attach_report_format_input');
                                attachReportFormat.setValue(null);
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    attachReportFormat,
                                    true,
                                    attachReportCode.up('form')
                                );
                            },
                            show: function (fieldcontainer) {
                                var attachReportCode = fieldcontainer.down('#attach_report_code_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    attachReportCode,
                                    false,
                                    attachReportCode.up('form')
                                );
                                var attachReportFormat = fieldcontainer.down('#attach_report_format_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    attachReportFormat,
                                    false,
                                    attachReportCode.up('form')
                                );
                            }
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
        validateForm: function (form) {
            var _form = form.form,
                vm = form.lookupViewModel(),
                invalid = [];
            _form.getFields().items.forEach(function (field) {
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
