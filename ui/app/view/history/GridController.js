Ext.define('CMDBuildUI.view.history.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.history-grid',

    control: {
        "#": {
            beforerender: 'onBeforeRender'
        },
        'tableview': {
            collapsebody: 'onCollapseBody'
        },
        '#viewMode': {
            change: 'onChangeValueCombo'
        },
        '#printHistory': {
            click: 'onPrintHistory'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.history.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            menuitems = [],
            vm = this.getViewModel(),
            store = vm.get("listAttributes");

        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get("objectType"), vm.get("objectTypeName")).then(function (model) {
            // activity field (if object type is process)
            me.addActivityFieldIfNeeded(model, store);

            // get model fields
            me.addAttributesFields(model, store);

            // Create menu items
            Ext.Array.forEach(store.getGroups().getRange(), function (item, index, allitems) {
                Ext.Array.push(menuitems, {
                    focusCls: '',
                    fieldCls: 'make-bold',
                    style: {
                        fontWeight: 'bold'
                    },
                    plain: true,
                    padding: '5 0 0 5',
                    hideOnClick: false,
                    text: item.getGroupKey()
                }, {
                    xtype: 'menuseparator'
                });

                Ext.Array.forEach(item.getRange(), function (elem, index, allitems) {
                    Ext.Array.push(menuitems, {
                        xtype: 'menucheckitem',
                        value: elem.get("value"),
                        text: elem.get("label"),
                        checkHandler: function (item, checked, eOpts) {
                            me.manageAttributes(item, checked);
                        }
                    });
                });

            });

            view.down("#attributesMenu").setMenu(menuitems);
        });
    },

    /**
     * Set the bind of store after combobox value change
     * @param {Ext.form.field.Field} field
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     */
    onChangeValueCombo: function (field, newValue, oldValue, eOpts) {
        var vm = this.getViewModel(),
            onlyMode = newValue == CMDBuildUI.view.history.Grid.only,
            store = vm.get(onlyMode ? 'objectsFields' : 'objects');

        if (oldValue || onlyMode) {
            vm.set("onlyModeActive", onlyMode);
            this.getView().setStore(store);

            vm.set("historyfilter.references", false);
            vm.set("historyfilter.relations", false);
            if (!vm.get("historyfilter.cards") && !vm.get("historyfilter.system")) {
                vm.set("historyfilter.cards", true);
            }
        }

        vm.set("disableButtonPrint", vm.get("onlyModeActive") && vm.get("numSelectedAttributes") === 0);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPrintHistory: function (button, e, eOpts) {
        var vm = this.getViewModel(),
            url = Ext.String.format("{0}/print?{1}", vm.get("storedata.proxyurl"), Ext.Object.toQueryString({ types: vm.get("storedata.historyTypes") }));

        if (vm.get("onlyModeActive")) {
            url = Ext.String.format("{0}&{1}", url, Ext.Object.toQueryString({ attributes: Ext.JSON.encode(vm.get("printAttributes")) }));
        }

        CMDBuildUI.util.Utilities.openPrintPopup(url, "csv");
    },

    /**
     * Clear plugin widget for history rows
     *
     */
    clearAllWidgetRows: function () {
        var me = this,
            view = this.getView();

        Ext.Array.forEach([view.freeRowContexts, view.liveRowContexts], function (item, index, allitems) {
            Ext.Object.eachValue(item, function (row, indexRow, allrows) {
                me.clearWidgetRow(row);
            });
        });
    },

    /**
     * On row collapse
     *
     * @param {HTMLElement} rowNode
     * @param {Ext.data.Model} record
     * @param {HTMLElement} expandRow
     * @param {Object} eOpts
     */
    onCollapseBody: function (rowNode, record, expandRow, eOpts) {
        // remove widget otherwise when switching the view mode
        // old rows are keeped in cache.
        var view = this.getView();
        this.clearWidgetRow(view.liveRowContexts[record.internalId]);
    },

    privates: {
        /**
         * @property {Number} _loaders
         *
         * Used to check for loaders count.
         */
        _loaders: 0,

        /**
         * Add activity field if object type is process
         * @param {Object} model
         * @param {Ext.data.Store} store
         */
        addActivityFieldIfNeeded: function (model, store) {
            if (model.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                store.add({
                    value: 'Code',
                    label: CMDBuildUI.locales.Locales.common.tabs.activity,
                    group: ''
                });
            }
        },

        /**
         * Add attributes fields
         * @param {Object} model
         * @param {Ext.data.Store} store
         */
        addAttributesFields: function (model, store) {
            Ext.Array.forEach(model.getFields(), function (field) {
                var attrconf = field.attributeconf;
                if (!Ext.String.startsWith(field.name, "_") && !field.hidden && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                    store.add({
                        value: field.name,
                        label: attrconf.description_localized || field._localized_description || field.description,
                        group: attrconf._group_description_translation || attrconf._group_description || CMDBuildUI.locales.Locales.common.attributes.nogroup
                    });
                }
            });

            // Add notes field
            store.add({
                value: "Notes",
                label: CMDBuildUI.locales.Locales.common.tabs.notes,
                group: CMDBuildUI.locales.Locales.common.tabs.notes
            });
        },

        /**
         * Manage the selection of attributes on menu
         * @param {Ext.menu.CheckItem} record
         * @param {Boolean} checked
         */
        manageAttributes: function (record, checked) {
            var me = this,
                vm = this.getViewModel(),
                value = record.value,
                label = record.text,
                store = this.getView().getStore(),
                attributesCalled = vm.get("attributesCalled"),
                selectedAttributes = vm.get("selectedAttributes"),
                printAttributes = vm.get("printAttributes"),
                numSelectedAttributes = vm.get("numSelectedAttributes");

            if (checked) {
                Ext.Array.push(selectedAttributes, label);
                Ext.Array.push(printAttributes, value);
                numSelectedAttributes += 1;
            } else {
                Ext.Array.remove(selectedAttributes, label);
                Ext.Array.remove(printAttributes, value);
                numSelectedAttributes -= 1;
            }

            vm.set("selectedAttributes", selectedAttributes);
            vm.set("numSelectedAttributes", numSelectedAttributes);

            if (!Ext.Array.contains(attributesCalled, value)) {
                me.mask();
                Ext.Ajax.request({
                    url: Ext.String.format("{0}/changes?attrs={1}", vm.get("storedata.proxyurl"), value),
                    method: 'GET',
                    params: {
                        types: 'cards,system'
                    },
                    callback: function (options, success, response) {
                        if (success) {
                            var data = Ext.decode(response.responseText).data,
                                model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(
                                    CMDBuildUI.util.helper.ModelHelper.getModelName(vm.get("objectType"), vm.get("objectTypeName"))
                                ),
                                modelField = model.getField(value);

                            Ext.Array.push(attributesCalled, value);
                            vm.set("attributesCalled", attributesCalled);

                            Ext.Array.forEach(data, function (item, index, allitems) {

                                item._modelField = modelField;
                                item._fieldName = label;

                                if (modelField && modelField.attributeconf && modelField.attributeconf.password) {
                                    item._newValue = "•••••";
                                } else if (modelField && modelField.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file) {
                                    item._newValue = item["_" + value + "_name"];
                                } else if (item["_" + value + "_description_translation"]) {
                                    item._newValue = item["_" + value + "_description_translation"];
                                } else if (item["_" + value + "_description"]) {
                                    item._newValue = item["_" + value + "_description"];
                                } else if (item["_" + value + "_details"]) {
                                    item._newValue = Ext.Array.pluck(item["_" + value + "_details"], "_description_translation").join(", ");
                                } else {
                                    item._newValue = Ext.isEmpty(item[value]) ? null : item[value];
                                }
                            });

                            store.add(data);
                        }
                        me.unmask();
                    }
                });
            }

            store.addFilter({
                id: 'attributesFilter',
                property: '_fieldName',
                operator: 'in',
                value: selectedAttributes
            });
        },

        /**
         * Add load mask
         */
        mask: function () {
            if (this._loaders <= 0) {
                this.getView().getView().loadMask.show();
            }
            this._loaders++;
        },

        /**
         * Remove load mask
         */
        unmask: function () {
            this._loaders--;
            if (this._loaders <= 0) {
                this.getView().getView().loadMask.hide();
            }
        },

        /**
         * Clear plugin widget for specific row
         *
         * @param {Ext.grid.RowContext} row
         */
        clearWidgetRow: function (row) {
            var view = this.getView(),
                plugin = view.getPlugin(),
                widgetName = plugin.getId() + '-' + view.getView().getId(),
                widget = row.widgets[widgetName];

            delete row.widgets[widgetName];
            delete row.viewModel;
            if (widget) {
                widget.destroy();
            }
        }
    }

});