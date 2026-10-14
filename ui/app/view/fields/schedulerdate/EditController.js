Ext.define('CMDBuildUI.view.fields.schedulerdate.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-edit',
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
            view.generateForm();
        });

        vm.bind('{theEvent.date}', function (dateChange) {
            const theEvent = vm.get("theEvent");
            theEvent.set('begin', dateChange)
        }, view)
    },

    /**
   * @param {Ext.button.Button} button
   * @param {Event} e
   * @param {Object} eOpts
   */
    onSaveBtnClick: function (button, e, eOpts) {
        const view = this.getView(),
            theEvent = this.getViewModel().get("theEvent");
        view.fireEvent('popupsave', view, theEvent);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        const view = this.getView(),
            theEvent = this.getViewModel().get("theEvent");
        view.fireEvent('popupsaveandclose', view, theEvent);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const view = this.getView(),
            theEvent = this.getViewModel().get("theEvent");
        view.fireEvent('popupclose', view, theEvent);
    },

    /**
     * 
     * @param {*} originalEvent 
     */
    savePopup: function (originalEvent) {
        const theEvent = this.getViewModel().get("theEvent"),
            modified = theEvent.modified;

        //sets the modified fields of the form --> in the original theEvent record
        if (Object.getOwnPropertyNames(modified).length != 0) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            const sequence = originalEvent.store.associatedEntity
            for (var field in modified) {
                if (field != '_category_code' && field != '_priority_code') {
                    originalEvent.set(field, theEvent.get(field));
                    sequence._dirty_edit = true;
                }
            }
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        }
    }
});
