Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.fieldset.ParamsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-bus-descriptors-tabitems-properties-fieldsets-paramsfieldset',

    viewModel: {},
    ui: 'administration-formpagination',

    items: [
        {
            xtype: 'fieldset',

            title: CMDBuildUI.locales.Locales.administration.busmessages.params,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.busmessages.params'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'fieldcontainer',

                    items: [
                        {
                            xtype: 'grid',
                            headerBorders: false,
                            border: false,
                            bodyBorder: false,
                            rowLines: false,
                            sealedColumns: false,
                            sortableColumns: false,
                            enableColumnHide: false,
                            enableColumnMove: false,
                            enableColumnResize: false,
                            cls: 'administration-reorder-grid',
                            itemId: 'descriptorParamsGrid',
                            selModel: {
                                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                            },
                            viewConfig: {
                                markDirty: false
                            },
                            plugins: {
                                ptype: 'actionColumnRowEditing',
                                id: 'actionColumnRowEditing',
                                hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                                clicksToEdit: 10,
                                buttonsUi: 'button-like-tool',
                                errorSummary: false,
                                placeholdersButtons: [],
                                extraButtons: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('lock', 'solid'),
                                        xtype: 'button',
                                        ui: 'button-like-tool',
                                        minWidth: 30,
                                        maxWidth: 30,
                                        tooltip: CMDBuildUI.locales.Locales.administration.bus.insertencryptedvalue,
                                        localized: {
                                            tooltip:
                                                'CMDBuildUI.locales.Locales.administration.bus.insertencryptedvalue'
                                        },
                                        handler: function (button, rowIndex, colIndex) {
                                            CMDBuildUI.util.Msg.prompt(
                                                CMDBuildUI.locales.Locales.administration.bus.insertencryptedvalue,
                                                CMDBuildUI.locales.Locales.administration.bus.value,
                                                function (btn, text) {
                                                    if (btn == 'ok') {
                                                        var jsonData = {
                                                            value: text
                                                        };
                                                        Ext.Ajax.request({
                                                            url: Ext.String.format(
                                                                '{0}/utils/crypto/encrypt',
                                                                CMDBuildUI.util.Config.baseUrl
                                                            ),
                                                            method: 'POST',
                                                            jsonData: jsonData,
                                                            success: function (response) {
                                                                var res = Ext.JSON.decode(response.responseText);
                                                                if (res.success) {
                                                                    var grid = button.up('grid');
                                                                    grid.editingPlugin.context.record.set(
                                                                        'value',
                                                                        res.data.encrypted
                                                                    );
                                                                    grid.editingPlugin.editor.items.items[1].setValue(
                                                                        res.data.encrypted
                                                                    );
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            );
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return !record.get('_editing');
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                Ext.String.format(
                                                    ' data-testid="administration-content-bus-descriptors-params-grid-removeBtn-{0}"',
                                                    rowIndex
                                                ),
                                                -7
                                            );
                                            if (!record.get('_editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'ellipsis-h',
                                                    'solid'
                                                );
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('lock', 'solid');
                                        }
                                    }
                                ]
                            },
                            controller: {
                                control: {
                                    '#': {
                                        edit: function (editor, context, eOpts) {
                                            context.record.set('key', editor.editor.items.items[0].getValue());
                                            context.record.set('value', editor.editor.items.items[1].getValue());
                                            context.record.set('_editing', false);
                                        },
                                        beforeedit: function (editor, context, eOpts) {
                                            if (editor.view.lookupViewModel().get('actions.view')) {
                                                return false;
                                            }
                                            context.record.set('_editing', true);
                                            return true;
                                        },
                                        canceledit: function (editor, context) {
                                            if (context && context.record) {
                                                var previousKey = context.record.previousValues.key;
                                                var previousValue = context.record.previousValues.value;
                                                if (previousKey) {
                                                    context.record.set('key', previousKey);
                                                }
                                                if (previousValue) {
                                                    context.record.set('value', previousValue);
                                                }
                                                context.record.set('_editing', false);
                                            }
                                        }
                                    }
                                }
                            },
                            columnWidth: 1,
                            autoEl: {
                                'data-testid': 'administration-content-bus-descriptors-params-grid'
                            },

                            forceFit: true,
                            loadMask: true,

                            labelWidth: 'auto',
                            bind: {
                                store: '{paramsStore}'
                            },
                            columns: [
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.bus.key,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.bus.key'
                                    },
                                    dataIndex: 'key',
                                    align: 'left',
                                    editor: {
                                        xtype: 'textfield',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo'
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.bus.value,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.bus.value'
                                    },
                                    flex: 1,
                                    dataIndex: 'value',
                                    align: 'left',
                                    editor: {
                                        xtype: 'textfield',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo'
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnEdit',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            handler: function (grid, rowIndex, colIndex, item, e, record) {
                                                grid.editingPlugin.startEdit(record, 0);
                                            },
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                if (record.get('_editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'check',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'pencil-alt',
                                                    'solid'
                                                );
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnCancel',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                            },
                                            handler: function (grid, rowIndex, colIndex) {
                                                var vm = grid.lookupViewModel();
                                                var store = grid.getStore();
                                                var record = store.getAt(rowIndex);
                                                store.remove(record);
                                                grid.refresh();
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return !record.get('_editing') ? false : true;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                metadata.value = Ext.String.insert(
                                                    metadata.value,
                                                    Ext.String.format(
                                                        ' data-testid="administration-content-bus-descriptors-params-grid-removeBtn-{0}"',
                                                        rowIndex
                                                    ),
                                                    -7
                                                );
                                                if (record.get('_editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            margin: '20 0 20 0',
                            xtype: 'grid',
                            headerBorders: false,
                            border: false,
                            bodyBorder: false,
                            rowLines: false,
                            sealedColumns: false,
                            sortableColumns: false,
                            enableColumnHide: false,
                            enableColumnMove: false,
                            enableColumnResize: false,
                            cls: 'administration-reorder-grid',
                            itemId: 'descriptorParamsGridNew',
                            selModel: {
                                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                            },
                            //layout: 'hbox',
                            autoEl: {
                                'data-testid': 'administration-content-bus-descriptors-params-grid-new'
                            },

                            forceFit: true,
                            loadMask: true,

                            labelWidth: 'auto',
                            bind: {
                                store: '{newParamsStore}',
                                hidden: '{actions.view}'
                            },

                            columns: [
                                {
                                    xtype: 'widgetcolumn',
                                    dataIndex: 'key',
                                    align: 'left',
                                    flex: 1,
                                    widget: {
                                        xtype: 'textfield',
                                        itemId: 'newKey',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo',
                                        autoEl: {
                                            'data-testid': 'administration-content-bus-descriptors-params-grid-new-key'
                                        }
                                    }
                                },
                                {
                                    xtype: 'widgetcolumn',
                                    dataIndex: 'value',
                                    align: 'left',
                                    flex: 1,
                                    widget: {
                                        xtype: 'textfield',
                                        itemId: 'newValue',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo',
                                        autoEl: {
                                            'data-testid':
                                                'administration-content-bus-descriptors-params-grid-new-value'
                                        }
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnEditNew',
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                            disabled: true
                                        }
                                    ]
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnAddNew',
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-content-bus-descriptors-params-grid-new-addBtn'
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                metadata.value = Ext.String.insert(
                                                    metadata.value,
                                                    ' data-testid="administration-content-bus-descriptors-params-grid-addBtn"',
                                                    -7
                                                );
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid');
                                            },

                                            handler: function (grid, rowIndex, colIndex) {
                                                var vm = grid.lookupViewModel();
                                                var key = grid.up('grid').down('#newKey');
                                                var value = grid.up('grid').down('#newValue');
                                                Ext.suspendLayouts();
                                                var mainGrid = grid.up('form').down('#descriptorParamsGrid');
                                                var paramsStore = mainGrid.getStore();

                                                var newParam = CMDBuildUI.model.importexports.GateAttribute.create({
                                                    key: key.getValue(),
                                                    value: value.getValue()
                                                });

                                                paramsStore.add(newParam);
                                                value.reset();
                                                key.reset();
                                                value.allowBlank = true;
                                                Ext.resumeLayouts();
                                                mainGrid.getView().refresh();
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnEncrypt',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('lock', 'solid'),
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.bus
                                                    .insertencryptedvalue;
                                            },
                                            handler: function (grid, rowIndex, colIndex, btn, e, record) {
                                                CMDBuildUI.util.Msg.prompt(
                                                    CMDBuildUI.locales.Locales.administration.bus.insertencryptedvalue,
                                                    CMDBuildUI.locales.Locales.administration.bus.value,
                                                    function (btn, text) {
                                                        if (btn == 'ok') {
                                                            var jsonData = {
                                                                value: text
                                                            };
                                                            Ext.Ajax.request({
                                                                url: Ext.String.format(
                                                                    '{0}/utils/crypto/encrypt',
                                                                    CMDBuildUI.util.Config.baseUrl
                                                                ),
                                                                method: 'POST',
                                                                jsonData: jsonData,
                                                                success: function (response) {
                                                                    var res = Ext.JSON.decode(response.responseText);
                                                                    if (res.success) {
                                                                        var value = grid.up('grid').down('#newValue');
                                                                        value.setValue(res.data.encrypted);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                );
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return !record.get('_editing') ? false : true;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                metadata.value = Ext.String.insert(
                                                    metadata.value,
                                                    Ext.String.format(
                                                        ' data-testid="administration-content-bus-descriptors-params-grid-removeBtn-{0}"',
                                                        rowIndex
                                                    ),
                                                    -7
                                                );
                                                if (record.get('_editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('lock', 'solid');
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    columnWidth: 1,
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('disabled', {
                            disabled: {
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.bus.disabled,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.disabled'
                                    }
                                },
                                emptyText: CMDBuildUI.locales.Locales.administration.bus.disabledplaceholder,
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.administration.bus.disabledplaceholder'
                                },
                                bind: {
                                    disabled: '{actions.view}',
                                    value: '{theDescriptor.disabled}'
                                }
                            }
                        })
                    ]
                }
            ]
        }
    ]
});
