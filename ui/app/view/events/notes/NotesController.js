Ext.define('CMDBuildUI.view.events.notes.NotesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-notes-notes',

    control: {
        '#editbtn': {
            click: 'onEditBtnClick'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button Edit button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().set("editmode", true);
    },

    /**
     * @param {Ext.button.Button} button Save button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const view = this.getView(),
            vm = this.getViewModel(),
            theEvent = vm.get("theEvent"),
            cancelBtn = view.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

        theEvent.save({
            success: function (record, operation) {
                vm.set('tabcounters.notes', CMDBuildUI.util.Utilities.extractTextFromHTML(theEvent.get('notes') || null));
                vm.set('editmode', false);
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button Cancel button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel(),
            theEvent = vm.get("theEvent");

        theEvent.reject();
        vm.set("editmode", false);
    }

});
