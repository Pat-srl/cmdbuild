Ext.define('CMDBuildUI.mixins.routes.management.Views', {
    mixinId: 'managementroutes-views-mixin',

    /**
     * Before show view
     *
     * @param {String} viewName
     * @param {Object} action
     */
    onBeforeShowView: function (viewName, type, typeName, action) {
        if (!action) {
            action = arguments[arguments.length - 1];
            typeName = CMDBuildUI.util.helper.ModelHelper.objecttypes.event + 's';
        }

        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            //removes action from the context
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            const itemId = null;
            this.handleVwFiring(type, typeName, itemId);

            //stops the action
            action.stop();
        } else {
            this.checkViewValidity(viewName, type, typeName, function (success, opts) {
                if (success) {
                    //no need to update the action, will be updated in the action.resume()
                    // //removes action from the context
                    // CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

                    //removes the detail window
                    CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                    // Load flows statues
                    CMDBuildUI.util.Stores.loadFlowStatuses().then(function () {
                        //resumes the action
                        action.resume();
                    });
                } else {
                    //if the context is not initialized and the path is not correct, makes it correct
                    if (opts.error == 2) {
                        //stops the action
                        action.stop();

                        //creates the new url
                        var url;
                        switch (opts.expectedType) {
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.event:
                                url = Ext.String.format('views/{0}/{1}', viewName, opts.expectedType + 's');
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                url = Ext.String.format(
                                    'views/{0}/{1}/{2}/cards',
                                    viewName,
                                    opts.expectedType + 'es',
                                    opts.expectedTypeName
                                );
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                url = Ext.String.format(
                                    'views/{0}/{1}/{2}/instances',
                                    viewName,
                                    opts.expectedType + 'es',
                                    opts.expectedTypeName
                                );
                                break;
                        }

                        //redirects to correct type
                        CMDBuildUI.util.Utilities.redirectTo(url, true);
                    } else {
                        //stops the action
                        action.stop();

                        //redirects to management
                        CMDBuildUI.util.Utilities.redirectTo('management', true);
                    }
                }
            });
        }
    },

    /**
     * Show view
     *
     * @param {String} viewName
     */
    showView: function (viewName, type, typeName, itemId) {
        //once we are here, the view exists and the type is congruent with the typeName
        const object = CMDBuildUI.util.helper.ModelHelper.getViewFromName(viewName);
        var xtype, config;

        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.event + 's':
                itemId = typeName;
                typeName = type;

                //TODO: change events-container in order to allow an advanced filter passed as parameter
                const schedules = Ext.create('Ext.data.BufferedStore', {
                    model: 'CMDBuildUI.model.calendar.Event',
                    storeId: 'schedules',
                    type: 'buffered',
                    autoLoad: true,
                    autoDestroy: true,
                    pageSize: 100,
                    remoteFilter: true,
                    remoteSort: true,
                    proxy: {
                        type: 'baseproxy',
                        url: CMDBuildUI.util.api.Calendar.getEventsUrl(),
                        extraParams: {
                            detailed: true
                        }
                    }
                });

                //sets the advanded filter from the view object
                var advancedFilter = schedules.getAdvancedFilter();
                advancedFilter.addBaseFilter(object.get('filter'));

                // defines the xtype
                xtype = 'events-container';

                // defines xtype configs
                config = {
                    viewModel: {
                        data: {
                            schedules: schedules,
                            selectedId: itemId || ''
                        }
                    }
                };

                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass + 'es':
                // defines the xtype
                xtype = 'classes-cards-grid-container';

                // defines xtype configs
                config = {
                    // objectTypeName: object.get("sourceClassName"),
                    maingrid: true,
                    filter: object.get('filter'),
                    objectTypeName: typeName,
                    viewModel: {
                        data: {
                            selectedId: itemId
                        }
                    }
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process + 'es':
                // defines the xtype
                xtype = 'processes-instances-grid';

                // defines xtype configs
                config = {
                    objectTypeName: object.get('sourceClassName'),
                    filter: object.get('filter'),
                    maingrid: true,
                    viewModel: {
                        data: {
                            // objectTypeName: object.get("sourceClassName"),
                            objectTypeName: typeName,
                            selectedId: itemId
                        }
                    }
                };
                break;
            case this.items:
                if (object.get('type') === CMDBuildUI.model.views.View.types.sql) {
                    // defines the xtype
                    xtype = 'views-items-grid';
                } else if (object.get('type') === CMDBuildUI.model.views.View.types.join) {
                    // defines the xtype
                    xtype = 'joinviews-items-grid';
                }
                // defines xtype configs
                config = {
                    viewModel: {
                        data: {
                            objectTypeName: viewName
                        }
                    }
                };
                break;
        }

        CMDBuildUI.util.Navigation.addIntoManagementContainer(xtype, config);
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
            viewName,
            null,
            {
                vwObjectType: type,
                vwObjectTypeName: typeName,
                vwObjectId: itemId
            }
        );
        // firest the global objecttypechanged event
        Ext.GlobalEvents.fireEventArgs('objecttypechanged', [viewName]);
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} className
     * @param {*} cardId
     * @param {*} action
     */
    onBeforeShowVwCard: function (viewName, type, className, cardId, action) {
        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            //removes action from the context
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            this.handleVwFiring(type, className, cardId);

            //stops the action
            action.stop();
        } else {
            this.checkViewValidity(viewName, type, className, function (success, opts) {
                //if the context is correct and the model il loaded
                if (success) {
                    //removes the detail window
                    CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                    action.resume();
                } else {
                    /**
                     * this switch case handles all the three error types at the same
                     */
                    switch (opts.error) {
                        case 1:
                        // break;
                        case 2:
                        // break;
                        case 3:
                            //stops the action
                            action.stop();

                            //redirects to management
                            CMDBuildUI.util.Utilities.redirectTo('management');
                            break;
                    }
                }
            });
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} processName
     * @param {*} idInstance
     * @param {*} action
     */
    onBeforeShowVwProcess: function (viewName, type, processName, idInstance, action) {
        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            //removes action from the context
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            this.handleVwFiring(type, processName, idInstance);

            //stops the action
            action.stop();
        } else {
            this.checkViewValidity(viewName, type, processName, function (success, opts) {
                //if the context is correct and the model il loaded
                if (success) {
                    //removes the detail window
                    CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                    action.resume();
                } else {
                    /**
                     * this switch case handles all the three error types at the same
                     */
                    switch (opts.error) {
                        case 1:
                        // break;
                        case 2:
                        // break;
                        case 3:
                            //stops the action
                            action.stop();

                            //redirects to management
                            CMDBuildUI.util.Utilities.redirectTo('management');
                            break;
                    }
                }
            });
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} eventId
     * @param {*} action
     */
    onBeforeShowVwEvent: function (viewName, type, eventId, action) {
        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            //removes action from the context
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            this.handleVwFiring(type, type, eventId);

            //stops the action
            action.stop();
        } else {
            this.checkViewValidity(
                viewName,
                type,
                CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
                function (success, opts) {
                    //if the context is correct and the model il loaded
                    if (success) {
                        //removes the detail window
                        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                        action.resume();
                    } else {
                        /**
                         * this switch case handles all the three error types at the same
                         */
                        switch (opts.error) {
                            case 1:
                            // break;
                            case 2:
                            // break;
                            case 3:
                                //stops the action
                                action.stop();

                                //redirects to management
                                CMDBuildUI.util.Utilities.redirectTo('management');
                                break;
                        }
                    }
                }
            );
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} eventId
     * @param {*} actionType
     * @param {*} action
     */
    onBeforeShowVwEventWindow: function (viewName, type, eventId, actionType, action) {
        //sets right order arguments
        if (!action) {
            action = arguments[arguments.length - 1];
            actionType = eventId;
            eventId = null;
        }

        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            // //fires the needed events and updates custom page specific contex variables
            // this.handleVwFiring(type, type, eventId);

            //resume the action
            action.resume();
        } else {
            this.checkViewValidity(
                viewName,
                type,
                CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
                function (success, opts) {
                    if (success) {
                        /**
                         * shows the correct grid first
                         * the arguments are setted in the right into the showView function
                         */
                        this.showView(viewName, type, eventId);

                        //resumes the action
                        action.resume();
                    } else {
                        /**
                         * this switch case handles all the three error types at the same
                         */
                        switch (opts.error) {
                            case 1:
                            // break;
                            case 2:
                            // break;
                            case 3:
                                //stops the action
                                action.stop();

                                //redirects to management
                                CMDBuildUI.util.Utilities.redirectTo('management');
                                break;
                        }
                    }
                }
            );
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} eventId
     * @param {*} actionType
     */
    showVwEventAction: function (viewName, type, eventId, actionType) {
        if (!actionType) {
            actionType = arguments[arguments.length - 1];
            eventId = null;
        }

        switch (actionType) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                this.showEventCreate();
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                this.showEventView(eventId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                this.showEventEdit(eventId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                this.showEventNotes(eventId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                this.showEventHistory(eventId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                this.showEventEmails(eventId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                this.showEventAttachments(eventId);
                break;
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} className
     * @param {*} cardId
     * @param {*} actionType
     * @param {*} action
     */
    onBeforeShowVwClassWindow: function (viewName, type, className, cardId, actionType, action) {
        if (!action) {
            action = arguments[arguments.length - 1];
            actionType = cardId;
            cardId = null;
        }

        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            //fires the needed events and updates custom page specific contex variables
            // this.handleVwFiring(type, className, cardId);

            //resume the action
            action.resume();
        } else {
            this.checkViewValidity(viewName, type, className, function (success, opts) {
                if (success) {
                    /**
                     * shows the correct grid first
                     * the arguments are setted in the right into the showView function
                     */
                    this.showView(viewName, type, className, cardId);

                    //resumes the action
                    action.resume();
                } else {
                    /**
                     * this switch case handles all the three error types at the same
                     */
                    switch (opts.error) {
                        case 1:
                        // break;
                        case 2:
                        // break;
                        case 3:
                            //stops the action
                            action.stop();

                            //redirects to management
                            CMDBuildUI.util.Utilities.redirectTo('management');
                            break;
                    }
                }
            });
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} className
     * @param {*} cardId
     * @param {*} actionType
     */
    showVwClassAction: function (viewName, type, className, cardId, actionType) {
        if (!actionType) {
            actionType = arguments[arguments.length - 1];
            cardId = null;
        }

        switch (actionType) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                this.showCardCreate(className);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                this.showCardView(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail:
                this.showCardDetails(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                this.showCardEdit(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.clone:
                this.showCardClone(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations:
                this.showCardCloneandRelations(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                this.showCardNotes(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.relations:
                this.showCardRelations(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                this.showCardHistory(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                this.showCardEmails(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                this.showCardAttachments(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.schedules:
                this.showCardSchedules(className, cardId);
                break;
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} processName
     * @param {*} idInstance
     * @param {*} activityId
     * @param {*} actionType
     * @param {*} action
     */
    onBeforeShowVwProcessInstanceWindow: function (
        viewName,
        type,
        processName,
        idInstance,
        activityId,
        actionType,
        action
    ) {
        const pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;

        // fix variables
        if (!action) {
            action = actionType;
            actionType = activityId;
            activityId = null;
        }
        if (!action) {
            action = actionType;
            actionType = idInstance;
            instanceid = null;
        }

        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, viewName)) {
            // //fires the needed events and updates custom page specific contex variables
            // this.handleVwFiring(type, processName, idInstance);

            //resume the action
            CMDBuildUI.util.Stores.loadFlowStatuses().then(function () {
                action.resume();
            });
        } else {
            this.checkViewValidity(viewName, type, processName, function (success, opts) {
                if (success) {
                    CMDBuildUI.util.Stores.loadFlowStatuses().then(
                        function () {
                            CMDBuildUI.util.helper.ModelHelper.getModel(
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                                processName
                            ).then(
                                function (model) {
                                    /**
                                     * shows the correct grid first
                                     * the arguments are setted in the right into the showView function
                                     */
                                    this.showView(viewName, type, processName, idInstance);

                                    //resumes the action
                                    action.resume();
                                },
                                Ext.emptyFn,
                                Ext.emptyFn,
                                this
                            );
                        },
                        Ext.emptyFn,
                        Ext.emptyFn,
                        this
                    );
                } else {
                    /**
                     * this switch case handles all the three error types at the same
                     */
                    switch (opts.error) {
                        case 1:
                        // break;
                        case 2:
                        // break;
                        case 3:
                            //stops the action
                            action.stop();

                            //redirects to management
                            CMDBuildUI.util.Utilities.redirectTo('management');
                            break;
                    }
                }
            });
        }
    },

    /**
     *
     * @param {*} viewName
     * @param {*} type
     * @param {*} processName
     * @param {*} idInstance
     * @param {*} activityId
     * @param {*} actionType
     */
    showVwProcessInstanceAction: function (viewName, type, processName, idInstance, activityId, actionType) {
        if (!actionType) {
            actionType = arguments[arguments.length - 1];
            activityId = null;
            idInstance = null;
        }

        switch (actionType) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                this.showProcessInstanceCreate(processName);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                this.showProcessInstanceView(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                this.showProcessInstanceEdit(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                this.showProcessInstanceNotes(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.relations:
                this.showProcessInstanceRelations(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                this.showProcessInstanceHistory(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                this.showProcessInstanceEmails(processName, idInstance, activityId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                this.howProcessInstanceAttachments(processName, idInstance, activityId);
                break;
        }
    },

    /**
     * this function checks the existence of the view and the match of the view with the given type
     * afer the check execute the callback
     * @param {*} viewName
     * @param {*} type
     * @param {*} callback
     * callback arguments
     * succeess {Boolean} if true the view exist and the given type is
     * opts.error {Number}
     *  1: Cant find the view,
     *  2: thers a type mismatch
     *  3: Can't load the model
     * opts.expectedType {String} the type expected when opst.error == 2
     * opts.expectedTypeName {String} the typeName expected whne opts.error = 2
     */
    checkViewValidity: function (viewName, type, typeName, callback) {
        const me = this,
            object = CMDBuildUI.util.helper.ModelHelper.getViewFromName(viewName);
        var objectTypeName,
            objectType,
            tmpObjectType,
            opts = {};

        if (object && object.get('type') === CMDBuildUI.model.views.View.types.filter) {
            objectTypeName = object.get('sourceClassName');

            //assert: objectType = class || process
            objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
            tmpObjectType = objectType + 'es';
            if (tmpObjectType != type) {
                opts = {
                    error: 2,
                    expectedType: objectType,
                    expectedTypeName: objectTypeName
                };

                // the check on type fails
                callback.call(this, false, opts);
                return;
            }
        } else if (object && object.get('type') === CMDBuildUI.model.views.View.types.calendar) {
            objectType = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
            tmpObjectType = CMDBuildUI.util.helper.ModelHelper.objecttypes.event + 's';

            if (tmpObjectType != type) {
                opts = {
                    error: 2,
                    expectedType: objectType
                    // expectedTypeName: objectTypeName
                };

                // the check on type fails
                callback.call(this, false, opts);
                return;
            } else {
                //the check is valid
                callback.call(this, true);
                return;
            }
        } else if (
            (object && object.get('type') === CMDBuildUI.model.views.View.types.sql) ||
            (object && object.get('type') === CMDBuildUI.model.views.View.types.join)
        ) {
            tmpObjectType = this.items;

            if (tmpObjectType != type) {
                opts = {
                    error: 2,
                    expectedType: tmpObjectType
                    // expectedTypeName: objectTypeName
                };

                // the check on type fails
                callback.call(this, false, opts);
                return;
            }
            objectType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
            objectTypeName = viewName;
        } else {
            opts = {
                errorId: 1
            };

            //the object is not found or the type is not allowed
            callback.call(this, false, opts);
            return;
        }

        // get model
        CMDBuildUI.util.helper.ModelHelper.getModel(objectType, objectTypeName).then(
            function (model) {
                //model loaded
                callback.call(me, true);
            },
            function () {
                opts = {
                    errorId: 3
                };

                //could not load the model
                callback.call(me, false, opts);
            }
        );
    },

    /**
     * This function fires the events and updates the context for the changed context variables
     * @param {String} type  classes or processed
     * @param {*} typeName class or process type name
     * @param {*} itemId the id of the item
     */
    handleVwFiring: function (type, typeName, itemId) {
        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextViewObjectType(type)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextViewObjectType(type);
            Ext.GlobalEvents.fireEventArgs('objecttypechanged', [type]);
        }

        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextViewObjectTypeName(typeName)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextViewObjectTypeName(typeName);
            Ext.GlobalEvents.fireEventArgs('objecttypenamechanged', [typeName]);
        }

        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextViewObjectId(itemId)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextViewObjectId(itemId);
            Ext.GlobalEvents.fireEventArgs('objectidchanged', [itemId]);
        }

        //TODO: handle activity id changed
        //objectcustompageactivityidchanged
    },

    privates: {
        items: 'items'
    }
});
