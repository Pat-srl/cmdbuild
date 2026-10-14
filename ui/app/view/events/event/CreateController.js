Ext.define('CMDBuildUI.view.events.event.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-event-create',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        vm.bind('{theEvent}', function (theEvent) {
            if (theEvent) {
                view.generateForm();
            }
        }, view);

        vm.bind('{theEvent.date}', function (dateChange) {
            const theEvent = vm.get("theEvent");
            theEvent.set('begin', dateChange);
        }, view);
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const form = this.getView(),
            vm = this.getViewModel(),
            theEvent = vm.get('theEvent'),
            saveAndCloseBtn = form.down("#saveandclosebtn"),
            cancelBtn = form.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveAndCloseBtn, cancelBtn]);

        if (!theEvent.get('timeZone')) {
            theEvent.set('timeZone', CMDBuildUI.util.helper.UserPreferences.getPreferences().get(CMDBuildUI.model.users.Preference.timezone));
        }
        theEvent.save({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Utilities.redirectTo(
                        CMDBuildUI.util.Navigation.getScheduleBaseUrl(theEvent.getId(), CMDBuildUI.mixins.DetailsTabPanel.actions.view)
                    )
                }

                Ext.GlobalEvents.fireEventArgs("cardcreated", [records]);
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        })
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const form = this.getView(),
            vm = this.getViewModel(),
            theEvent = vm.get('theEvent'),
            saveBtn = form.down("#savebtn"),
            cancelBtn = form.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveBtn, cancelBtn]);

        theEvent.save({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Utilities.redirectTo(
                        CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                            theEvent.getId(),
                            CMDBuildUI.mixins.DetailsTabPanel.actions.view
                        ));
                }

                CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);

                // close detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();

                Ext.GlobalEvents.fireEventArgs("cardcreated", [records]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        })
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
    }

});
