Ext.define('CMDBuildUI.mixins.routes.management.Processes', {
    mixinId: 'managementroutes-processes-mixin',

    /******************* PROCESS INSTANCES GRID ********************/
    /**
     * Before show process instances grid
     * 
     * @param {String} processName
     * @param {Object} action
     */
    onBeforeShowProcessInstancesGrid: function (processName, action) {
        var me = this;
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;

        //removes detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //update context variables
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(null);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(null);

        // if processes are not enable stop action
        if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled)) {
            CMDBuildUI.util.Notifier.showErrorMessage("Workflow feature is not enabled");
            action.stop();
            return;
        }

        CMDBuildUI.util.Stores.loadFlowStatuses().then(function () {

            //get model
            if (CMDBuildUI.util.helper.ModelHelper.getProcessFromName(processName)) {
                if (me.getCustomProcessActionRouting(processName, null, 'showGrid', action)) {
                    return;
                }
                //check objectTypeName
                if (CMDBuildUI.util.Navigation.checkCurrentContext(type, processName)) {

                    // fire global event objectidchanged
                    Ext.GlobalEvents.fireEventArgs("objectidchanged", [null]);
                    action.stop();

                } else {
                    action.resume();
                }
            } else {
                CMDBuildUI.util.Utilities.redirectTo("management");
                action.stop();
            }
        });
    },
    /**
     * Show process instances grid
     * 
     * @param {String} processName
     * is called dicretly from code, not from router.
     */
    showProcessInstancesGrid: function (processName, instanceId) {
        if (!CMDBuildUI.util.Navigation.shouldUseCustomRouting.call(this, processName)) {
            CMDBuildUI.util.Navigation.addIntoManagementContainer('processes-instances-grid', {
                objectTypeName: processName,
                maingrid: true,
                viewModel: {
                    data: {
                        objectTypeName: processName,
                        selectedId: instanceId
                    }
                }
            });

            // update current context
            CMDBuildUI.util.Navigation.updateCurrentManagementContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                processName,
                instanceId
            );

            // fire global event objecttypechanged
            Ext.GlobalEvents.fireEventArgs("objecttypechanged", [processName]);

        }
    },

    /**
     * @param {String} processName 
     * @param {String|Number} instanceId 
     * @param {String} activityId 
     * @param {Object} action 
     */
    onBeforeShowProcessInstance: function (processName, instanceId, action) {
        var me = this;

        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action and activity from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(null);

        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        CMDBuildUI.util.Stores.loadFlowStatuses().then(function () {

            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(type, processName).then(function (model) {
                if (me.getCustomProcessActionRouting(processName, instanceId, null, 'viewInRow', action)) {
                    return;
                }

                //checks the objectTypeName
                if (CMDBuildUI.util.Navigation.checkCurrentContext(type, processName, true)) {

                    //checks the objectId
                    if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(instanceId)) {

                        // fire global event objectidchanged
                        Ext.GlobalEvents.fireEventArgs("objectidchanged", [instanceId]);

                        //updates the context with the new id
                        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(instanceId);
                    }
                    action.stop();
                } else {
                    action.resume();
                }
            }, function () {
                action.stop();
            });
        });
    },
    /**
     * @param {String} processName 
     * @param {String|Number} instanceId 
     */
    showProcessInstance: function (processName, instanceId) {
        // show grid
        this.showProcessInstancesGrid(processName, instanceId);
    },

    /******************* PROCESS INSTANCE DETAIL WINDOW ********************/
    /**
     * Before show process instance view
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     * @param {Object} action
     */
    onBeforeShowProcessInstanceWindow: function (processName, instanceId, activityId, formMode, action) {
        var me = this;
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;

        // fix variables for create form
        if (!action && instanceId === 'new') {
            action = activityId;
            instanceId = null;
        }

        // load model
        CMDBuildUI.util.Stores.loadFlowStatuses().then(function () {
            CMDBuildUI.util.helper.ModelHelper.getModel(type, processName).then(function (model) {
                if (me.getCustomProcessActionRouting(processName, instanceId, activityId, formMode, action)) {
                    return;
                }
                //checks the objectTypeName
                if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, processName, true)) {

                    // show instances grid for processName
                    me.showProcessInstancesGrid(processName, instanceId);
                }

                // resume action
                action.resume();
            }, function () {
                CMDBuildUI.util.Msg.alert('Error', 'Process non found!');
                action.stop();
            });
        });
    },

    /**
     * Show process instance create
     * 
     * @param {String} processName
     * @param {Object} action
     */
    showProcessInstanceCreate: function (processName) {
        this.showProcessInstanceTabPanel(
            processName,
            null,
            null,
            CMDBuildUI.mixins.DetailsTabPanel.actions.create
        );
    },

    /**
     * Show process instance view
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     */
    showProcessInstanceView: function (processName, instanceId, activityId) {
        this.showProcessInstanceTabPanel(
            processName,
            instanceId,
            activityId,
            CMDBuildUI.mixins.DetailsTabPanel.actions.view
        );
    },
    /**
     * Show process instance edit
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     */
    showProcessInstanceEdit: function (processName, instanceId, activityId) {
        this.showProcessInstanceTabPanel(
            processName,
            instanceId,
            activityId,
            CMDBuildUI.mixins.DetailsTabPanel.actions.edit
        );
    },

    /**
     * Show notes view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceNotes: function (processName, instanceId, activityId) {
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName, "process");
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.note_read)) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.notes
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view", processName, instanceId));
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceRelations: function (processName, instanceId, activityId) {
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName, "process");
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.relation_read)) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.relations
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view", processName, instanceId));
        }
    },

    /**
     * Show history view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceHistory: function (processName, instanceId, activityId) {
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName, "process");
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.history_read)) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.history
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view", processName, instanceId));
        }
    },

    /**
     * Show emails view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceEmails: function (processName, instanceId, activityId) {
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName, "process");
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.email_read)) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.emails
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view", processName, instanceId));
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceAttachments: function (processName, instanceId, activityId) {
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName, "process");
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) && obj.get(CMDBuildUI.model.users.Grant.permissions.attachment_read)) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.attachments
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view", processName, instanceId));
        }
    },

    /**
     * 
     * @param {String} processName 
     * @param {Numeric} instanceId 
     */
    openProcessInstanceView: function (processName, instanceId) {
        this.openProcessInstance(processName, instanceId, 'view');
    },

    /**
     * 
     * @param {String} processName 
     * @param {Numeric} instanceId 
     */
    openProcessInstanceEdit: function (processName, instanceId) {
        this.openProcessInstance(processName, instanceId, 'edit');
    },

    privates: {
        /**
         * 
         * @param {String} processName 
         * @param {Number|String} instanceId 
         * @param {String} activityId 
         * @param {String} action 
         */
        showProcessInstanceTabPanel: function (processName, instanceId, activityId, action) {
            if (
                !CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action) ||
                !CMDBuildUI.util.Navigation.checkCurrentManagementContextActivity(activityId)
            ) {
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('processes-instances-tabpanel', {
                    tabtools: [],
                    viewModel: {
                        data: {
                            objectTypeName: processName,
                            objectId: instanceId,
                            activityId: activityId,
                            action: action
                        }
                    }
                });
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
                CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(activityId);
            }
        },

        /**
         * 
         * @param {String} processName 
         * @param {Numeric} instanceId 
         * @param {String} action 
         */
        openProcessInstance: function (processName, instanceId, action) {
            var me = this;
            // load process model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                processName
            ).then(function (model) {
                model.load(instanceId, {
                    callback: function (record, operation, success) {
                        var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(processName, instanceId);
                        if (success && record) {
                            var tasks = record.get("_tasklist");
                            if (tasks.length === 1) {
                                var task = tasks[0];
                                url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                                    processName,
                                    instanceId,
                                    task._id,
                                    task.writable ? action : 'view'
                                );
                            }
                        }
                        me.redirectTo(url);
                    }
                });
            });
        },

        /**
         * 
         * @param {String} className 
         * @param {String} idCard 
         * @param {String} formMode 
         * @param {String} action 
         * 
         * @return {Boolean}
         */
        getCustomProcessActionRouting: function (processName, instanceId, activityId, formMode, action) {
            var actions = CMDBuildUI.util.Navigation.getProcessActionsMap();

            var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(processName);
            var uiRouting_custom = process.get('uiRouting_custom') || {};
            var customUiRouting = uiRouting_custom[actions[formMode]];
            if (!Ext.isEmpty(customUiRouting)) {

                customUiRouting = customUiRouting.replace(':processName', processName);
                customUiRouting = customUiRouting.replace(':idInstance', instanceId);
                customUiRouting = customUiRouting.replace(':activityId', activityId);

                CMDBuildUI.util.Utilities.redirectTo(customUiRouting, true);
                if (action) {
                    action.stop();
                }
                return true;
            }
        }
    }
});