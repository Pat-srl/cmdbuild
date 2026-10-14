Ext.define('CMDBuildUI.view.widgets.customform.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-customform-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        'tableview': {
            actionclonerowclick: 'onActionCloneRowClick',
            actioneditrowclick: 'onActionEditRowClick',
            actionremoverowclick: 'onActionRemoveRowClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.customform.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        // debugger;
        if (!Ext.ClassManager.isCreated(view.getModelName())) {
            // generate model
            var theWidget = me.getViewModel().get('theWidget');
            CMDBuildUI.view.widgets.customform.Utilities.getModel(theWidget).then(function (model) {
                // render widget
                me.renderWidget();
            });
        } else {
            // render widget
            me.renderWidget();
        }
    },

    /**
     * On popup close handler.
     */
    onBeforeDestroy: function () {
        // update output variable in target object
        var view = this.getView(),
            store = this.getViewModel().get('dataStore'),
            rows = [],
            row = view.lookupViewModel().get('theRow'),
            theWidget = this.getViewModel().get('theWidget'),
            storedata;
        if (store) {
            storedata = store.getRange();
        } else {
            storedata = [row];
        }
        rows = CMDBuildUI.view.widgets.customform.Utilities.serialize(theWidget, storedata);
        if (!Ext.isEmpty(view.getOutput())) {
            view.getTarget().set(view.getOutput(), rows);
        }
    },

    /**
     * Render widget content
     */
    renderWidget: function () {
        var view = this.getView(),
            vm = this.getViewModel(),
            theWidget = vm.get('theWidget'),
            modelname = this.getView().getModelName(),
            conf;

        if (theWidget.get('Layout').toLowerCase() === 'form') {
            conf = this.getFormConfig();
            var theRow = Ext.create(modelname);
            vm.set('theRow', theRow);
        } else if (theWidget.get('Layout').toLowerCase() === 'grid') {
            conf = this.getGridConfig();

            var storeconf = {
                model: modelname,
                proxy: 'memory'
            };

            // add listeners to validate rows when is inline
            if (theWidget.get('_inline')) {
                // set min height
                conf.minHeight = view.up('form').getHeight() * 0.25;

                function validateData(store) {
                    var owner = theWidget.getOwner();

                    if (owner) {
                        var hasInvalidRecords =
                            store.findBy(function (record) {
                                return !record.isValid();
                            }) !== -1;
                        if (hasInvalidRecords) {
                            owner.addError(CMDBuildUI.locales.Locales.widgets.customform.datanotvalid, theWidget);
                        } else {
                            owner.removeError(CMDBuildUI.locales.Locales.widgets.customform.datanotvalid, theWidget);
                        }
                    }
                }
                storeconf.listeners = {
                    datachanged: validateData,
                    update: validateData
                };
            }

            vm.setStores({
                dataStore: storeconf
            });
        } else {
            // TODO: Show error message
        }
        view.removeAll(true);
        view.add(conf);
        this.loadData();
    },

    /**
     * Load data
     * @param {Boolean} force If `true` data is always readed from the server or from the configuration.
     * Data saved in output variable in target object will be ignored.
     */
    loadData: function (force) {
        var view = this.getView(),
            vm = this.getViewModel(),
            theWidget = vm.get('theWidget'),
            isForm = theWidget.get('Layout').toLowerCase() === 'form',
            model_attributes = CMDBuildUI.view.widgets.customform.Utilities.getAttributesForModelWidget(theWidget);

        function callbackFn(response) {
            if (isForm) {
                if (response.length) {
                    if (response[0].isModel) {
                        vm.get('theRow').updateDataFromObject(response[0].getData());
                    } else {
                        vm.get('theRow').updateDataFromObject(response[0]);
                    }
                }
            } else {
                vm.get('dataStore').add(response);
            }
        }

        // clear store data
        if (vm.get('dataStore')) {
            vm.get('dataStore').removeAll();
        }

        // When RefreshBehaviour is everytime the load is performed with binding defined in `onTargetFormOpen`.
        // // check refresh behaviour
        // if (theWidget.get("RefreshBehaviour") && theWidget.get("RefreshBehaviour").toLowerCase() === 'everytime') {
        //     force = true;
        // }

        // get data from output
        var outputdata = vm.get('theTarget').get(view.getOutput()),
            serializationconfig = CMDBuildUI.view.widgets.customform.Utilities.getSerializationConfig(theWidget);
        if (!force && outputdata !== undefined) {
            if (serializationconfig.type === 'json') {
                CMDBuildUI.view.widgets.customform.Utilities.loadDataFromJson(outputdata, callbackFn);
            } else {
                CMDBuildUI.view.widgets.customform.Utilities.loadDataFromRawText(
                    outputdata,
                    serializationconfig,
                    callbackFn,
                    model_attributes
                );
            }
            return;
        }

        // set empty value for data type if not specified
        if (!theWidget.get('DataType')) {
            theWidget.set('DataType', '');
        }

        var theTarget = vm.get('theTarget');
        CMDBuildUI.view.widgets.customform.Utilities.loadData(theWidget, theTarget, callbackFn, model_attributes);
    },

    /**
     * @param {Ext.view.Table} tableview
     * @param {CMDBuildUI.model.base.Base} record
     * @param {Integer} rowIndex
     * @param {Integer} colIndex
     */
    onActionRemoveRowClick: function (tableview, record, rowIndex, colIndex) {
        // erase record
        record.erase();
    },

    /**
     * @param {Ext.view.Table} tableview
     * @param {CMDBuildUI.model.base.Base} record
     * @param {Integer} rowIndex
     * @param {Integer} colIndex
     */
    onActionEditRowClick: function (tableview, record, rowIndex, colIndex) {
        var config = this.getFormConfig(),
            popup,
            theRow = record.clone(),
            theTarget = this.getViewModel().get('theTarget');

        Ext.apply(config, {
            viewModel: {
                data: {
                    theRow: theRow,
                    theTarget: theTarget
                }
            },
            buttons: [
                {
                    reference: 'cancelBtn',
                    itemId: 'cancelBtn',
                    ui: 'secondary-action-small',
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    autoEl: {
                        'data-testid': 'widgets-customform-form-cancel'
                    },
                    handler: function () {
                        popup.close();
                    }
                },
                {
                    reference: 'saveBtn',
                    itemId: 'saveBtn',
                    formBind: true,
                    ui: 'management-primary-small',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'widgets-customform-form-save'
                    },
                    handler: function () {
                        var changes = theRow.getChanges();
                        for (var c in changes) {
                            record.set(c, theRow.get(c));
                        }
                        popup.close();
                    }
                }
            ]
        });

        popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            Ext.String.format(
                '{0} - {1}',
                this.getViewModel().get('theWidget').get('_label'),
                CMDBuildUI.locales.Locales.widgets.customform.editrow
            ),
            config,
            {
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                beforeclose: function (panel, eOpts) {
                    panel.removeAll(true);
                }
            }
        );
    },

    /**
     * @param {Ext.view.Table} tableview
     * @param {CMDBuildUI.model.base.Base} record
     * @param {Integer} rowIndex
     * @param {Integer} colIndex
     */
    onActionCloneRowClick: function (tableview, record, rowIndex, colIndex) {
        var recordData = record.getData();
        delete recordData._id;
        this.getViewModel().get('dataStore').add([recordData]);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent('popupclose');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} e
     */
    onRefreshBtnClick: function (button, e) {
        // update ajax action id
        CMDBuildUI.util.Ajax.setActionId('widget.customform.refreshdata');
        // load data
        this.loadData(true);
        //CMDBuildUI.view.widgets.customform.Panel.loadData(theWidget, theTarget, dataStore, outputdata, serializationconfig, true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} e
     */
    onAddRowBtnClick: function (button, e) {
        var config = this.getFormConfig(),
            popup,
            theRow = Ext.create(this.getView().getModelName()),
            theTarget = this.getViewModel().get('theTarget'),
            vm = this.getViewModel();

        Ext.apply(config, {
            viewModel: {
                data: {
                    theRow: theRow,
                    theTarget: theTarget
                }
            },
            buttons: [
                {
                    reference: 'cancelBtn',
                    itemId: 'cancelBtn',
                    ui: 'secondary-action-small',
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    autoEl: {
                        'data-testid': 'widgets-customform-form-cancel'
                    },
                    handler: function () {
                        popup.close();
                    }
                },
                {
                    reference: 'saveBtn',
                    itemId: 'saveBtn',
                    formBind: true,
                    ui: 'management-primary-small',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'widgets-customform-form-save'
                    },
                    handler: function () {
                        vm.get('dataStore').add(theRow);
                        popup.close();
                    }
                }
            ]
        });

        popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            Ext.String.format(
                '{0} - {1}',
                this.getViewModel().get('theWidget').get('_label'),
                CMDBuildUI.locales.Locales.widgets.customform.addrow
            ),
            config,
            {
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                beforeclose: function (panel, eOpts) {
                    panel.removeAll(true);
                }
            }
        );
    },

    onImportBtnClick: function (btn, eOpts) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.widgets.customform.import,
            {
                xtype: 'widgets-customform-import',
                attributes: CMDBuildUI.view.widgets.customform.Utilities.getAttributesForModelWidget(
                    this.getViewModel().get('theWidget')
                ),
                gridStore: btn.lookupViewModel().get('dataStore'),

                closePopup: function () {
                    popup.close();
                }
            },
            null,
            {
                width: 600,
                height: 500
            }
        );
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onExportBtnClick: function (btn, eOpts) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.widgets.customform.export,
            {
                xtype: 'widgets-customform-export',
                attributes: CMDBuildUI.view.widgets.customform.Utilities.getAttributesForModelWidget(
                    this.getViewModel().get('theWidget')
                ),
                gridStore: btn.lookupViewModel().get('dataStore'),

                closePopup: function () {
                    popup.close();
                }
            },
            null,
            {
                width: 600,
                height: 500
            }
        );
    },

    privates: {
        /**
         * Get grid configuration
         *
         * @return {Object} grid configuration
         */
        getGridConfig: function () {
            var columns = [],
                vm = this.getViewModel(),
                fields = CMDBuildUI.view.widgets.customform.Utilities.getAttributesForModelWidget(vm.get('theWidget')),
                theWidget = vm.get('theWidget'),
                tbar,
                plugins;

            // define columns
            for (var i = 0; i < fields.length; i++) {
                var field = fields[i],
                    column = CMDBuildUI.util.helper.GridHelper.getColumn(field, {
                        allowFilter: false
                    });
                if (column) {
                    if (field.writable && theWidget.get('ReadOnly') !== 'true') {
                        column.editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);
                        column.editor.allowBlank = !field.mandatory;
                        if (column.editor) {
                            if (
                                field.cmdbuildtype.toLowerCase() ===
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase() ||
                                field.cmdbuildtype.toLowerCase() ===
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()
                            ) {
                                // override editor listeners to update field description
                                column.editor.listeners = {
                                    change: function (field, newvalue, oldvalue, eOpts) {
                                        var object = field.getRefOwner().context.record;
                                        if (object) {
                                            object.set(
                                                Ext.String.format('_{0}_description', field.name),
                                                field.getDisplayValue()
                                            );
                                        }
                                    }
                                };
                            }

                            //change xtype of editor when colunm is reference (only in grid view)
                            if (
                                field.cmdbuildtype.toLowerCase() ===
                                CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()
                            ) {
                                column.editor.xtype = 'referencecombofield';
                                column.editor.filterRecordLinkName = 'theTarget';
                                column.editor.customFormTargetObj = vm.get('theTarget');
                            }
                        }

                        if (field.mandatory) {
                            column.text += ' *';
                        }
                    }
                    columns.push(column);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Configuration error for column ' + field.getName(),
                        CMDBuildUI.util.Logger.levels.error
                    );
                }
            }

            if (this.getView().getFormMode() !== CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                // add view row action columns
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    menuDisabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{!permissions.clone}'
                    },
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.widgets.customform.clonerow,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actionclonerowclick', grid, record, rowIndex, colIndex);
                    },
                    autoEl: {
                        'data-testid': 'widgets-customform-grid-row-clone'
                    }
                });
                // add edit row action columns
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    menuDisabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{!permissions.modify}'
                    },
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.widgets.customform.editrow,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actioneditrowclick', grid, record, rowIndex, colIndex);
                    },
                    autoEl: {
                        'data-testid': 'widgets-customform-grid-row-modify'
                    }
                });
                // add delete row action columns
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    menuDisabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{!permissions.delete}'
                    },
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.widgets.customform.deleterow,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actionremoverowclick', grid, record, rowIndex, colIndex);
                    },
                    autoEl: {
                        'data-testid': 'widgets-customform-grid-row-delete'
                    }
                });

                // configure tbar
                tbar = [
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.widgets.customform.addrow,
                        ui: 'management-primary',
                        reference: 'addrowbtn',
                        itemid: 'addrowbtn',
                        handler: 'onAddRowBtnClick',
                        bind: {
                            disabled: '{!permissions.add}'
                        },
                        autoEl: {
                            'data-testid': 'widgets-customform-grid-addrow'
                        }
                    },
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.widgets.customform.import,
                        ui: 'management-primary',
                        reference: 'importbtn',
                        itemid: 'importbtn',
                        handler: 'onImportBtnClick',
                        bind: {
                            disabled: '{!permissions.import}'
                        },
                        autoEl: {
                            'data-testid': 'widgets-customform-grid-import'
                        }
                    },
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.widgets.customform.export,
                        ui: 'management-primary',
                        reference: 'exportbtn',
                        itemid: 'exportbtn',
                        handler: 'onExportBtnClick',
                        bind: {
                            disabled: '{!permissions.export}'
                        },
                        autoEl: {
                            'data-testid': 'widgets-customform-grid-export'
                        }
                    },
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.widgets.customform.refresh,
                        ui: 'management-primary',
                        reference: 'refreshbtn',
                        itemid: 'refreshbtn',
                        handler: 'onRefreshBtnClick',
                        hidden: true,
                        bind: {
                            hidden: '{!permissions.refresh}'
                        },
                        autoEl: {
                            'data-testid': 'widgets-customform-grid-refresh'
                        }
                    }
                ];

                // configure plugins
                if (vm.get('permissions.modify') && !theWidget.get('DisableCellEditing')) {
                    plugins = {
                        ptype: 'cellediting',
                        clicksToEdit: 1,
                        skipValidationOnFocusOut: true
                    };
                }
            }

            return {
                xtype: 'grid',
                forceFit: true,
                loadMask: true,
                columns: columns,
                bind: {
                    store: '{dataStore}'
                },
                plugins: plugins,
                listeners: {
                    beforeedit: function (editor, context, eOpts) {
                        if (context.column.getEditor()) {
                            context.column.getEditor()._ownerRecord = context.record;
                        }
                    },
                    validateEdit: function (editor, context, eOpts) {
                        if (!context.column.getEditor().isValid()) {
                            return false;
                        }
                    }
                },
                tbar: tbar
            };
        },

        /**
         * Get form configuration
         *
         * @return {Object} form configuration
         */
        getFormConfig: function () {
            var view = this.getView(),
                vm = view.lookupViewModel(),
                theWidget = vm.get('theWidget'),
                model = Ext.ClassManager.get(view.getModelName()),
                mode = CMDBuildUI.util.helper.FormHelper.formmodes.update,
                formValidation = null,
                grouping;

            if (theWidget.get('ModelType').toLowerCase() === 'class') {
                var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(theWidget.get('ClassModel'));
                grouping = (item && item.attributeGroups().getRange()) || [];
                formValidation = item.get('validationRule');
            }

            if (view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                mode = CMDBuildUI.util.helper.FormHelper.formmodes.read;
            }

            // get form fields
            var items = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
                mode: mode,
                showAsFieldsets: true,
                linkName: 'theRow',
                filterLinkName: 'theTarget',
                grouping: grouping,
                formValidation: formValidation
            });

            return {
                xtype: 'form',
                modelValidation: true,
                autoScroll: true,
                fieldDefaults: {
                    labelAlign: 'top'
                },
                padding: theWidget.get('_inline') ? '0 0 0 25' : 0,
                items: items
            };
        },

        /**
         * Resolve variable.
         * @param {String} variable
         * @return {*} The variable resolved.
         */
        extractVariableFromString: function (variable, theTarget) {
            if (Ext.isString(variable) && CMDBuildUI.util.api.Client.testRegExp(/^{(client|server)+:*.+}$/, variable)) {
                variable = variable.replace('{', '').replace('}', '');
                var s_variable = variable.split(':'),
                    result;
                if (s_variable[0] === 'server') {
                    result = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
                    return result[s_variable[1]];
                } else if (s_variable[0] === 'client') {
                    result = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
                    return result[s_variable[1]];
                }
            } else {
                return variable;
            }
        }
    }
});
