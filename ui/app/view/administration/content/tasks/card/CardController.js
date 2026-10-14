Ext.define('CMDBuildUI.view.administration.content.tasks.card.CardController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin',
        'CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'
    ],
    alias: 'controller.view-administration-content-tasks-card',

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#prevBtn': {
            click: 'onPrevBtnClick'
        },
        '#nextBtn': {
            click: 'onNextBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#': {
            beforerender: 'onBeforeRender'
        }

    },

    onBeforeRender: function (view) {
        var me = this;
        var vm = view.getViewModel();
        var formButtons;
        vm.bind({
            bindTo: {
                theTask: '{theTask}'
            }
        }, function (data) {
            if (data.theTask) {

                try {
                    // workaround after patch "3.3.0-35"                    
                    if (data.theTask._config.get('tag') === 'database') {
                        var gateStore = Ext.getStore('importexports.Gates');
                        var gate = gateStore.findRecord('code', data.theTask._config.get('gate'));
                        if (gate) {
                            data.theTask._config.set('gateconfig_handlers_0_type', gate.get('_handler_type'));
                            data.theTask._config.set('gateconfig_handlers_0_gate', gate.get('code'));
                            data.theTask._config.set('tag', gate.get('_handler_type'));
                        }
                    }
                    var type = data.theTask.get('type');
                    if (type === 'trigger') {
                        vm.set('waterwayBusStore', CMDBuildUI.util.Stores.stores.busdescriptors);
                    }
                    if (data.theTask._config.get('tag') === 'ifc') {
                        vm.bind({
                            bindTo: {
                                gate: '{theTask.config.gateconfig_handlers_1_gate}'
                            }
                        }, function (_data) {
                            if (!vm.destroyed) {
                                var container = view.down('#targetCardIdContainer');
                                if (container) {
                                    container.removeAll();
                                    CMDBuildUI.util.Stores.loadETLGatesStore().then(function (gates) {
                                        if (!vm.destroyed) {
                                            var store = Ext.getStore('importexports.Gates');
                                            var record = store.findRecord('code', _data.gate);
                                            if (record) {
                                                var className = record.get('config').bimserver_project_master_card_target_class;
                                                if (className) {
                                                    var referencecombo = {
                                                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                                                        columnWidth: 0.5,
                                                        xtype: 'referencecombofield',
                                                        displayField: 'Description',
                                                        itemId: 'ownerCard',
                                                        valueField: '_id',
                                                        name: 'gateconfig_handlers_1_config_bimserver_project_master_card_id',
                                                        width: '100%',
                                                        style: 'padding-right: 15px',
                                                        metadata: {
                                                            targetType: 'class',
                                                            targetClass: className
                                                        },
                                                        hidden: true,
                                                        bind: {
                                                            disabled: '{!theTask.config.gateconfig_handlers_1_gate}',
                                                            value: '{theTask.config.gateconfig_handlers_1_config_bimserver_project_master_card_id}',
                                                            hidden: '{actions.view || theTask.config.gateconfig_handlers_1_config_bimserver_project_master_card_mode !== "static"}'
                                                        },
                                                        listeners: {
                                                            change: function (input, newValue, oldValue) {
                                                                var _vm = input.lookupViewModel();

                                                                if (_vm.get('theTask')._config.get('gateconfig_handlers_1_config_bimserver_project_master_card_id') !== newValue) {
                                                                    _vm.set('gateconfig_handlers_1_config_bimserver_project_master_card_id', newValue);
                                                                    _vm.get('theTask')._config.set('gateconfig_handlers_1_config_bimserver_project_master_card_id', newValue);
                                                                }
                                                            }
                                                        }

                                                    };
                                                    container.add(referencecombo);
                                                    container.add({
                                                        xtype: 'displayfield',
                                                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                                                        bind: {
                                                            hidden: '{!actions.view}',
                                                            value: '{theTask.config._gateconfig_handlers_1_config_bimserver_project_master_card_id_description}'
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    Ext.asap(function () {
                        me.generateCardFor(type, data, view);
                        view.setHidden(false);
                        view.up().unmask();
                    });
                } catch (error) {
                    CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                }
            }
        });
        // isView | isEdit            
        var modelName = CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(vm.get('taskType') || vm.get('grid').getSelection()[0].get('type'), vm.get('subType'));
        if (!vm.get('theTask') || !vm.get('theTask').phantom) {
            vm.linkTo("theTask", {
                type: modelName,
                id: vm.get('grid').getSelection()[0].get('_id')
            });
        }
        // isClone
        // if (vm.get('theTask') && vm.get('theTask').phantom) {
        //     var config = vm.get('theTask')._config;
        //     //  config.updateDataFromObject(vm.get('theTask').get('config'));
        // }        

        if (vm.get('actions.view')) {
            var topbar = {
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                hidden: true,
                bind: {
                    hidden: '{!actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true, // #editBtn set true for show the button
                    view: false, // #viewBtn set true for show the button
                    clone: true, // #cloneBtn set true for show the button
                    'delete': true, // #deleteBtn set true for show the button
                    activeToggle: false // #enableBtn and #disableBtn set true for show the buttons       
                },

                    /* testId */
                    'importexporttemplates',

                    /* viewModel object needed only for activeTogle */
                    'theTask',

                    /* add custom tools[] on the left of the bar */
                    [],

                    /* add custom tools[] before #editBtn*/
                    [],

                    /* add custom tools[] after at the end of the bar*/
                    []
                )
            };
            view.addDocked(topbar);

        }
        formButtons = {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: CMDBuildUI.
                util.
                administration.
                helper.FormHelper.
                getPrevNextSaveCancelButtons(false, /* formBind */ {
                    // prev
                    bind: {
                        disabled: '{isPrevDisabled}'
                    }
                }, {
                    // next
                    bind: {
                        disabled: '{isNextDisabled}'
                    }
                }, {
                    // save
                    bind: {
                        hidden: '{actions.view}',
                        disabled: '{!isNextDisabled}'
                    }
                }, {
                    // cancel
                    bind: {
                        hidden: '{actions.view}'
                    }
                })
        };

        view.addDocked(formButtons);

        if (!CMDBuildUI.util.Stores.loaded.emailaccounts) {
            CMDBuildUI.util.Stores.loadEmailAccountsStore();
        }
        if (!CMDBuildUI.util.Stores.loaded.emailtemplates) {
            CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        }

        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();


    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = me.getView();
        var vm = me.getViewModel();
        var theTask = vm.get('theTask');
        if (form && me.validateForm(form)) {
            button.setDisabled(true);
            CMDBuildUI.util.Utilities.showLoader(true);
            var configData = theTask._config.getData();

            if (vm.get('isAdvancedCron')) {
                var cronExpression = Ext.String.format('{0} {1} {2} {3} {4}',
                    vm.get('advancedCronMinuteValue'),
                    vm.get('advancedCronHourValue'),
                    vm.get('advancedCronDayValue'),
                    vm.get('advancedCronMonthValue'),
                    vm.get('advancedCronDayofweekValue')
                );
                configData.cronExpression = cronExpression;
            }
            switch (theTask.get('type')) {
                case CMDBuildUI.model.tasks.Task.types.workflow:
                    configData.classname = vm.get('workflowClassName');
                    configData.attributes = vm.serializeAttributesMapStore();
                    break;
                case CMDBuildUI.model.tasks.Task.types.emailService:
                    configData.action_workflow_class_name = vm.get('workflowClassName');
                    configData.action_workflow_fields_mapping = vm.serializeAttributesMapStore();
                    break;
                case CMDBuildUI.model.tasks.Task.types.sendemail:
                    var emailContextStore = form.down('#emailContextVariableGrid').getStore();
                    var reportParametersStore = form.down('#reportParametersGrid').getStore();
                    Ext.Array.forEach(emailContextStore.getRange(), function (item) {
                        configData.email_template_context[item.get('key')] = item.get('value');
                    });

                    Ext.Array.forEach(reportParametersStore.getRange(), function (item) {
                        configData.attach_report_params[item.get('key')] = item.get('value');
                    });

                    break;
                default:
                    break;
            }

            delete configData._id;
            theTask.set('config', configData);
            theTask._config.set('cronExpression', configData.cronExpression);
            theTask.save({
                success: function (record, operation) {
                    vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), record, me]);
                    if (!button.destroyed) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                    form.up().fireEvent("closed");
                },
                failure: function () {
                    if (!button.destroyed) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theTask").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },

    onETLStoreDataChanged: function (data) {
        var vm = this.getViewModel();
        Ext.Array.forEach([false, true], function (value) {
            vm.set('allImportExportTemplate.isReady', value);
            data.isReady = value;
        });
    },
    privates: {

        validateForm: function (form) {

            var me = this,
                invalid = [];
            var taskType = form.getViewModel().get('taskType');
            switch (taskType) {
                case CMDBuildUI.model.tasks.Task.types.import_export:
                    invalid = me.importexport.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.emailService:
                    invalid = me.emailservice.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.workflow:
                    invalid = me.startworkflow.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.import_database:
                    invalid = me.connector.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.importgis:
                    invalid = me.gistemplate.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.sendemail:
                    invalid = me.sendemail.validateForm(form);
                    break;
                case CMDBuildUI.model.tasks.Task.types.workflow:
                    invalid = me.workflow.validateForm(form);
                    break;
                default:
                    break;
            }
            return !invalid.length;

        }
    }
});