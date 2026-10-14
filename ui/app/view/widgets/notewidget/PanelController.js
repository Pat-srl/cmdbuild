Ext.define('CMDBuildUI.view.widgets.notewidget.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-notewidget-panel',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            theWidget = vm.get("theWidget"),
            owner = theWidget.getOwner(),
            content, valueBind, editmode;

        if (theWidget.get('_inline')) {
            valueBind = '{theTarget.Notes}';
        } else {
            valueBind = '{notes}';
            vm.set("notes", vm.get("theTarget.Notes") || '');
        }

        if (view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
            content = {
                xtype: 'panel',
                scrollable: true,
                cls: Ext.baseCSSPrefix + 'selectable',
                bind: {
                    html: valueBind
                }
            };
            editmode = false;
        } else {
            content = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                flex: 1,
                bind: {
                    value: valueBind
                },
                listeners: {
                    change: function (field, newValue, oldValue, eOpts) {
                        if (theWidget.get("_required")) {
                            owner.setValue(!Ext.isEmpty(newValue) ? true : null);
                            owner.validate();
                        }
                    }
                }
            });
            editmode = true;
        }

        vm.set("editmode", editmode);
        view.add(content);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        vm.set("theTarget.Notes", vm.get("notes"));
        this.closePopup();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.closePopup();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.closePopup();
    },

    privates: {
        /**
         * Close widget popup.
         */
        closePopup: function () {
            this.getView().fireEvent("popupclose");
        }
    }
});