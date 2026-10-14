Ext.define('CMDBuildUI.mixins.routes.management.NavigationTree', {
    mixinId: 'managementroutes-navigationtree-mixin',

    /**
     *
     * @param {String} navTreeName
     */
    showNavigationTreeContent: function (navTreeName) {
        const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
        if (detailWindow && !detailWindow.navTreeNode) {
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        }
        CMDBuildUI.util.Navigation.addIntoManagementContainer('navcontent-container', {
            navTreeName: navTreeName
        });

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
            navTreeName
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs('objecttypechanged', [navTreeName]);
    },

    /**
     *
     * @param {String} navTreeName
     * @param {String} className
     */
    navigationCards: function (navTreeName, className) {
        if (
            !CMDBuildUI.util.Navigation.checkCurrentContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
                navTreeName
            )
        ) {
            CMDBuildUI.util.Utilities.redirectTo('navigation/' + navTreeName);
        }

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);
        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
    },

    /**
     *
     * @param {String} navTreeName
     * @param {String} className
     * @param {String} cardId
     */
    navigationCardsId: function (navTreeName, className, cardId) {
        if (
            !CMDBuildUI.util.Navigation.checkCurrentContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
                navTreeName
            )
        ) {
            CMDBuildUI.util.Utilities.redirectTo('navigation/' + navTreeName);
        }

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);
        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
    },

    /**
     *
     * @param {String} navTreeName
     * @param {String} className
     * @param {String} actionType
     * @param {String} action
     * @returns
     */
    navigationCardsCreate: function (navTreeName, className, actionType, action) {
        if (
            !CMDBuildUI.util.Navigation.checkCurrentContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
                navTreeName
            )
        ) {
            CMDBuildUI.util.Utilities.redirectTo('navigation/' + navTreeName);
            return;
        }

        switch (actionType) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                this.showCardCreate(className);
                break;
        }
    },

    /**
     *
     * @param {String} navTreeName
     * @param {String} className
     * @param {String} cardId
     * @param {String} actionType
     * @param {String} action
     * @returns
     */
    navigationCardsIdAction: function (navTreeName, className, cardId, actionType, action) {
        if (
            !CMDBuildUI.util.Navigation.checkCurrentContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
                navTreeName
            )
        ) {
            CMDBuildUI.util.Utilities.redirectTo('navigation/' + navTreeName);
            return;
        }

        switch (actionType) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                this.showCardView(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail:
                this.showCardDetails(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                this.showCardEdit(className, cardId);
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
            case CMDBuildUI.mixins.DetailsTabPanel.actions.clone:
                this.showCardClone(className, cardId);
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations:
                this.showCardCloneandRelations(className, cardId);
                break;
        }
    }
});
