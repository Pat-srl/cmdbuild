Ext.define('CMDBuildUI.view.notes.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.notes-panel',

    control: {
        '#': {
            activate: 'onActivate'
        },
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
     * Refresh data on tab activate event
     * 
     * @param {CMDBuildUI.view.relations.masterdetail.TabPanelContainer} view 
     * @param {Object} eOpts 
     */
    onActivate: function (view, eOpts) {
        var vm = this.getViewModel();
        // get model
        var model = CMDBuildUI.util.helper.ModelHelper.getNotesModel(vm.get("objectType"), vm.get("objectTypeName"));
        if (vm.get("objectId")) {
            // set instance to ViewModel
            vm.linkTo('theObject', {
                type: model.getName(),
                id: vm.get("objectId")
            });
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
        var vm = this.getViewModel(),
            form = this.getView(),
            updatingEventName = '',
            cancelBtn = form.down("#cancelbtn");

        switch (vm.get('objectType')) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                updatingEventName = 'cardupdated';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                updatingEventName = 'processinstanceupdated';
                break;
        }

        if (vm.get('objectType') == 'process') {
            vm.get('theObject').set('_activity', vm.get('activityId'));
        }

        if (form.isValid()) {
            button.showSpinner = true;
            CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);
            vm.get("theObject").save({
                callback: function (record, operation, success) {
                    vm.set("editmode", false);
                    if (updatingEventName) {
                        Ext.GlobalEvents.fireEventArgs(updatingEventName, [record]);
                    }
                    if (vm.get('popupId')) {
                        CMDBuildUI.util.Utilities.closePopup(vm.get('popupId'));
                    }
                    CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                    vm.set("tabcounters.notes", !Ext.isEmpty(CMDBuildUI.util.Utilities.extractTextFromHTML(record.get("Notes"))));
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            });
        } else {
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        }
    },

    /**
     * @param {Ext.button.Button} button Cancel button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.getViewModel().get("theObject").reject(); // discard changes
        this.getViewModel().set("editmode", false);
    }
});