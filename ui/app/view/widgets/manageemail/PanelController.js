Ext.define('CMDBuildUI.view.widgets.manageemail.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-manageemail-panel',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.manageemail.PanelController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            obj = vm.get("theTarget"),
            theWidget = vm.get("theWidget");

        // this type of widget does not support inline mode
        if (theWidget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }

        // update emails from tempaltes
        if (obj) {
            obj.loadTemplates().then(function (templates) {
                obj.updateObjEmailsFromTemplates(false, true);
            });
        }

        if (theWidget.get("_required")) {
            theWidget.getOwner().setValue(true);
        }

        // add emails grid
        vm.set("emails", obj.emails());
        view.add({
            xtype: 'emails-grid',
            formMode: view.getFormMode()
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    }
});