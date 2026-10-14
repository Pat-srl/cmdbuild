Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.WaterWayMixin', {
    mixinId: 'administration-task-waterwaymixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'
    ],
    requires: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    waterway: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getNameInput(theVmObject, 'code'),
                        ctx.getDescriptionInput(theVmObject, 'description')
                    ]
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
                ctx.getRowFieldContainer(
                    [
                        ctx.getBasicCronInput(theVmObject, 'config.cronExpression')
                    ]
                ),
                ctx.getRowFieldContainer(
                    [
                        ctx.getAdvancedCronInput(theVmObject, 'config.cronExpression')
                    ], {
                    layout: 'fit'
                }
                )
            ];

            return items;
        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer(
                            [
                                ctx.getWaterwayBusInput(theVmObject, 'config.busdescriptor', {
                                    allowBlank: false
                                }),
                                ctx.getWaterwayGateTemplateInput(theVmObject, 'config.target', {
                                    allowBlank: false
                                })
                            ]
                        )
                    ]
                )
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

            invalid = CMDBuildUI.util.administration.helper.CronValidatorHelper.taskCronValidation(vm, invalid);
            if (invalid.length) {
                CMDBuildUI.util.administration.helper.FormHelper.showInvalidFieldsMessage({ items: invalid });
            }

            return invalid;
        }

    }



});