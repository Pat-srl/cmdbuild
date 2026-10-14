Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.ConnectorMixin', {
    mixinId: 'administration-task-importdatabasemixin',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    connector: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getNameInput(theVmObject, 'code'),
                    ctx.getDescriptionInput(theVmObject, 'description')
                ])
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

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getDatabaseGatesInput(theVmObject, 'config.gateconfig_handlers_0_target')
                ]),
                ctx.getRowFieldContainer([
                    ctx.getJdbcUrlInput(theVmObject, 'config.gateconfig_handlers_0_config_jdbcUrl')
                ]),
                ctx.getRowFieldContainer([
                    ctx.getJdbcUsernameInput(theVmObject, 'config.gateconfig_handlers_0_config_jdbcUsername'),
                    ctx.getJdbcPasswordInput(theVmObject, 'config.gateconfig_handlers_0_config_jdbcPassword')
                ])
            ];
            return items;
        },

        getNotificationPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([ctx.getNotificationInput(theVmObject, 'config.notificationMode')]),
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
                                    // hidden: '{!isImport}'
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
                                    // hidden: '{!isImport}'
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
                                var input = component.down('#errorTemplate_input');
                                if (input) {
                                    input.setValue('');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        input,
                                        true,
                                        input.up('form')
                                    );
                                }
                            },
                            show: function (component, eOpts) {
                                var input = component.down('#errorTemplate_input');
                                if (input) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        input,
                                        false,
                                        input.up('form')
                                    );
                                }
                            }
                        }
                    }
                )
            ];

            return items;
        },

        validateForm: function (form) {
            var _form = form.form,
                vm = _form.lookupViewModel(),
                invalid = [];
            _form.getFields().items.forEach(function (field) {
                if (!field.validate()) {
                    invalid.push(field);
                }
            });

            invalid = CMDBuildUI.util.administration.helper.CronValidatorHelper.taskCronValidation(vm, invalid);
            if (invalid.length) {
                CMDBuildUI.util.administration.helper.FormHelper.showInvalidFieldsMessage({ items: invalid });
            }

            return invalid;
        }
    }
});
