Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContextMenusFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset',

    viewModel: {},

    items: [
        {
            xtype: 'fieldset',
            collapsible: true,
            collapsed: true,
            layout: 'column',
            bind: {
                title: '{contextMenuTitle}'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    columnWidth: 1,
                    items: [
                        {
                            xtype: 'components-grid-reorder-grid',
                            bind: {
                                store: '{theObject.contextMenuItems}'
                            },
                            columnWidth: 0.5,
                            reference: 'contextMenuGrid',
                            flex: 1,
                            viewConfig: {
                                markDirty: false
                            },
                            columns: [
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets
                                        .contextMenus.inputs.menuItemName.label,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus.inputs.menuItemName.label'
                                    },
                                    width: '10%',
                                    align: 'left',
                                    dataIndex: 'label',
                                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                        switch (record.get('type')) {
                                            case 'separator':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.menuItemName.values.separator
                                                    .label;
                                            default:
                                                return record.get('label');
                                        }
                                    }
                                },
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets
                                        .contextMenus.inputs.typeOrGuiCustom.label,
                                    width: '15%',
                                    align: 'left',
                                    dataIndex: 'type',
                                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                        switch (value) {
                                            case 'custom':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.typeOrGuiCustom.values.custom
                                                    .label;
                                            case 'component':
                                                const vm = view.lookupViewModel();
                                                let customComponentStore, customComponent, customComponentDescription;
                                                if (record && record.get('componentId')) {
                                                    customComponentStore = vm.get('contextMenuComponentStore');
                                                    if (customComponentStore) {
                                                        customComponent = customComponentStore.findRecord(
                                                            'name',
                                                            record.get('componentId')
                                                        );
                                                    }
                                                }
                                                if (customComponent) {
                                                    customComponentDescription = customComponent.get('description');
                                                }

                                                return Ext.String.htmlDecode(
                                                    Ext.String.format(
                                                        '{0}<br>{1}',
                                                        CMDBuildUI.locales.Locales.administration.classes.texts
                                                            .component,
                                                        customComponentDescription || record.get('componentId')
                                                    )
                                                );
                                            case 'separator':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.typeOrGuiCustom.values.separator
                                                    .label; //'[---------]';
                                        }
                                    }
                                },
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets
                                        .contextMenus.inputs.javascriptScript.label,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus.inputs.javascriptScript.label'
                                    },
                                    width: '35%',
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    widget: {
                                        xtype: 'aceeditortextarea',
                                        inputField: 'script',
                                        vmObjectName: 'record',
                                        config: {
                                            options: {
                                                readOnly: '{!actions.edit}'
                                            }
                                        },
                                        bind: {
                                            hidden: '{record.type === "separator"}',
                                            value: '{record.script}'
                                        }
                                    },
                                    // called when the widget is initially instantiated
                                    // on the widget column
                                    onWidgetAttach: function (col, widget, rec) {
                                        if (rec.get('type') !== 'separator') {
                                            widget.aceEditor.setValue(
                                                widget.$widgetRecord.get(
                                                    rec.get('type') === 'component' ? 'config' : 'script'
                                                )
                                            );
                                            widget.aceEditor.moveCursorTo(0);
                                            // put the widget inside record for later use in controller
                                            // on edit btn click event. Needed for value change in widget.
                                            rec.widget = widget;
                                        } else {
                                            widget.setHidden(true);
                                        }
                                    }
                                },
                                {
                                    //Applicability
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets
                                        .contextMenus.inputs.applicability.label,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus.inputs.applicability.label'
                                    },
                                    width: '20%',
                                    align: 'left',
                                    dataIndex: 'visibility',
                                    // TODO: move to renderer helper
                                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                        switch (value) {
                                            case 'one':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.applicability.values.one.label;
                                            case 'many':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.applicability.values.many.label;
                                            case 'all':
                                                return CMDBuildUI.locales.Locales.administration.processes.properties
                                                    .form.fieldsets.contextMenus.inputs.applicability.values.all.label;
                                        }
                                    }
                                },
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.common.labels.status,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
                                    },
                                    width: '10%',
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    dataIndex: 'active',
                                    widget: {
                                        xtype: 'checkbox', // textfield | combo | radio
                                        bind: '{record.active}',
                                        boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                        localized: {
                                            boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                        },
                                        readOnly: true
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    minWidth: 150,
                                    maxWidth: 150,
                                    width: '10%',
                                    bind: {
                                        hidden: '{actions.view}'
                                    },
                                    align: 'center',
                                    items: [
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                                            handler: 'onEditBtn',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                                            handler: 'moveUp',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return rowIndex === 0;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                                            bind: {
                                                hidden: '{!actions.edit}'
                                            },
                                            handler: 'moveDown',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions
                                                    .movedown;
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return rowIndex >= view.store.getCount() - 1;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                            bind: {
                                                hidden: '{!actions.edit}',
                                                disabled: '{isLastDisabledAddButton}'
                                            },
                                            handler: 'deleteRow',

                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.delete;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.tooltips
                                                    .localize;
                                            },
                                            handler: function (grid, rowIndex, colIndex, item, event, record) {
                                                const vm = grid
                                                    .up('administration-content-classes-tabitems-properties-properties')
                                                    .lookupViewModel();
                                                const translationCode =
                                                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassContextMenuItem(
                                                        grid.lookupViewModel().get('objectTypeName'),
                                                        grid.getStore().getAt(rowIndex).get('label') || '.'
                                                    );
                                                const vmObject =
                                                    'theContextMenuTranslation_' +
                                                    CMDBuildUI.util.Utilities.stringToHex(
                                                        grid.getStore().getAt(rowIndex).get('label')
                                                    );
                                                CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                                                    translationCode,
                                                    CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                                                    vmObject,
                                                    vm,
                                                    true
                                                );
                                            },
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return record.get('type') === 'separator' ? true : false;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                if (record.get('editing')) {
                                                    return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    );
                                                }
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid');
                                            },
                                            margin: '0 10 0 10'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            // FORM
                            columnWidth: 1,

                            items: [
                                {
                                    xtype: 'label',
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.processes.strings
                                        .createnewcontextaction,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.processes.strings.createnewcontextaction'
                                    },
                                    margin: '0 0 10 0',
                                    style: {
                                        'font-weight': 'bold',
                                        'padding': '7px 10px 6px',
                                        'color': '#707070'
                                    },
                                    cls: 'cmdbuild-label-as-header',
                                    bind: {
                                        hidden: '{actions.view}'
                                    }
                                },
                                {
                                    margin: '7 0 0 0',
                                    viewConfig: {
                                        markDirty: false
                                    },
                                    xtype: 'components-grid-reorder-grid',
                                    bind: {
                                        store: '{contextMenuItemsStoreNew}',
                                        hidden: '{actions.view}',
                                        hideHeaders: '{theObject.name}'
                                    },
                                    flex: 1,

                                    columns: [
                                        {
                                            flex: 1,
                                            xtype: 'widgetcolumn',
                                            style: 'padding:0;',
                                            align: 'left',
                                            widget: {
                                                xtype: 'panel',
                                                align: 'left',
                                                bind: {
                                                    data: '{record}'
                                                },
                                                items: [
                                                    {
                                                        width: '100%',
                                                        itemId: 'contextMenuLabel',
                                                        xtype: 'textfield',
                                                        inputField: 'label',
                                                        bind: {
                                                            value: '{record.label}'
                                                        }
                                                    }
                                                ]
                                            }
                                        },
                                        {
                                            flex: 1,
                                            xtype: 'widgetcolumn',
                                            style: 'padding:0',
                                            widget: {
                                                xtype: 'panel',
                                                align: 'left',
                                                bind: {
                                                    data: '{record}'
                                                },
                                                dataIndex: 'type',
                                                items: [
                                                    {
                                                        xtype: 'combobox',

                                                        inputField: 'type',
                                                        width: '100%',
                                                        style: 'padding-top:0px',
                                                        editable: false,
                                                        forceSelection: true,
                                                        allowBlank: false,
                                                        displayField: 'label',
                                                        valueField: 'value',

                                                        bind: {
                                                            value: '{record.type}',
                                                            store: '{contextMenuItemTypeStore}'
                                                        },
                                                        listeners: {
                                                            select: function (ele, rec, idx) {
                                                                const isComponent = ele.getValue() === 'component';
                                                                this.up().getWidgetRecord().data._isComponent =
                                                                    isComponent;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        xtype: 'combobox',
                                                        inputField: 'componentId',
                                                        width: '100%',
                                                        editable: false,
                                                        queryMode: 'local',
                                                        forceSelection: true,
                                                        allowBlank: false,
                                                        displayField: 'description',
                                                        valueField: 'name',
                                                        hidden: true,
                                                        bind: {
                                                            value: '{record.componentId}',
                                                            store: '{contextMenuComponentStore}',
                                                            hidden: '{!record._isComponent}'
                                                        }
                                                    }
                                                ]
                                            }
                                        },
                                        {
                                            flex: 2,
                                            width: '35%',
                                            xtype: 'widgetcolumn',
                                            align: 'left',

                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            widget: {
                                                xtype: 'component',
                                                html: '<div id="newContextMenuScriptField" style="min-height:58px;height:100%;min-width:20px; width:100%"></div>',
                                                listeners: {
                                                    afterrender: function (cmp) {
                                                        const me = this;
                                                        const editor = (window.newContextMenuScriptField =
                                                            ace.edit('newContextMenuScriptField'));

                                                        //set the theme
                                                        //
                                                        editor.setTheme('ace/theme/chrome');

                                                        //set the mode
                                                        //
                                                        editor.getSession().setMode('ace/mode/javascript');

                                                        //set some options
                                                        //
                                                        editor.setOptions({
                                                            showLineNumbers: true,
                                                            showPrintMargin: false
                                                        });

                                                        //set a value
                                                        //
                                                        editor.setValue('');

                                                        editor.getSession().on('change', function (event, _editor) {
                                                            const vm = me
                                                                .up(
                                                                    'administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset'
                                                                )
                                                                .getViewModel()
                                                                .getParent();
                                                            vm.get('contextMenuItemsStoreNew')
                                                                .getData()
                                                                .items[0].set('script', _editor.getValue());
                                                        });
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            flex: 1,
                                            xtype: 'widgetcolumn',
                                            style: 'padding:0',
                                            widget: {
                                                xtype: 'panel',
                                                align: 'left',
                                                dataIndex: 'visibility',

                                                items: [
                                                    {
                                                        xtype: 'combobox',
                                                        inputField: 'visibility',
                                                        width: '100%',
                                                        style: 'padding-top:0px',
                                                        editable: false,
                                                        forceSelection: true,
                                                        allowBlank: false,
                                                        displayField: 'label',
                                                        valueField: 'value',
                                                        bind: {
                                                            store: '{contextMenuApplicabilityStore}',
                                                            value: '{record.visibility}'
                                                        }
                                                    }
                                                ]
                                            }
                                        },
                                        {
                                            flex: 1,
                                            width: '10%',
                                            xtype: 'widgetcolumn',
                                            align: 'left',
                                            dataIndex: 'active',
                                            widget: {
                                                xtype: 'checkbox',
                                                bind: '{record.active}',
                                                boxLabel:
                                                    CMDBuildUI.locales.Locales.administration.common.labels.active,
                                                localized: {
                                                    boxLabel:
                                                        'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                                }
                                            },
                                            onWidgetAttach: function (column, widget, record) {
                                                widget.setVisible(record.get('type') !== 'separator');
                                            }
                                        },
                                        {
                                            xtype: 'actioncolumn',
                                            minWidth: 150,
                                            maxWidth: 150,

                                            align: 'center',
                                            items: [
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    ),
                                                    disabled: true
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    ),
                                                    disabled: true
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    ),
                                                    disabled: true
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    ),
                                                    disabled: true
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'plus',
                                                        'solid'
                                                    ),
                                                    getTip: function (
                                                        value,
                                                        metadata,
                                                        record,
                                                        rowIndex,
                                                        colIndex,
                                                        store
                                                    ) {
                                                        return CMDBuildUI.locales.Locales.administration.common.actions
                                                            .add;
                                                    },
                                                    handler: 'onAddNewContextMenuBtn'
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
        }
    ]
});
