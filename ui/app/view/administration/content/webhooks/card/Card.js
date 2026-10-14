Ext.define('CMDBuildUI.view.administration.content.webhooks.card.Card', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.webhooks.card.CardController',
        'CMDBuildUI.view.administration.content.webhooks.card.CardModel'
    ],
    alias: 'widget.administration-content-webhooks-card',
    controller: 'administration-content-webhooks-card',
    viewModel: {
        type: 'administration-content-webhooks-card'
    },
    layout: 'fit',

    bind: {
        userCls: '{formModelCls}'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    ui: 'administration-formpagination',

    items: [
        {
            xtype: 'container',
            autoScroll: true,
            scrollable: 'y',
            items: [
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    layout: 'column',
                    collapsible: false,
                    title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                                    {
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.code,
                                        localized: {
                                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
                                        },
                                        name: {
                                            allowBlank: false,
                                            bind: {
                                                value: '{theWebhook.code}',
                                                disabled: '{actions.edit}'
                                            }
                                        }
                                    },
                                    true,
                                    '[name="description"]'
                                ),

                                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                                    description: {
                                        allowBlank: false,
                                        bind: {
                                            value: '{theWebhook.description}'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                                    active: {
                                        fieldcontainer: {
                                            columnWidth: 0.5,
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.enabled,
                                            localized: {
                                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.enabled'
                                            }
                                        },
                                        bind: {
                                            disabled: '{actions.view}',
                                            value: '{theWebhook.active}'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                                    {
                                        target: {
                                            fieldcontainer: {
                                                fieldLabel: CMDBuildUI.locales.Locales.administration.bus.classprocess,
                                                localized: {
                                                    fieldLabel:
                                                        'CMDBuildUI.locales.Locales.administration.bus.classprocess'
                                                }
                                            },
                                            allowBlank: false,
                                            withClasses: true,
                                            withProcesses: true,
                                            bind: {
                                                store: '{getAllClassStore}',
                                                value: '{theWebhook.target}'
                                            }
                                        }
                                    },
                                    'target'
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('language', {
                                    language: {
                                        fieldcontainer: {
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
                                            }
                                        },
                                        emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                                        localized: {
                                            emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                                        },
                                        triggers: {
                                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                        },
                                        valueField: 'code',
                                        displayField: 'description',
                                        queryMode: 'local',
                                        typeAhead: true,
                                        bind: {
                                            store: '{languages}',
                                            value: '{theWebhook.language}'
                                        },
                                        displayfield: {
                                            bind: {
                                                value: '{theWebhook._language_description}'
                                            }
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            hidden: true,
                            bind: {
                                hidden: '{!theWebhook.target}'
                            },
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('event', {
                                    event: {
                                        fieldcontainer: {
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.bus.event,
                                            localized: {
                                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.event'
                                            },
                                            allowBlank: false,
                                            columnWidth: 0.25
                                        },
                                        allowBlank: false,
                                        forceSelection: true,
                                        bind: {
                                            value: '{theWebhook.event}',
                                            store: '{eventsStore}'
                                        }
                                    }
                                }),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('method', {
                                    method: {
                                        fieldcontainer: {
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.bus.method,
                                            localized: {
                                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.method'
                                            },
                                            allowBlank: false,
                                            columnWidth: 0.25
                                        },
                                        allowBlank: false,
                                        forceSelection: true,
                                        bind: {
                                            value: '{theWebhook.method}',
                                            store: '{methodsStore}'
                                        }
                                    }
                                }),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('url', {
                                    url: {
                                        fieldcontainer: {
                                            columnWidth: 0.5,
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                                            localized: {
                                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                                            }
                                        },
                                        vtype: 'url',
                                        allowBlank: false,
                                        bind: {
                                            value: '{theWebhook.url}'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            columnWidth: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.webhooks.headers,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.webhooks.headers'
                            },
                            items: [
                                {
                                    columnWidth: 1,
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
                                    reference: 'processAttributesGrid',
                                    itemId: 'processAttributesGrid',
                                    bind: {
                                        store: '{headersStore}'
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
                                        placeholdersButtons: []
                                    },
                                    listeners: {
                                        canceledit: function (grid) {
                                            var vm = grid.view.lookupViewModel();
                                            vm.set('gridEditing', false);
                                        },
                                        beforeedit: function (grid, context) {
                                            var vm = grid.view.lookupViewModel();
                                            if (vm.get('actions.view')) {
                                                return false;
                                            }
                                            vm.set('gridEditing', true);
                                        },
                                        edit: function (tableview, context) {
                                            var vm = tableview.view.lookupViewModel();
                                            vm.set('gridEditing', false);
                                        }
                                    },
                                    columns: [
                                        {
                                            flex: 1,
                                            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                            },
                                            dataIndex: 'key',
                                            align: 'left',
                                            editor: {
                                                xtype: 'textfield',
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo',
                                                bind: {
                                                    value: '{record.key}'
                                                }
                                            },
                                            variableRowHeight: true
                                        },
                                        {
                                            flex: 1,
                                            text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                            },
                                            dataIndex: 'value',
                                            align: 'left',
                                            editor: {
                                                xtype: 'textfield',
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo'
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
                                                    getTip: function (
                                                        value,
                                                        metadata,
                                                        record,
                                                        rowIndex,
                                                        colIndex,
                                                        store
                                                    ) {
                                                        return CMDBuildUI.locales.Locales.administration.common.actions
                                                            .edit;
                                                    },
                                                    getClass: function (
                                                        value,
                                                        metadata,
                                                        record,
                                                        rowIndex,
                                                        colIndex,
                                                        store
                                                    ) {
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
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'times',
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
                                                            .remove;
                                                    },
                                                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                                                        var vm = grid.lookupViewModel().getParent();
                                                        grid.getStore().remove(record);
                                                    },
                                                    isActionDisabled: function (
                                                        view,
                                                        rowIndex,
                                                        colIndex,
                                                        item,
                                                        record
                                                    ) {
                                                        return !record.get('editing') ? false : true;
                                                    },
                                                    getClass: function (
                                                        value,
                                                        metadata,
                                                        record,
                                                        rowIndex,
                                                        colIndex,
                                                        store
                                                    ) {
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
                                                        return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                            'times',
                                                            'solid'
                                                        );
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            columnWidth: 1,
                            fieldLabel: '',
                            bind: {
                                hidden: '{actions.view}'
                            },
                            margin: '20 0 20 0',
                            items: [
                                {
                                    columnWidth: 1,
                                    xtype: 'components-grid-reorder-grid',
                                    itemId: 'processAttributesGridForm',
                                    bind: {
                                        hidden: '{gridEditing}',
                                        store: '{headersNewStore}'
                                    },
                                    viewConfig: {
                                        markDirty: false
                                    },
                                    columns: [
                                        {
                                            flex: 1,
                                            text: '',
                                            xtype: 'widgetcolumn',
                                            align: 'left',
                                            dataIndex: 'key',
                                            widget: {
                                                xtype: 'textfield',
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo',
                                                bind: {
                                                    value: '{record.key}'
                                                }
                                            }
                                        },
                                        {
                                            flex: 1,
                                            text: '',
                                            xtype: 'widgetcolumn',
                                            align: 'left',
                                            dataIndex: 'value',
                                            widget: {
                                                xtype: 'textfield',
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo',
                                                bind: {
                                                    value: '{record.value}'
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'actioncolumn',
                                            width: 30,
                                            minWidth: 30, // width property not works. Use minWidth.
                                            maxWidth: 30,
                                            align: 'center',
                                            items: [
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'ellipsis-h',
                                                        'solid'
                                                    ),
                                                    disabled: true
                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'actioncolumn',
                                            width: 30,
                                            minWidth: 30, // width property not works. Use minWidth.
                                            maxWidth: 30,
                                            align: 'center',
                                            items: [
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
                                                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                                                        var formGridStore = grid.getStore();
                                                        var vm = grid.lookupViewModel();
                                                        var gridStore = vm.getStore('headersStore');
                                                        gridStore.add(record);
                                                        formGridStore.removeAll();
                                                        formGridStore.add(
                                                            CMDBuildUI.model.base.KeyDescriptionValue.create()
                                                        );
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            hidden: true,
                            bind: {
                                hidden: '{!theWebhook.method || theWebhook.method == "get" || theWebhook.method == "delete"}'
                            },
                            items: [
                                {
                                    columnWidth: 1,
                                    xtype: 'aceeditortextarea',
                                    allowBlank: true,
                                    vmObjectName: 'theWebhook',
                                    inputField: '_body',
                                    itemId: 'webhook-body',
                                    autoEl: {
                                        'data-testid': 'webhook-body'
                                    },
                                    options: {
                                        mode: 'ace/mode/json',
                                        readOnly: true
                                    },
                                    bind: {
                                        readOnly: '{actions.view}',
                                        config: {
                                            options: {
                                                readOnly: '{actions.view}'
                                            }
                                        }
                                    },
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.body'
                                    },
                                    minHeight: '385px',
                                    listeners: {
                                        render: function (element) {
                                            var aceEditor = element.getAceEditor();
                                            var vm = element.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        body: '{theWebhook.body}'
                                                    },
                                                    single: true
                                                },
                                                function (data) {
                                                    if (data.body) {
                                                        var value = JSON.stringify(data.body, null, '\t');
                                                        aceEditor.setValue(value, -1);
                                                    }
                                                }
                                            );
                                            vm.bind(
                                                {
                                                    isView: '{actions.view}'
                                                },
                                                function (data) {
                                                    aceEditor.setReadOnly(data.isView);
                                                }
                                            );
                                            aceEditor.getSession().on('change', function (event, _editor) {
                                                vm.set('theWebhook._body', _editor.getValue());
                                                try {
                                                    vm.set('theWebhook.body', Ext.JSON.decode(_editor.getValue()));
                                                } catch (error) {}
                                            });
                                        }
                                    },
                                    name: 'body',
                                    width: '95%'
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    dockedItems: [
        {
            dock: 'top',
            xtype: 'container',
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            },
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                        {
                            edit: true,
                            delete: true,
                            activeToggle: true,
                            clone: true
                        },
                        'webhooks',
                        'theWebhook'
                    )
                }
            ]
        },
        {
            xtype: 'toolbar',
            itemId: 'bottomtoolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(
                true,
                {},
                {
                    bind: {
                        hidden: '{!toolAction._canAdd || actions.edit}'
                    }
                }
            )
        }
    ]
});
