Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.Report', {
    extend: 'Ext.form.FieldSet',

    requires: ['CMDBuildUI.view.administration.content.emails.templates.card.attachments.ReportController'],

    alias: 'widget.administration-content-emails-templates-card-attachments-report',
    controller: 'administration-content-emails-templates-card-attachments-report',
    viewModel: {},
    title: CMDBuildUI.locales.Locales.administration.localizations.report,

    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.localizations.report'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    userCls: 'fieldset-fullwidth',
    ui: 'administration-formpagination',
    items: [
        {
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
            cls: 'administration-tool',
            style: 'position: absolute; right: 0;',
            itemId: 'removeNotificationTool',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            }
        },
        {
            xtype: 'container',
            columnWidth: 1,
            layout: 'column',
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('reportCode', {
                            reportCode: {
                                fieldcontainer: {}, // config for fieldcontainer
                                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.report, // the localized object for label of field
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.report'
                                },
                                displayField: 'description',
                                valueField: 'code',
                                bind: {
                                    store: '{reportsStore}',
                                    value: '{report.code}'
                                }
                            }
                        }),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('reportFormat', {
                            reportFormat: {
                                fieldcontainer: {}, // config for fieldcontainer
                                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format, // the localized object for label of field
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
                                },
                                displayField: 'label',
                                valueField: 'value',
                                bind: {
                                    store: '{reportsFormatStore}',
                                    value: '{report.format}'
                                }
                            }
                        })
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.parameter,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.parameter'
                            },
                            items: []
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.value,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                            },
                            items: []
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    columnWidth: 1,
                    items: [
                        {
                            xtype: 'grid',
                            width: '100%',
                            headerBorders: false,
                            border: false,
                            bodyBorder: false,
                            rowLines: false,
                            sealedColumns: false,
                            sortableColumns: false,
                            enableColumnHide: false,
                            enableColumnMove: false,
                            enableColumnResize: false,
                            cls: 'administration-reorder-grid reorder-grid-fullwidth',
                            itemId: 'reportParametersGrid',

                            selModel: {
                                pruneRemoved: false
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
                                errorSummary: false
                            },
                            controller: {
                                control: {
                                    '#': {
                                        edit: function (editor, context, eOpts) {
                                            context.record.set('value', editor.editor.items.items[1].getValue());
                                        },
                                        beforeedit: function (editor, context, eOpts) {
                                            if (editor.view.lookupViewModel().get('actions.view')) {
                                                return false;
                                            }
                                            context.record.previousValues = context.record.getData();
                                            return true;
                                        },
                                        canceledit: function (editor, context) {
                                            if (context && context.record) {
                                                var previousValue =
                                                    context.record.previousValues &&
                                                    context.record.previousValues.value;
                                                if (previousValue) {
                                                    context.record.set('value', previousValue);
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            autoEl: {
                                'data-testid': 'administration-content-importexport-datatemplates-grid'
                            },

                            forceFit: true,
                            loadMask: true,

                            labelWidth: 'auto',
                            bind: {
                                store: '{paramsStore}'
                            },
                            columns: [
                                {
                                    width: '52%',
                                    dataIndex: 'key',
                                    align: 'left',
                                    editor: {
                                        xtype: 'displayfield',
                                        height: 19,
                                        minHeight: 19,
                                        maxHeight: 19,
                                        padding: 0,
                                        ui: 'reordergrid-editor-combo'
                                    }
                                },
                                {
                                    width: '48%',
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
                                    itemId: 'actionColumnCancel',
                                    width: 30,
                                    minWidth: 30, // width property not works. Use minWidth.
                                    maxWidth: 30,
                                    align: 'center',
                                    items: []
                                },
                                {
                                    xtype: 'actioncolumn',
                                    itemId: 'actionColumnEdit',
                                    width: 30,
                                    minWidth: 30,
                                    maxWidth: 30,
                                    align: 'center',
                                    items: [
                                        {
                                            handler: function (grid, rowIndex, colIndex, item, e, record) {
                                                grid.editingPlugin.startEdit(record, 1);
                                            },
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                            },
                                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                                const actionsView = this.lookupViewModel().get('actions.view');
                                                if (actionsView) {
                                                    return 'x-hidden';
                                                } else {
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
