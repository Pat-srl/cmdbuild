Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.TriggersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-triggersfieldset',

    controller: 'administration-content-processes-tabitems-properties-fieldsets-triggersfieldset',

    viewModel: {},

    items: [
        {
            xtype: 'fieldset',
            collapsible: true,
            collapsed: true,
            layout: 'column',
            bind: {
                title: '{formTriggerTitle}'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    columnWidth: 1,
                    items: [
                        {
                            xtype: 'components-grid-reorder-grid',
                            bind: {
                                store: '{formTriggersStore}'
                            },
                            cls: 'administration-reorder-grid margin-bottom5',
                            columnWidth: 0.5,
                            reference: 'triggersGrid',
                            flex: 1,
                            columns: [
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.classes.properties.form.inputs
                                        .javascriptScript,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.inputs.javascriptScript'
                                    },
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    widget: {
                                        xtype: 'aceeditortextarea',
                                        config: {
                                            options: {
                                                readOnly: true
                                            }
                                        },
                                        inputField: 'script',
                                        vmObjectName: 'record',
                                        theMainObject: 'theTrigger',
                                        bind: {
                                            value: '{record.script}'
                                        }
                                    },
                                    // called when the widget is initially instantiated
                                    // on the widget column
                                    onWidgetAttach: function (col, widget, rec) {
                                        widget.aceEditor.setValue(widget.$widgetRecord.get('script'));
                                        widget.aceEditor.moveCursorTo(0);
                                        // put the widget inside record for later use in controller
                                        // on edit btn click event. Needed for value change in widget.
                                        rec.widget = widget;
                                    }
                                },
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.classes.properties.form.inputs
                                        .events,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.inputs.events'
                                    },
                                    align: 'left',
                                    cellWrap: true,
                                    renderer: function (value, meta, record) {
                                        return record.getSelectedTriggers();
                                    }
                                },
                                {
                                    flex: 1,
                                    text: CMDBuildUI.locales.Locales.administration.common.labels.status,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
                                    },
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    widget: {
                                        xtype: 'checkbox', // textfield | combo | radio
                                        bind: {
                                            value: '{record.active}',
                                            readOnly: '{!actions.edit}'
                                        },
                                        boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                        localized: {
                                            boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                        }
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    minWidth: 150,
                                    maxWidth: 150,
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
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return rowIndex === 0;
                                            },
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                                            handler: 'moveDown',
                                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                                return rowIndex >= view.store.getCount() - 1;
                                            },
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.actions
                                                    .movedown;
                                            }
                                        },
                                        {
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                            handler: 'deleteRow',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.common.tooltips.delete;
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'components-grid-reorder-grid',
                            bind: {
                                store: '{formTriggersStoreNew}',
                                hidden: '{actions.view}'
                            },
                            scrollable: 'y',
                            columnWidth: 0.5,
                            flex: 1,
                            columns: [
                                {
                                    flex: 2,
                                    text: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                        .formTriggers.inputs.createNewTrigger.label,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.createNewTrigger.label'
                                    },
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    cellWrap: true,
                                    variableRowHeight: true,
                                    widget: {
                                        xtype: 'component',
                                        html: '<div id="newTriggerScript" style="min-height:100px;height:100%;min-width:20px; width:100%"></div>',
                                        inputField: 'script',
                                        listeners: {
                                            afterrender: function (cmp) {
                                                Ext.asap(function () {
                                                    const me = this;
                                                    const editor = (window.newTriggerScript = ace.edit(
                                                        'newTriggerScript',
                                                        {
                                                            //set autoscroll
                                                            autoScrollEditorIntoView: true,
                                                            // set maxLines
                                                            maxLines: 30,
                                                            //set the mode
                                                            mode: 'ace/mode/javascript',
                                                            // set theme
                                                            theme: 'ace/theme/chrome',
                                                            // show line numbers
                                                            showLineNumbers: true,
                                                            // hide print margin
                                                            showPrintMargin: false
                                                        }
                                                    ));

                                                    editor.setValue('');
                                                    cmp.mon(cmp, 'resize', function () {
                                                        editor.renderer.$updateSizeAsync();
                                                    });
                                                    editor.getSession().on('change', function (event, _editor) {
                                                        editor.container.style.border = '';
                                                        delete editor.container.dataset.errorqtip;
                                                        const rows = _editor.getScreenLength();
                                                        if (rows > 8 && rows < editor.getOption('maxLines')) {
                                                            me.up().setHeight(rows * editor.getFontSize() + 25);
                                                        }

                                                        const vm = me
                                                            .up(
                                                                'administration-content-processes-tabitems-properties-properties'
                                                            )
                                                            .lookupViewModel();
                                                        vm.get('formTriggersStoreNew')
                                                            .getData()
                                                            .items[0].set(cmp.inputField, _editor.getValue());
                                                    });
                                                }, this);
                                            }
                                        }
                                    }
                                },
                                {
                                    flex: 2,
                                    text: '',
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    widget: {
                                        xtype: 'container',
                                        layout: 'column',
                                        defaults: {
                                            columnWidth: 0.5
                                        },
                                        items: [
                                            {
                                                xtype: 'checkboxgroup',
                                                userCls: 'hideCellCheboxes',
                                                columns: 1,
                                                vertical: true,
                                                items: [
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .beforeInsert.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeInsert.label'
                                                        },
                                                        recordKey: 'beforeInsert', // ✓
                                                        name: 'formTriggerBeforeInsert',
                                                        inputValue: true,
                                                        cls: 'checkbox1',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeInsert.label;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .beforeEdit.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeEdit.label'
                                                        },
                                                        recordKey: 'beforeEdit', // ✓
                                                        name: 'formTriggerBeforeEdit',
                                                        inputValue: true,
                                                        cls: 'checkbox4',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeEdit.label;
                                                            }
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'checkboxgroup',
                                                userCls: 'hideCellCheboxes',
                                                columns: 1,
                                                vertical: true,
                                                items: [
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .afterInsertSave.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertSave.label'
                                                        },
                                                        recordKey: 'afterInsert', // ✓
                                                        name: 'formTriggerAfterInsertSave',
                                                        inputValue: true,
                                                        cls: 'checkbox3',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertSave.label;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .afterInsertExecute.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertExecute.label'
                                                        },
                                                        recordKey: 'afterInsertExecute', // ✓
                                                        name: 'formTriggerAfterInsertExecute',
                                                        inputValue: true,
                                                        cls: 'checkbox3',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertExecute.label;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .afterEditSave.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditSave.label'
                                                        },
                                                        recordKey: 'afterEdit', // ✓
                                                        name: 'formTriggerAfterEditSave',
                                                        inputValue: true,
                                                        cls: 'checkbox6',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditSave.label;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .afterEditExecute.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditExecute.label'
                                                        },
                                                        recordKey: 'afterEditExecute', // ✓
                                                        name: 'formTriggerAfterEditExecute',
                                                        inputValue: true,
                                                        cls: 'checkbox6',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditExecute.label;
                                                            }
                                                        }
                                                    },
                                                    {
                                                        boxLabel:
                                                            CMDBuildUI.locales.Locales.administration.classes.properties
                                                                .form.fieldsets.formTriggers.inputs.events.values
                                                                .afterAbort.label,
                                                        localized: {
                                                            boxLabel:
                                                                'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterAbort.label'
                                                        },
                                                        recordKey: 'afterDelete',
                                                        name: 'formTriggerAfterDelete',
                                                        inputValue: true,
                                                        cls: 'checkbox9',
                                                        listeners: {
                                                            change: 'formTriggerCheckChange',
                                                            afterrender: function () {
                                                                var d = this.getEl();
                                                                d.dom.dataset.qtip =
                                                                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterAbort.label;
                                                            }
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                },
                                {
                                    flex: 1,
                                    text: '',
                                    xtype: 'widgetcolumn',
                                    align: 'left',
                                    dataIndex: 'active',
                                    widget: {
                                        xtype: 'checkbox', // textfield | combo | radio
                                        recordKey: 'active',
                                        name: 'formTriggerActive',
                                        boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                        localized: {
                                            boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                        },
                                        readOnly: false,
                                        listeners: {
                                            change: function (element, newValue, oldValue, eOpts) {
                                                element
                                                    .up(
                                                        'administration-content-processes-tabitems-properties-fieldsets-triggersfieldset'
                                                    )
                                                    .getViewModel()
                                                    .get('formTriggersStoreNew')
                                                    .getData()
                                                    .items[0].set(element.recordKey, element.checked);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    minWidth: 150,
                                    maxWidth: 150,
                                    bind: {
                                        hidden: '{actions.view}'
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
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                            handler: 'onAddNewTriggerBtn',
                                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                                return CMDBuildUI.locales.Locales.administration.classes.properties.form
                                                    .fieldsets.formTriggers.actions.addNewTrigger.tooltip;
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
