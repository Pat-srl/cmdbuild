Ext.define('CMDBuildUI.view.fields.bufferedcombo.SelectionPopupController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-bufferedcombo-selectionpopup',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#searchtextinput': {
            beforerender: 'onSearchTextInputBeforeRender',
            specialkey: 'onSearchSpecialKey'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var modelname = vm.get('modelname');
        var columns = CMDBuildUI.util.administration.helper.GridHelper.getColumns(modelname);
        if (vm.get("searchvalue")) {
            view.lookupReference("searchtextinput").focus();
        }
        // model name
        vm.set("storeinfo.modelname", modelname);

        // reconfigure table
        view.reconfigure(null, columns);

        // set autoload to true
        vm.set("storeinfo.autoload", true);

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onAddCardBtnClick(item, event, eOpts);
            },
            vm.get("objectTypeName"),
            vm.get("objectType")
        );
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddCardBtnClick: function (item, event, eOpts) {
        var vm = this.getViewModel();
        var title = Ext.String.format(
            "{0} {1}",
            CMDBuildUI.locales.Locales.classes.cards.addcard,
            vm.get("objectTypeDescription")
        );
        var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
            xtype: 'classes-cards-card-create',
            padding: 10,
            fireGlobalEventsAfterSave: false,
            viewModel: {
                data: {
                    objectTypeName: item.objectTypeName
                }
            },
            buttons: [{
                text: CMDBuildUI.locales.Locales.common.actions.save,
                formBind: true, //only enabled once the form is valid
                disabled: true,
                ui: 'management-primary-small',
                autoEl: {
                    'data-testid': 'selection-popup-card-create-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                },
                handler: function (button, e) {
                    var form = button.up("form"),
                        cancelBtn = form.down("#cancelbtn");
                    if (form.isValid()) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        button.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);
                        var object = form.getViewModel().get("theObject");
                        object.save({
                            success: function (record, operation) {
                                var filters = vm.get("records").getFilters();
                                filters.add({
                                    property: 'positionOf',
                                    value: record.getId()
                                });
                                vm.set("selection", record);
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                popup.destroy(true);
                            },
                            failure: function (record, operation) {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                            }
                        });
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                ui: 'secondary-action-small',
                itemId: 'cancelbtn',
                autoEl: {
                    'data-testid': 'selection-popup-card-create-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                },
                handler: function (button, e) {
                    popup.destroy(true);
                }
            }]
        });
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        if (vm.get("searchvalue")) {
            var store = vm.get("records");
            if (store) {
                // add filter
                store.getAdvancedFilter().addQueryFilter(vm.get("searchvalue"));
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get("records");
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    onSearchTextInputBeforeRender: function (field, eOpts) {
        var view = this.getView();
        field.lookupViewModel().set("searchvalue", view.getDefaultSearchFilter());
    },

    /**
     * 
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record 
     * @param {HTMLElement} element 
     * @param {Number} rowindex 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onRowDblClick: function (view, record, element, rowindex, e, eOpts) {
        this.onSaveBtnClick();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var view = this.getView();
        var vm = this.getViewModel();
        var selection;
        if (view.getSelection() && view.getSelection().length && vm.get("records").getById(view.getSelection()[0].getId())) {
            selection = view.getSelection();
        }
        view.setValueOnParentCombo(selection);
        CMDBuildUI.util.helper.FormHelper.endSavingForm();
        view.closePopup();
    }
});