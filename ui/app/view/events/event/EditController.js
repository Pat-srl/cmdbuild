Ext.define('CMDBuildUI.view.events.event.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-event-edit',
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
        }, this);

        vm.bind('{theEvent.date}', function (dateChange) {
            const theEvent = vm.get("theEvent");
            theEvent.set('begin', dateChange);
        }, view);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const view = this.getView(),
            vm = this.getViewModel(),
            theEvent = vm.get("theEvent"),
            saveAndCloseBtn = view.down("#saveandclosebtn"),
            cancelBtn = view.down("#cancelbtn");

        this.beforeSave();

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveAndCloseBtn, cancelBtn]);

        theEvent.save({
            callback: function (record, operation, success) {
                CMDBuildUI.util.Utilities.redirectTo(
                    CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                        record.getId(),
                        CMDBuildUI.mixins.DetailsTabPanel.actions.view
                    ));
                Ext.GlobalEvents.fireEventArgs("cardupdated", [record]);
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const view = this.getView(),
            vm = this.getViewModel(),
            theEvent = vm.get("theEvent"),
            saveBtn = view.down("#savebtn"),
            cancelBtn = view.down("#cancelbtn");

        this.beforeSave();

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveBtn, cancelBtn]);

        theEvent.save({
            callback: function (record, operation, success) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);

                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                CMDBuildUI.util.Utilities.redirectTo(
                    CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                        record.getId()));

                Ext.GlobalEvents.fireEventArgs("cardupdated", [record]);
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
        const theEvent = this.getViewModel().get("theEvent");
        theEvent.reject();
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
    },

    privates: {
        /**
         * 
         */
        beforeSave: function () {
            const combo = this.getView().down('#operationcombo'),
                value = combo.getValue();

            if (value) {
                const theEvent = this.getViewModel().get("theEvent");
                theEvent.set('status', value);
            }
        }
    }
});
