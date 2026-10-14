Ext.define('CMDBuildUI.view.processes.instances.instance.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-instance-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndCloseBtn': {
            click: 'onSaveBtnClick'
        },
        '#executeBtn': {
            click: 'onExecuteBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.set('basepermissions.edit', true);

        if (!(vm.get('theObject') && vm.get('objectModel'))) {
            // get instance model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                vm.get('objectTypeName')
            ).then(
                function (model) {
                    vm.set('objectModel', model);

                    // load process instance
                    vm.linkTo('theObject', {
                        type: model.getName(),
                        id: vm.get('objectId')
                    });

                    // load activity
                    view.loadActivity();
                },
                function () {
                    CMDBuildUI.util.Msg.alert('Error', 'Process non found!');
                }
            );
        } else {
            // load activity
            view.loadActivity();
        }

        this.initBeforeEditFormTriggers();
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.processes.instances.instance.Edit} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        if (view._isLocked) {
            view.lookupViewModel().get('theObject').removeLock();
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onExecuteBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        // disable button
        button.disable();
        // execute process
        var me = this;
        this.getView().executeProcess({
            success: function (record, operation) {
                if (CMDBuildUI.util.Ajax.getActionId() === CMDBuildUI.util.Ajax.processStatAbort) {
                    return;
                }

                if (me.getView().getForm().isValid()) {
                    button.enable();
                }

                var vm = button.lookupViewModel();
                // execute post action trigger
                me.executeAfterEditExecuteFormTriggers(record, vm.get('theActivity'));

                var forcereload = false;
                // get available tasks
                var tasks = record.get('_tasklist'),
                    isRunning = record.get('status') == me.getView().getOpenRunningStatusId(),
                    nexttask;

                if (!Ext.isEmpty(tasks) && isRunning) {
                    if (tasks.length === 1) {
                        nexttask = tasks[0];
                    } else if (vm.get('theActivity._activity_subset_id')) {
                        var subset = vm.get('theActivity._activity_subset_id');
                        nexttask = Ext.Array.findBy(tasks, function (taskitem) {
                            return taskitem._activity_subset_id == subset;
                        });
                    }
                }

                // redirect to next task
                if (nexttask) {
                    if (vm.get('theActivity._id') === nexttask._id && nexttask.writable) {
                        // change action in context menu to force redirect
                        // otherwise the page will be not update
                        // if activity and action are the same
                        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction('view');
                    }
                    var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                        record.get('_type'),
                        record.getId(),
                        nexttask._id,
                        nexttask.writable ? 'edit' : 'view'
                    );
                    me.redirectTo(url, true);
                } else {
                    forcereload = isRunning;
                    me.closeWindow();
                }

                if (!isRunning) {
                    var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(record.get('_type'));
                    me.redirectTo(url);
                    record = null;
                }

                // fire global event process instance updated
                Ext.GlobalEvents.fireEventArgs('processinstanceupdated', [record, forcereload]);
            },
            failure: function () {
                button.enable();
            },
            callback: function (record, operation, success) {
                if (me.getView() && me.getView().loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(me.getView().loadMask);
                }
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        // disable button
        button.disable();
        // execute process
        var me = this;
        this.getView().saveProcess({
            success: function (record, operation) {
                var vm = button.lookupViewModel();
                // execute post action trigger
                me.executeAfterEditFormTriggers(record, vm.get('theActivity'));

                // fire global event process instance updated
                Ext.GlobalEvents.fireEventArgs('processinstanceupdated', [record]);
                if (CMDBuildUI.util.Ajax.getActionId() === CMDBuildUI.util.Ajax.processStatAbort) {
                    return;
                }
                var url,
                    itemId = button.getItemId();
                if (itemId == 'saveAndCloseBtn') {
                    url = CMDBuildUI.util.Navigation.getProcessBaseUrl(record.get('_type'), record.getId());
                } else if (itemId == 'saveBtn') {
                    url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                        record.get('_type'),
                        record.getId(),
                        record.get('_activity'),
                        'view'
                    );
                }
                me.redirectTo(url);
            },
            failure: function () {
                button.enable();
            },
            callback: function (record, operation, success) {
                if (me.getView() && me.getView().loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(me.getView().loadMask);
                }
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        this.getView().cancelChanges(function () {
            me.closeWindow();
        });
    },

    privates: {
        /**
         * Close window
         */
        closeWindow: function () {
            this.getView().closeDetailWindow();
        },

        /**
         * Initialize before edit form triggers.
         */
        initBeforeEditFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeEdit,
                CMDBuildUI.util.api.Client.getApiForFormBeforeEdit()
            );
        },

        /**
         * Execute after edit form triggers.
         *
         * @param {CMDBuildUI.model.processes.Instance} record
         */
        executeAfterEditFormTriggers: function (record, activity) {
            var api = Ext.apply(
                {
                    record: record,
                    abortProcess: function () {
                        this._abortProcess(record);
                    }
                },
                CMDBuildUI.util.api.Client.getApiForFormAfterEdit()
            );

            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterEdit,
                record,
                activity,
                api
            );
        },

        /**
         * Execute after edit execute form triggers.
         *
         * @param {CMDBuildUI.model.processes.Instance} record
         */
        executeAfterEditExecuteFormTriggers: function (record, activity) {
            var api = Ext.apply(
                {
                    record: record,
                    abortProcess: function () {
                        this._abortProcess(record);
                    }
                },
                CMDBuildUI.util.api.Client.getApiForFormAfterEdit()
            );

            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterEditExecute,
                record,
                activity,
                api
            );
        }
    }
});
