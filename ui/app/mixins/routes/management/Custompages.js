Ext.define('CMDBuildUI.mixins.routes.management.Custompages', {
    mixinId: 'managementroutes-custompages-mixin',

    /******************* CUSTOM PAGES ********************/
    // /**
    //  * Before show custom page
    //  * 
    //  * @param {String} pageName
    //  * @param {Object} action
    //  */
    onBeforeShowCustomPage: function (pageName, type, typeName, action) {
        if (!action) {
            action = arguments[arguments.length - 1];
            typeName = null;
            type = null;
        }

        var pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            page = this.pageExists(pageName);
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, pageName)) {

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            var itemId = null;
            this.handleCpFiring(type, typeName, itemId);

            action.stop();
        } else if (!!page) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                page.get("componentId")
            ), function () {
                //removes the detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                action.resume();
            });
        } else {
            action.stop();
            CMDBuildUI.util.Utilities.redirectTo("management");
        }
    },

    showCustomPage: function (pageName, type, typeName, itemId) {
        //once we are here, the page allways exists
        var page = Ext.getStore("custompages.CustomPages").findRecord("name", pageName);
        menuTypeCustomPage = CMDBuildUI.model.menu.MenuItem.types.custompage;

        //the context is updated allways in the onBefore but in this case must stay here
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            pageName,
            null,
            {
                cpObjectType: type,
                cpObjectTypeName: typeName,
                cpObjectId: itemId
            }
        );

        CMDBuildUI.util.Navigation.addIntoManagementContainer('panel', {
            layout: 'fit',
            title: {
                xtype: "management-title",
                bind: {
                    text: '{title}',
                    objectTypeName: '{objectTypeName}',
                    menuType: '{menuType}'
                }
            },
            items: [{
                xtype: page.get("alias").replace("widget.", ""),
                viewModel: {
                    data: {
                        typeName: typeName,
                        selectedId: itemId
                    }
                }
            }],
            viewModel: {
                data: {
                    title: page.getTranslatedDescription(),
                    menuType: menuTypeCustomPage,
                    objectTypeName: pageName
                }
            }
        });

        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [pageName]);
    },

    onBeforeShowCpCard: function (pageName, type, className, cardId, action) {
        var pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            page = this.pageExists(pageName);
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, pageName)) {

            //updates the context action
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            this.handleCpFiring(type, className, cardId);

            //stops the action
            action.stop()
        } else if (!!page) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                page.get("componentId")
            ), function () {
                //removes the detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                action.resume();
            });
        } else {
            //stops the action
            action.stop();

            //redirects to management
            CMDBuildUI.util.Utilities.redirectTo("management");
        }

    },

    onBeforeShowCpProcessInstance: function (pageName, type, processName, instanceId, action) {

        var pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            page = this.pageExists(pageName);
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, pageName)) {

            //Updates the action and activity in the context
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);
            CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(null);

            //removes the detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

            //fires the needed events and updates custom page specific contex variables
            this.handleCpFiring(type, processName, instanceId);

            // stops the action
            action.stop();
        } else if (!!page) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                page.get("componentId")
            ), function () {
                //removes the detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

                action.resume();
            });
        } else {
            //stops the action
            action.stop();

            //redirects to management
            CMDBuildUI.util.Utilities.redirectTo("management");
        }
    },

    onBeforeShowCpCardWindow: function (pageName, className, cardId, actionType, action) {
        if (!action) {
            action = arguments[arguments.length - 1];
            actionType = cardId;
            cardId = null;
        }

        var pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            page = this.pageExists(pageName),
            me = this;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, pageName)) {

            //fires the needed events and updates custom page specific contex variables
            this.handleCpFiring('classes', className, cardId);

            action.resume();
        } else if (!!page) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                page.get("componentId")
            ), function () {
                //adds the custom page in the main view
                me.showCustomPage(pageName, 'classes', className, cardId);

                //NOTE: due to a bug the current acton mus be reset
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

                //resumes the action
                action.resume();
            })
        } else {
            //stops the action
            action.stop();

            //redirects to management
            CMDBuildUI.util.Utilities.redirectTo("management");
            //TODO: launch warnin
        }
    },

    onBeforeShowCpProcessInstanceWindow: function (pageName, processName, instanceId, activityId, actionType, action) {
        if (!action) {
            action = arguments[arguments.length - 1];
            actionType = instanceId;
            activityId = null;
            instanceId = null;
        }

        var pageType = CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            page = this.pageExists(pageName),
            me = this;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(pageType, pageName)) {

            //fires the needed events and updates custom page specific contex variables
            this.handleCpFiring('processes', processName, instanceId)

            //resume the action
            action.resume();
        } else if (!!page) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                page.get("componentId")
            ), function () {
                //show the custom page in the main view
                me.showCustomPage(pageName, 'processes', processName, instanceId);

                //NOTE: due to a bug the current acton mus be reset
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

                //resumes the action
                action.resume();
            });
        } else {

            //stops the action
            action.stop();

            //redirects to management
            CMDBuildUI.util.Utilities.redirectTo("management");
            //TODO: launch warnin
        }
    },

    showCpCardAction: function (pageName, className, cardId, action) {
        if (!action) {
            action = cardId;
            cardId = null;
        }

        switch (action) {
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
    showCpProcessInstanceAction: function (pageName, processName, idInstance, activityId, action) {
        if (!action) {
            action = idInstance;
            activityId = null;
            idInstance = null;
        }

        switch (action) {
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
                this.showProcessInstanceAttachments(processName, idInstance, activityId);
                break;
        }
    },

    /**
     * This function fires the events and updates the context for the changed context variables
     * @param {String} type  classes or processed
     * @param {*} typeName class or process type name
     * @param {*} itemId the id of the item
     */
    handleCpFiring: function (type, typeName, itemId) {
        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextCustomPageObjectType(type)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextCustomPageObjectType(type);
            Ext.GlobalEvents.fireEventArgs("objecttypechanged", [type]);
        }

        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextCustomPageObjectTypeName(typeName)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextCustomPageObjectTypeName(typeName);
            Ext.GlobalEvents.fireEventArgs("objecttypenamechanged", [typeName]);
        }

        if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextCustomPageObjectId(itemId)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextCustomPageObjectId(itemId)
            Ext.GlobalEvents.fireEventArgs("objectidchanged", [itemId]);
        }

        //TODO: handle activity id changed
        //objectcustompageactivityidchanged
    },

    /**
     * this function tells if a custom page exist in the application
     * @param {String} pageName 
     * @returns {Boolean} true if the page exists, false otherwise
     */
    pageExists: function (pageName) {
        return Ext.getStore("custompages.CustomPages").findRecord("name", pageName);
    }
});