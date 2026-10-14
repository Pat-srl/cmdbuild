Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.fieldsets.GroupingsOrdersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-dms-models-tabitems-properties-fieldsets-groupingsordersfieldset',
    controller: 'administration-content-dms-models-tabitems-properties-fieldsets-groupingsordersfieldset',

    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            bind: {
                title: '{groupingFieldsetTitle}'
            },
            collapsible: true,
            ui: 'administration-formpagination',
            collapsed: true,
            items: [
                {
                    columnWidth: 0.75,
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
                            selModel: {
                                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                            },
                            reference: 'groupingsGrid',
                            itemId: 'groupingsAttributesGrid',
                            bind: {
                                store: '{attributeGroupsStore}'
                            },
                            viewConfig: {
                                markDirty: false
                            },
                            plugins: {
                                ptype: 'actionColumnRowEditing',
                                id: 'actionColumnRowEditing',
                                hiddenColumnsOnEdit: [
                                    'actionColumnEdit',
                                    'actionColumnMoveUp',
                                    'actionColumnMoveDown',
                                    'actionColumnCancel',
                                    'actionLocalize'
                                ],
                                clicksToEdit: 10,
                                buttonsUi: 'button-like-tool',
                                errorSummary: false,
                                placeholdersButtons: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                        disabled: true,
                                        xtype: 'button',
                                        minWidth: 30,
                                        maxWidth: 30,
                                        ui: 'button-like-tool'
                                    },
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                        disabled: true,
                                        xtype: 'button',
                                        minWidth: 30,
                                        maxWidth: 30,
                                        ui: 'button-like-tool'
                                    },
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                        disabled: true,
                                        xtype: 'button',
                                        minWidth: 30,
                                        maxWidth: 30,
                                        ui: 'button-like-tool'
                                    }
                                ]
                            },

                            columns: [
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                                    },
                                    dataIndex: 'description',
                                    align: 'left',
                                    editor: {
                                        xtype: 'textfield',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo'
                                    },
                                    variableRowHeight: true
                                },
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode'
                                    },
                                    dataIndex: 'defaultDisplayMode',
                                    align: 'left',
                                    height: 19,
                                    editor: {
                                        xtype: 'combo',
                                        allowBlank: false,
                                        editable: false,
                                        displayField: 'label',
                                        valueField: 'value',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo',
                                        bind: {
                                            store: '{attriubteGroupingDisplayModeStore}'
                                        }
                                    },
                                    renderer: function (value) {
                                        var vm = this.lookupViewModel();
                                        var store = vm.get('attriubteGroupingDisplayModeStore');
                                        if (store) {
                                            var record = store.findRecord('value', value);
                                            if (record) {
                                                return record.get('label');
                                            }
                                        }
                                        return Ext.String.capitalize(value);
                                    },
                                    variableRowHeight: true
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnEdit',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    hidden: true,
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            handler: function (grid, rowIndex, colIndex, item, e, record) {
                                                grid.editingPlugin.startEdit(record, 0);
                                            },
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                if (record.get('editing')) {
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
                                    itemId: 'actionColumnMoveUp',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                                            handler: 'moveUp',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                if (!record.get('editing')) {
                                                    rowIndex =
                                                        rowIndex == null
                                                            ? view.getStore().findExact('id', record.get('id'))
                                                            : rowIndex;
                                                    return rowIndex == 0;
                                                } else {
                                                    return true;
                                                }
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                if (record.get('editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid');
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnMoveDown',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                                            handler: 'moveDown',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions
                                                    .movedown;
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                if (!record.get('editing')) {
                                                    rowIndex =
                                                        rowIndex == null
                                                            ? view.getStore().findExact('id', record.get('id'))
                                                            : rowIndex;
                                                    return rowIndex >= view.store.getCount() - 1;
                                                } else {
                                                    return true;
                                                }
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                if (record.get('editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'arrow-down',
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
                                            handler: 'deleteRow',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                if (!record.get('editing')) {
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                metadata.value = Ext.String.insert(
                                                    metadata.value,
                                                    Ext.String.format(
                                                        ' data-testid="administration-reordergrid-removeBtn-{0}"',
                                                        rowIndex
                                                    ),
                                                    -7
                                                );
                                                if (record.get('editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionLocalize',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.tooltips
                                                    .localize;
                                            },
                                            handler: function (grid, rowIndex, colIndex) {
                                                var me = this;
                                                var vm = grid.lookupViewModel();
                                                var translationCode =
                                                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassGroupDescription(
                                                        grid.lookupViewModel().get('objectTypeName'),
                                                        grid.getStore().getAt(rowIndex).get('name') || '.'
                                                    );
                                                CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                                                    translationCode,
                                                    CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                                                    Math.random()
                                                        .toString(36)
                                                        .replace(/[^a-z]+/g, '')
                                                        .substr(0, 10),
                                                    vm
                                                );
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                if (!record.get('editing')) {
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                metadata.value = Ext.String.insert(
                                                    metadata.value,
                                                    Ext.String.format(
                                                        ' data-testid="administration-reordergrid-removeBtn-{0}"',
                                                        rowIndex
                                                    ),
                                                    -7
                                                );
                                                if (record.get('editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid');
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'components-grid-reorder-grid',
                            margin: '20 0 20 0',
                            bind: {
                                store: '{attributeGroupsStoreNew}',
                                hidden: '{actions.view}'
                            },
                            viewConfig: {
                                markDirty: false
                            },
                            columns: [
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup'
                                    },
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    dataIndex: 'description',
                                    widget: {
                                        xtype: 'textfield',
                                        value: '',
                                        bind: {
                                            value: '{record.description}'
                                        }
                                    }
                                },
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.displaymode'
                                    },
                                    xtype: 'widgetcolumn',
                                    dataIndex: 'defaultDisplayMode',
                                    align: 'left',
                                    widget: {
                                        xtype: 'combo',
                                        allowBlank: false,
                                        editable: false,
                                        displayField: 'label',
                                        valueField: 'value',
                                        bind: {
                                            value: '{record.defaultDisplayMode}',
                                            store: '{attriubteGroupingDisplayModeStore}'
                                        }
                                    },
                                    variableRowHeight: true
                                },
                                {
                                    xtype: 'actioncolumn',
                                    minWidth: 150,
                                    maxWidth: 150,
                                    bind: {
                                        hidden: '{actions.biew}'
                                    },
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                            disabled: true
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                            disabled: true
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                            disabled: true
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                            disabled: true
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                if (Ext.String.trim(record.get('description')).length) {
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            },
                                            handler: 'onAddGroupClick',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.attributes.strings
                                                    .addnewgroup;
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
