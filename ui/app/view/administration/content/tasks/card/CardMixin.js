Ext.define('CMDBuildUI.view.administration.content.tasks.card.CardMixin', {

    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper'],
    mixinId: 'administration-importexportmixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                taskType: view.getViewModel().get('taskType'),
                subType: view.getViewModel().get('subType') || view.getViewModel().get('theTask')._config.get('tag'),
                theTask: view.getViewModel().get('selected') || view.getViewModel().get('theTask'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    edit: true,
                    view: false,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-task');
                    var grid = button.up('administration-content-tasks-grid') || button.lookupViewModel().get('grid');

                    me.getViewModel().get('theTask').erase({
                        success: function () {
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            grid.getStore().load();
                        }
                    });
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                taskType: vm.get('taskType'),
                subType: view.getViewModel().get('subType') || view.getViewModel().get('theTask')._config.get('tag'),
                theTask: view.getViewModel().get('selected') || view.getViewModel().get('theTask'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var newTask = vm.get('theTask').getData(); //vm.get('theTask').copyForClone();
        var config = vm.get('theTask').getAssociatedData().config;
        delete config._id;

        newTask.code = Ext.String.format('{0}_clone', newTask.code);
        delete newTask._id;
        newTask.description = Ext.String.format('{0}_clone', newTask.description);
        var modelName = CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(vm.get('taskType') || vm.get('grid').getSelection()[0].get('type'), vm.get('subType') || vm.get('theTask')._config.get('tag'));
        var theTask = Ext.create(modelName, newTask);
        theTask._config = Ext.create(vm.get('theTask')._config.$className, config);

        var viewModel = {
            data: {
                taskType: theTask.get('type') || vm.get('taskType'),
                subType: view.getViewModel().get('subType') || vm.get('theTask')._config.get('tag'),
                theTask: theTask,
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    edit: false,
                    view: false,
                    add: true
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },
    cronValidator: function (input, newValue, oldValue) {

        var form = input.up('form');
        if (!form) {
            return true;
        }
        var vm = form.lookupViewModel();
        var cronExpression = Ext.String.format('{0} {1} {2} {3} {4}',
            vm.get('advancedCronMinuteValue'),
            vm.get('advancedCronHourValue'),
            vm.get('advancedCronDayValue'),
            vm.get('advancedCronMonthValue'),
            vm.get('advancedCronDayofweekValue')
        );
        var regex = /^\s*($|#|\w+\s*=|(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?(?:,(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?)*)\s+(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)\s+(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\s+(\?|\*|(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*))$/;
        var isValid = regex.test('* ' + cronExpression);

        if (!isValid || (
            !form.down('[name="advancedcron_minute"]').getValue() ||
            !form.down('[name="advancedcron_hour"]').getValue() ||
            !form.down('[name="advancedcron_day"]').getValue() ||
            !form.down('[name="advancedcron_month"]').getValue() ||
            !form.down('[name="advancedcron_dayofweek"]').getValue())) {
            form.down('[name="advancedcron_minute"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_hour"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_day"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_month"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_dayofweek"]').markInvalid('Cron expression is invalid', true);
            return false;
        } else {
            form.down('[name="advancedcron_minute"]').markInvalid(null, false);
            form.down('[name="advancedcron_hour"]').markInvalid(null, false);
            form.down('[name="advancedcron_day"]').markInvalid(null, false);
            form.down('[name="advancedcron_month"]').markInvalid(null, false);
            form.down('[name="advancedcron_dayofweek"]').markInvalid(null, false);
            return true;
        }
    },
    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow),
            grid = view.ownerGrid,
            subType = view.getViewModel().get('subType') || record.get('config').tag,
            type = record.get('type');

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: {
                data: {
                    taskType: type,
                    subType: subType,
                    grid: grid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },

    onPrevBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        var newCurrentStep = vm.get('currentStep') - 1;
        vm.set('currentStep', newCurrentStep);
    },

    onNextBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        var newCurrentStep = vm.get('currentStep') + 1;
        vm.set('currentStep', newCurrentStep);
    },

    onSenderRegexClick: function (event, button, eOpts) {
        var me = this;

        var content = {
            xtype: 'administration-components-splitstring-grid',
            viewModel: {
                data: {
                    theMessage: me.getViewModel().get('theTask.config.filter_regex_from'),
                    theDivisor: ' OR ',
                    actions: me.getViewModel().get('actions')
                }
            }
        };

        // custom panel listeners
        var listeners = {
            returnString: function (result, _eOpts) {
                me.getViewModel().get('theTask')._config.set('filter_regex_from', result);
                me.getView().down('#filterSenderRegex_input').setValue(result);
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            },
            close: function (panel, _eOpts) {
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            }
        };

        CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-imports-view',
            CMDBuildUI.locales.Locales.administration.tasks.regexfilters,
            content,
            listeners,
            {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );
    },

    onSubjectRegexClick: function (button, event, eOpts) {
        var me = this;
        var content = {
            xtype: 'administration-components-splitstring-grid',
            viewModel: {
                data: {
                    theMessage: me.getViewModel().get('theTask.config.filter_regex_subject'),
                    theDivisor: ' OR ',
                    actions: me.getViewModel().get('actions')
                }
            }
        };

        // custom panel listeners
        var listeners = {
            returnString: function (result, _eOpts) {
                me.getViewModel().get('theTask')._config.set('filter_regex_subject', result);
                me.getView().down('#filterSubjectRegex_input').setValue(result);
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            },
            close: function (panel, _eOpts) {
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            }
        };

        CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-imports-view',
            'Regex filters',
            content,
            listeners,
            {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );
    },

    privates: {

        generateCardFor: function (type, data, view) {
            switch (type) {
                case CMDBuildUI.model.tasks.Task.types.import_export:
                case CMDBuildUI.model.tasks.Task.types.import_file:
                case CMDBuildUI.model.tasks.Task.types.export_file:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.notifications, 3, [this.getNotificationPanel('theTask', 'step4', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.emailService:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.parsing, 3, [this.getParsePanel('theTask', 'step4', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.process, 4, [this.getProcessPanel('theTask', 'step5', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.notifications, 5, [this.getNotificationPanel('theTask', 'step6', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.workflow:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 1, [this.getCronPanel('theTask', 'step2', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.import_database:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.importgis:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.notifications, 3, [this.getNotificationPanel('theTask', 'step4', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.sendemail:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    break;
                case CMDBuildUI.model.tasks.Task.types.waterway:
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.generalproperties, 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.settings, 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep(CMDBuildUI.locales.Locales.administration.tasks.cron, 2, [this.getCronPanel('theTask', 'step3', data)]);
                    break;
                default:
                    break;
            }
        }
    }
});