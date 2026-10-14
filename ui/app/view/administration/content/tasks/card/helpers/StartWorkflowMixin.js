Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.SatrtWorkflowMixin', {
    mixinId: 'administration-task-startworkflowmixin',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    startworkflow: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    ctx.getNameInput(theVmObject, 'code'),
                    ctx.getDescriptionInput(theVmObject, 'description')
                ]),
                ctx.getRowFieldContainer([ctx.getTypeInput(theVmObject, 'type', true)]),
                ctx.getRowFieldContainer([
                    ctx.getRowFieldContainer([
                        ctx.getProcessesInput(theVmObject, 'config.classname'),
                        ctx.getTaskUserInput(theVmObject, 'config.username')
                    ]),

                    ctx.getRowFieldContainer([ctx.getProcessAttributesGridForWorkflow()], {
                        bind: {
                            hidden: '{!workflowClassName}'
                        }
                    }),
                    ctx.getRowFieldContainer([ctx.getProcessAttributesGridFormForWorkflow()], {
                        bind: {
                            hidden: '{!workflowClassName}'
                        }
                    })
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

        validateForm: function (form) {
            var me = this,
                _form = form.form,
                vm = form.lookupViewModel(),
                invalid = [];
            _form.getFields().items.forEach(function (field) {
                var fieldName = field.getName();

                switch (fieldName) {
                    case 'config.filter_regex_from':
                    case 'config.filter_regex_subject':
                        if (form.down('[name="config.filter_type"]').getValue() === 'regex') {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, false, _form);
                        } else {
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
        }
    }
});
