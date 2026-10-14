Ext.define('CMDBuildUI.view.fields.bufferedcombo.BufferedComboFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-bufferedcombofield',

    control: {
        '#': {
            beforequery: 'onBeforeQuery',
            change: 'onChange',
            cleartrigger: 'onClearTrigger',
            searchtrigger: 'onSearchTrigger',
            expand: 'onExpand'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view
     * @param {Numeric|String} newvalue
     * @param {Numeric|String} oldvalue
     * @param {Object} eOpts
     */
    onChange: function (view, newvalue, oldvalue, eOpts) {
        var object = view._ownerRecord;
        if (!object && view.getRecordLinkName()) {
            object = view.lookupViewModel().get(view.getRecordLinkName());
        }
        if (object) {
            var selected = view.getSelection();
            var key = Ext.String.format("_{0}_description", view.getName());
            var value = selected ? selected.get(view.getDisplayField()) : null;
            if (object.isModel) {
                object.set(key, value);
            } else {
                object[key] = value;
            }
            CMDBuildUI.util.Logger.log("set selection ", CMDBuildUI.util.Logger.levels.debug);
        }
        if (view.hasBindingValue && Ext.isEmpty(newvalue) && !Ext.isEmpty(oldvalue)) {
            view.getBind().value.setValue(null);
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} combo
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onClearTrigger: function (combo, trigger, eOpts) {
        combo.lastSelectedRecords = [];
        this.getViewModel().set("selection", null);
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchTrigger: function (view, trigger, eOpts) {
        // prevent multiple popup opening
        if (!view.popupAlreadyOpened) {
            view.popupAlreadyOpened = true;
            var objectTypeDescription = view.ownerCt.getFieldLabel();
            var title = Ext.String.format(
                "{0} - {1}",
                CMDBuildUI.locales.Locales.common.grid.list,
                objectTypeDescription
            );            
            var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
                xtype: 'fields-bufferedcombo-selectionpopup',
                defaultSearchFilter: eOpts.searchquery ? eOpts.searchquery : null,
                viewModel: {
                    data: {
                        modelname: view.getModelname(),
                        storealias: view.getStorealias(),
                        storeinfo: {
                            type: null,
                            proxyurl: view.getViewModel().get("storeinfo.proxyurl"),
                            // proxyurl: eval(view.getModelname()).getProxy().getUrl(),
                            autoload: false
                        },
                        selection: view.getSelection()
                    }
                },

                setValueOnParentCombo: function (record) {
                    if (view.column) {
                        var object = view._ownerRecord;
                        object.set(view.getName(), record[0].getId());
                    }
                    view.setSelection(record);
                },

                closePopup: function () {
                    popup.removeAll(true);
                    popup.close();
                }
            }, {
                close: function () {
                    // set to false flag
                    view.popupAlreadyOpened = false;
                }
            });


        }
    },

    /**
     * Before query listener.
     * @param {Object} queryPlan An object containing details about the query to be executed.
     * @param {String} queryPlan.query The query value to be used to match against the ComboBox's {@link #valueField}.
     * @param {String} queryPlan.lastQuery The query value used the last time a store query was made.
     * @param {Boolean} queryPlan.forceAll If `true`, causes the query to be executed even if the minChars threshold is not met.
     * @param {Boolean} queryPlan.cancel A boolean value which, if set to `true` upon return, causes the query not to be executed.
     * @param {Boolean} queryPlan.rawQuery If `true` indicates that the raw input field value is being used, and upon store load,
     * the input field value should **not** be overwritten.
     */
    onBeforeQuery: function (queryPlan) {
        var view = this.getView();
        if (!view._allowexpand) {
            view.expand(queryPlan.query);
            return false;
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.fields.reference.ReferenceCombo} view 
     * @param {Object} eOpts 
     */
    onExpand: function (view, eOpts) {
        var picker = view.getPicker();
        if (picker.getSelectionModel().hasSelection()) {
            var selected = picker.getSelectionModel().getSelection()[0];
            var itemNode = picker.getNode(selected);

            if (itemNode) {
                picker.setScrollY(itemNode.offsetTop);
            }
        }
    }
});