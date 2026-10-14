Ext.define('CMDBuildUI.mixins.routes.management.Events', {
    mixinId: 'managementroutes-events-mixin',

    /**
     *
     * @param {*} action
     */
    beforeShowEvents: function (action) {
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) == false) {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        }

        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {

            //check if the objectid is null
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(null)) {

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [null]);

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(null);
            }

            action.stop();
        } else {
            action.resume();
        }
    },

    /**
     *
     * @param {String} eventId
     */
    showEvents: function (eventId) {
        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        CMDBuildUI.util.Navigation.addIntoManagementContainer('events-container', {
            viewModel: {
                data: {
                    selectedId: eventId
                }
            }
        });

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            eventId
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [type]);
    },

    /**
     *
     * @param {*} eventId
     * @param {*} action
     */
    beforeShowEvent: function (eventId, action) {
        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {

            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(eventId)) {

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(eventId);

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [eventId]); //it's not corect firing this event allways
            }
            action.stop();
        } else {
            action.resume();
        }
    },

    /**
     *
     * @param {String} eventId
     */
    showEvent: function (eventId) {
        this.showEvents(eventId)
    },

    /**
     *
     * @param {*} eventId
     * @param {*} action
     */
    onBeforeShowEventWindow: function (eventId, action) {
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) == false) {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        }

        // fix variables for create form
        if (!action) {
            action = eventId;
            eventId = null;
        }

        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.event;
        if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, type)) {
            this.showEvents(eventId);

        } else {
            //check if the objectid is null
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(eventId)) {

                //fires global event
                Ext.GlobalEvents.fireEventArgs("objectidchanged", [eventId]);

                //updates the context variables
                CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(eventId);
            }
        }
        action.resume();
    },

    /**
     *
     * @param {*} eventId
     */
    showEventView: function (eventId) {
        this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.view)
    },

    /**
     *
     * @param {*} eventId
     */
    showEventEdit: function (eventId) {
        this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
    },

    /**
     *
     * @param {*} eventId
     */
    showEventNotes: function (eventId) {
        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_note_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.notes);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },

    /**
     *
     * @param {*} eventId
     */
    showEventHistory: function (eventId) {
        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_history_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.history);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },

    /**
     *
     * @param {*} eventId
     */
    showEventAttachments: function (eventId) {
        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) && privileges.card_tab_attachment_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.attachments);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId), eventId);
        }
    },

    /**
     *
     * @param {*} eventId
     */
    showEventCreate: function (eventId) {
        this.showEventTabPanel(null, CMDBuildUI.mixins.DetailsTabPanel.actions.create);
    },

    /**
     *
     * @param {*} eventId
     */
    showEventEmails: function (eventId) {
        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_email_access_read) {
            this.showEventTabPanel(eventId, CMDBuildUI.mixins.DetailsTabPanel.actions.emails);
        } else {
            this.redirectTo(Ext.String.format("events/{0}", eventId));
        }
    },

    privates: {
        /**
         *
         * @param {*} eventId
         * @param {*} action
         */
        showEventTabPanel: function (eventId, action) {
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action)) {
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('events-tabpanel', {
                    tabtools: [],
                    viewModel: {
                        data: {
                            action: action,
                            selectedId: eventId
                        }
                    }
                });
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
            }
        }
    }
})