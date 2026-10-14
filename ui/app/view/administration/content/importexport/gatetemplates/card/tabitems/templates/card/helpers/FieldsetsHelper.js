Ext.define(
    'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper',
    {
        singleton: true,

        requires: ['CMDBuildUI.util.administration.helper.FormHelper', 'Ext.grid.plugin.DragDrop'],

        getGeneralPropertiesFieldset: function () {
            return {
                fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                layout: 'column',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                },

                items: [
                    // row
                    this.createContainer([
                        //input
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput(
                            {
                                code: {
                                    allowBlank: false,
                                    bind: {
                                        value: '{theGateTemplate.code}',
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
                                    value: '{theGateTemplate.description}'
                                }
                            }
                        })
                    ]),
                    this.createContainer([
                        //input
                        CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                            {
                                targetName: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations['class'],
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                                    },
                                    withStandardClasses: true,
                                    allowBlank: false,
                                    bind: {
                                        value: '{theGateTemplate.targetName}',
                                        disabled: '{actions.edit}'
                                    }
                                }
                            },
                            'targetName'
                        ),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('source', {
                            source: {
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gates.sourcelayer,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.sourcelayer'
                                    }
                                },
                                allowBlank: false,
                                bind: {
                                    value: '{theGateTemplate.source}'
                                }
                            }
                        })
                    ]),
                    this.createContainer([
                        //input
                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(
                            {
                                active: {
                                    bind: {
                                        value: '{theGateTemplate.active}'
                                    }
                                }
                            },
                            'active'
                        )
                    ])
                ]
            };
        },

        getAttributesFieldset: function () {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
                },
                items: [
                    {
                        xtype: 'grid',
                        viewModel: {},
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
                        itemId: 'importExportAttributeGrid',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        dockedItems: [
                            {
                                hidden: true,
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                xtype: 'toolbar',
                                dock: 'top',
                                border: 0,
                                items: [
                                    {
                                        xtype: 'tbfill'
                                    },
                                    {
                                        xtype: 'button',
                                        ui: 'button-like-tool',
                                        itemId: 'autogenarateBtn',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('magic', 'solid'),
                                        tooltip:
                                            CMDBuildUI.locales.Locales.administration.importexport.texts
                                                .addallattributes,
                                        localized: {
                                            tooltip:
                                                'CMDBuildUI.locales.Locales.administration.importexport.texts.addallattributes'
                                        },
                                        autoEl: {
                                            'data-testid': 'administration-importexpport-addAllFreeAttributesBtn'
                                        }
                                    }
                                ]
                            }
                        ],
                        plugins: {
                            ptype: 'actionColumnRowEditing',
                            id: 'actionColumnRowEditing',
                            hiddenColumnsOnEdit: [
                                'actionColumnEdit',
                                'actionColumnMoveUp',
                                'actionColumnMoveDown',
                                'actionColumnCancel'
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
                                }
                            ]
                        },
                        controller: {
                            control: {
                                '#': {
                                    edit: function (editor, context, eOpts) {
                                        context.record.set('columnName', editor.editor.items.items[1].getValue());
                                        context.record.set('mode', editor.editor.items.items[2].getValue());
                                        context.record.set('default', editor.editor.items.items[3].getValue());
                                    },
                                    beforeedit: function (editor, context, eOpts) {
                                        if (editor.view.lookupViewModel().get('actions.view')) {
                                            return false;
                                        }
                                        var vm = editor.view.lookupViewModel();
                                        var comboStore = vm.get('attributeModesAllStore');
                                        comboStore.clearFilter();

                                        var allAttributesStore = editor.view
                                            .lookupViewModel()
                                            .get('allAttributesStore');
                                        var attribute = allAttributesStore.findRecord(
                                            'name',
                                            context.record.get('attribute')
                                        );
                                        var columnNameInput = editor.editor.items.items[1];
                                        var modeInput = editor.editor.items.items[2];
                                        modeInput.forceSelection = false;
                                        if (attribute) {
                                            if (context.record.get('columnName') === 'CM_RELATIVE_LOCATION') {
                                                columnNameInput.setDisabled(true);
                                                modeInput.setStore(vm.get('attributeModesReferenceStore'));
                                                modeInput.setHideTrigger(false);
                                                modeInput.setDisabled(false);
                                            } else {
                                                columnNameInput.setDisabled(false);
                                                if (
                                                    ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(
                                                        attribute.get('_subtype')
                                                    ) > -1 ||
                                                    attribute.get('name') === 'IdTenant'
                                                ) {
                                                    if (!Ext.isEmpty(attribute.get('targetClass'))) {
                                                        CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                            attribute.get('targetClass')
                                                        )
                                                            .getGeoAttributes(true)
                                                            .then(function (geoAttributesStore) {
                                                                if (
                                                                    geoAttributesStore.findBy(function (item) {
                                                                        return item.get('subtype') === 'polygon';
                                                                    }) > -1
                                                                ) {
                                                                    modeInput.setStore(
                                                                        vm.get('attributeModesReferenceStore')
                                                                    );
                                                                } else {
                                                                    modeInput.setStore(
                                                                        vm.get('attributeModesReferenceLightStore')
                                                                    );
                                                                }
                                                            });

                                                        CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                            attribute.get('targetClass')
                                                        )
                                                            .getGeoAttributes(true)
                                                            .then(function (geoAttributesStore) {
                                                                if (
                                                                    geoAttributesStore.findBy(function (item) {
                                                                        return item.get('subtype') === 'polygon';
                                                                    }) > -1
                                                                ) {
                                                                    modeInput.setStore(
                                                                        vm.get('attributeModesReferenceStore')
                                                                    );
                                                                } else {
                                                                    modeInput.setStore(
                                                                        vm.get('attributeModesReferenceLightStore')
                                                                    );
                                                                }
                                                                modeInput.style =
                                                                    'color:#57585C!important; border: 1px solid #cf4c35';
                                                                modeInput.setHideTrigger(false);
                                                                modeInput.setEmptyText(
                                                                    Ext.String.format(
                                                                        '{0} *',
                                                                        CMDBuildUI.locales.Locales.administration
                                                                            .importexport.texts.selectmode
                                                                    )
                                                                );
                                                                modeInput.setDisabled(false);
                                                                modeInput.forceSelection = true;
                                                            });
                                                    } else if (attribute.get('name') === 'IdTenant') {
                                                        modeInput.setStore(vm.get('attributeModesReferenceLightStore'));
                                                        Ext.asap(function () {
                                                            modeInput.forceSelection = true;
                                                            modeInput.setHideTrigger(false);
                                                            modeInput.setDisabled(false);
                                                            modeInput.enable();
                                                        });
                                                    } else {
                                                        modeInput.setDisabled(false);
                                                        modeInput.setHideTrigger(false);
                                                        modeInput.setStore(vm.get('attributeModesLookupStore'));
                                                        Ext.asap(function () {
                                                            modeInput.allowBlank = false;
                                                            modeInput.forceSelection = true;
                                                            modeInput.isValid();
                                                        });
                                                    }
                                                } else {
                                                    modeInput.allowBlank = true;
                                                    modeInput.style = 'border: 0px solid!important';
                                                    modeInput.setHideTrigger(true);
                                                    modeInput.setEmptyText('');
                                                    modeInput.disable();
                                                }
                                            }
                                        }
                                        return true;
                                    },
                                    canceledit: function (editor, context) {
                                        if (context && context.record) {
                                            var previousColumnName = context.record.previousValues.columnName;
                                            var previousMode = context.record.previousValues.mode;
                                            if (previousColumnName) {
                                                context.record.set('columnName', previousColumnName);
                                            }
                                            if (previousMode) {
                                                context.record.set('mode', previousMode);
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        columnWidth: 1,
                        autoEl: {
                            'data-testid': 'administration-content-importexport-datatemplates-grid'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{theGateTemplate.columns}',
                            hidden: '{isAttributeGridHidden}'
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.attributes.attribute'
                                },
                                dataIndex: '_attribute_description',
                                align: 'left',
                                editor: {
                                    xtype: 'displayfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                },
                                renderer: function (value, cell, record) {
                                    var vm = this.lookupViewModel();
                                    function recoveryDescription(_vm, _value, _record) {
                                        _vm.bind(
                                            {
                                                bindTo: {
                                                    rawAttributeStore: '{allAttributesStore}'
                                                },
                                                single: true
                                            },
                                            function (data) {
                                                if (!_value) {
                                                    var attribute = data.rawAttributeStore.findRecord(
                                                        'name',
                                                        _record.get('attribute')
                                                    );
                                                    if (attribute) {
                                                        _record.set(
                                                            '_attribute_description',
                                                            attribute.get('description')
                                                        );
                                                    } else {
                                                        _record.set('_attribute_description', _record.get('attribute'));
                                                    }
                                                }
                                            }
                                        );
                                    }
                                    if (!value) {
                                        recoveryDescription(vm, value, record);
                                    }
                                    return value;
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.gates.dwgproperty,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.gates.dwgproperty'
                                },
                                flex: 1,
                                dataIndex: 'columnName',
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
                                text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.importexport.texts.mode'
                                },
                                flex: 1,
                                dataIndex: 'mode',
                                align: 'left',
                                editor: {
                                    xtype: 'combo',
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode,
                                    localized: {
                                        emptyText:
                                            'CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode'
                                    },
                                    itemId: 'modeCombo',
                                    editable: false,
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    allowBlank: false,
                                    bind: {
                                        store: '{attributeModesAllStore}'
                                    },
                                    listeners: {
                                        beforerender: function (combo) {
                                            var grid = this.up('grid');
                                            var vm = grid.lookupViewModel();
                                            var record = grid.editingPlugin.context.record;
                                            var attribute;
                                            var allAttributes = vm.get('allAttributesStore');
                                            if (allAttributes && allAttributes.getData().length) {
                                                attribute = allAttributes.findRecord('name', record.get('attribute'));
                                            }
                                            if (
                                                (attribute &&
                                                    ['lookup', 'lookupArray', 'reference'].indexOf(
                                                        attribute.get('_subtype')
                                                    ) > -1) ||
                                                record.get('columnName') === 'CM_RELATIVE_LOCATION'
                                            ) {
                                                combo.setDisabled(false);
                                            } else {
                                                combo.setDisabled(true);
                                            }

                                            if (
                                                attribute.get('_subtype') === 'reference' ||
                                                record.get('columnName') === 'CM_RELATIVE_LOCATION'
                                            ) {
                                                if (!Ext.isEmpty(attribute.get('targetClass'))) {
                                                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                        attribute.get('targetClass')
                                                    )
                                                        .getGeoAttributes(true)
                                                        .then(function (geoAttributesStore) {
                                                            if (
                                                                geoAttributesStore.findBy(function (item) {
                                                                    return item.get('subtype') === 'polygon';
                                                                }) > -1
                                                            ) {
                                                                combo.setStore(vm.get('attributeModesReferenceStore'));
                                                            } else {
                                                                combo.setStore(
                                                                    vm.get('attributeModesReferenceLightStore')
                                                                );
                                                            }
                                                        });
                                                } else {
                                                    combo.setStore(vm.get('attributeModesReferenceLightStore'));
                                                }
                                            } else if (
                                                attribute &&
                                                (['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1 ||
                                                    ['reference', 'foreignKey'].indexOf(attribute.get('_subtype')) >
                                                        -1 ||
                                                    attribute.get('_id') === 'IdTenant')
                                            ) {
                                                combo.setStore(vm.get('attributeModesReferenceLightStore'));
                                            } else if (
                                                attribute &&
                                                ['lookup', 'lookupArray'].indexOf(attribute.get('_subtype')) > -1
                                            ) {
                                                combo.setStore(vm.get('attributeModesLookupStore'));
                                            }
                                        },
                                        change: function (combo, newValue, oldValue) {
                                            var grid = this.up('grid');
                                            var record = grid.editingPlugin.context.record;
                                            var columnNameField = grid.editingPlugin.editor.getEditor(1);
                                            if (
                                                newValue ===
                                                CMDBuildUI.model.importexports.GateAttribute.relativelocation
                                            ) {
                                                record.set('columnName', 'CM_RELATIVE_LOCATION');
                                                columnNameField.setValue('CM_RELATIVE_LOCATION');
                                                columnNameField.setDisabled(true);
                                            } else if (
                                                oldValue ===
                                                CMDBuildUI.model.importexports.GateAttribute.relativelocation
                                            ) {
                                                columnNameField.setDisabled(false);
                                                columnNameField.allowBlank = false;
                                                columnNameField.setValue(null);
                                                record.set('columnName', '');
                                            }
                                        }
                                    }
                                },
                                renderer: function (value, cell, record, rowIndex, colIndex, store, grid) {
                                    if (!value || value === 'default') {
                                        return CMDBuildUI.locales.Locales.administration.common.labels.default;
                                    }
                                    var vm = this.up('panel').lookupViewModel();
                                    var attribute;
                                    var allAttributes = vm.get('allAttributesStore');
                                    if (allAttributes && allAttributes.getData().length) {
                                        attribute = allAttributes.findRecord('name', record.get('attribute'));
                                        vm.manageDataFormatFieldset(attribute);
                                    }
                                    if (
                                        !value &&
                                        attribute &&
                                        ['lookup', 'lookupArray', 'reference'].indexOf(attribute.get('_subtype')) > -1
                                    ) {
                                        return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
                                    }
                                    var mode = Ext.Array.findBy(
                                        Ext.Array.merge(vm.get('attributeModes'), vm.get('attributeModesLookup')),
                                        function (element) {
                                            return element.value === value;
                                        }
                                    );
                                    return (
                                        (mode && mode.label) ||
                                        CMDBuildUI.locales.Locales.administration.common.labels.default
                                    );
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.importexport.texts.defaultvalue,
                                flex: 1,
                                dataIndex: 'default',
                                align: 'left',
                                editor: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    ui: 'reordergrid-editor-combo',
                                    bind: {
                                        disabled: '{!isImport}'
                                    }
                                },
                                renderer: Ext.util.Format.htmlEncode,
                                bind: {
                                    disabled: '{!isImport}'
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
                                        handler: function (cell, rowIndex, colIndex, item, e, record) {
                                            cell.editingPlugin.startEdit(record, 1);
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
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
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                                        },
                                        handler: function (grid, rowIndex, colIndex) {
                                            Ext.suspendLayouts();
                                            var store = grid.getStore();

                                            var record = store.getAt(rowIndex);
                                            var previousRecord = store.getAt(rowIndex - 1);
                                            rowIndex--;
                                            if (!record || rowIndex < 0) {
                                                return;
                                            }
                                            previousRecord.set('index', rowIndex + 1);
                                            record.set('index', rowIndex);

                                            try {
                                                store.sort('index', 'ASC');
                                                grid.lookupViewModel().getParent().filterFreeAttributes();
                                                grid.refresh();
                                                Ext.resumeLayouts();
                                            } catch (e) {}
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
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                                        },
                                        handler: function (grid, rowIndex, colIndex) {
                                            Ext.suspendLayouts();
                                            var store = grid.getStore();

                                            var record = store.getAt(rowIndex);
                                            var previousRecord = store.getAt(rowIndex + 1);

                                            rowIndex++;
                                            if (!record || rowIndex >= store.getCount()) {
                                                return;
                                            }

                                            previousRecord.set('index', rowIndex - 1);
                                            record.set('index', rowIndex);
                                            try {
                                                store.sort('index', 'ASC');
                                                grid.lookupViewModel().getParent().filterFreeAttributes();
                                                grid.refresh();
                                                Ext.resumeLayouts();
                                            } catch (e) {}
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
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid');
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
                                            var vm = grid.up('form').lookupViewModel();
                                            var store = grid.getStore();
                                            var record = store.getAt(rowIndex);
                                            store.remove(record);
                                            var importKeyAttributes = vm.get('theGateTemplate._importKeyAttribute');
                                            if (importKeyAttributes.length) {
                                                vm.set(
                                                    'theGateTemplate._importKeyAttribute',
                                                    Ext.Array.remove(importKeyAttributes, record.get('attribute'))
                                                );
                                            }
                                            vm.filterFreeAttributes();
                                            grid.refresh();
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return !record.get('editing') ? false : true;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                Ext.String.format(
                                                    ' data-testid="administration-importexport-attribute-removeBtn-{0}"',
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
                        itemId: 'importExportAttributeGridNew',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        layout: 'hbox',
                        autoEl: {
                            'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{newSelectedAttributesStore}',
                            hidden: '{actions.view}'
                        },

                        columns: [
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'attribute',
                                align: 'left',
                                flex: 1,
                                widget: {
                                    xtype: 'groupedcombo',
                                    queryMode: 'local',
                                    typeAhead: true,
                                    editable: true,
                                    forceSelection: true,
                                    emptyText:
                                        CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                                    localized: {
                                        emptyText:
                                            'CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute'
                                    },
                                    itemId: 'selectAttributeForGrid',
                                    displayField: 'description',
                                    valueField: 'name',
                                    bind: {
                                        store: '{freeAttributesStore}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-importexport-attribute-name'
                                    },
                                    listeners: {
                                        change: function (combo, newValue, oldValue) {
                                            var grid = combo.up('grid');
                                            var modeInput = combo.up('grid').down('#newComboMode');
                                            var columnNameInput = combo.up('grid').down('#newAttributeColumnName');
                                            columnNameInput.setDisabled(false);
                                            modeInput.clearValue();
                                            var allAttributes = grid
                                                .up('form')
                                                .getViewModel()
                                                .get('allAttributesStore');

                                            if (allAttributes && allAttributes.getData().length) {
                                                var attribute = allAttributes.findRecord('name', newValue);
                                                if (attribute) {
                                                    columnNameInput.setValue(attribute.get('description'));
                                                    modeInput.setInputPresets(attribute);
                                                } else {
                                                    modeInput.setInputPresets();
                                                }
                                            } else {
                                                modeInput.setInputPresets();
                                            }
                                        }
                                    },
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'columnName',
                                align: 'left',
                                flex: 1,
                                widget: {
                                    xtype: 'textfield',
                                    itemId: 'newAttributeColumnName',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    autoEl: {
                                        'data-testid': 'administration-importexport-attribute-columnname'
                                    }
                                }
                            },
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'mode',
                                align: 'left',
                                flex: 1,

                                widget: {
                                    xtype: 'combo',
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    itemId: 'newComboMode',
                                    typeAhead: true,
                                    editable: true,
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    hideTrigger: true,
                                    forceSelection: true,
                                    allowBlank: false,
                                    disabled: true,
                                    disabledCls: 'x-item-disabled',
                                    autoEl: {
                                        'data-testid': 'administration-importexport-attribute-mode'
                                    },
                                    setInputPresets: function (attribute) {
                                        var modeInput = this;
                                        var vm = modeInput.lookupViewModel();
                                        if (!attribute) {
                                            modeInput.allowBlank = true;
                                            modeInput.style = 'border: 0px solid!important';
                                            modeInput.setHideTrigger(true);
                                            modeInput.setEmptyText('');
                                            modeInput.disable();
                                            return;
                                        }
                                        if (
                                            ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(
                                                attribute.get('_subtype')
                                            ) > -1 ||
                                            attribute.get('name') === 'IdTenant'
                                        ) {
                                            if (attribute.get('_subtype') === 'reference') {
                                                if (!Ext.isEmpty(attribute.get('targetClass'))) {
                                                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                        attribute.get('targetClass')
                                                    )
                                                        .getGeoAttributes(true)
                                                        .then(function (geoAttributesStore) {
                                                            if (
                                                                geoAttributesStore.findBy(function (item) {
                                                                    return item.get('subtype') === 'polygon';
                                                                }) > -1
                                                            ) {
                                                                modeInput.setStore(
                                                                    vm.get('attributeModesReferenceStore')
                                                                );
                                                            } else {
                                                                modeInput.setStore(
                                                                    vm.get('attributeModesReferenceLightStore')
                                                                );
                                                            }
                                                            modeInput.style =
                                                                'color:#57585C!important; border: 1px solid #cf4c35';
                                                            modeInput.allowBlank = false;
                                                            modeInput.clearValue();
                                                            modeInput.markInvalid('Required');
                                                            modeInput.setHideTrigger(false);
                                                            modeInput.setEmptyText(
                                                                Ext.String.format(
                                                                    '{0} *',
                                                                    CMDBuildUI.locales.Locales.administration
                                                                        .importexport.texts.selectmode
                                                                )
                                                            );
                                                            modeInput.enable();
                                                        });
                                                } else {
                                                    modeInput.allowBlank = true;
                                                    modeInput.style = 'border: 0px solid!important';
                                                    modeInput.setHideTrigger(true);
                                                    modeInput.setEmptyText('');
                                                    modeInput.disable();
                                                }
                                            } else {
                                                modeInput.setStore(vm.get('attributeModesLookupStore'));
                                                modeInput.style = 'color:#57585C!important; border: 1px solid #cf4c35';
                                                modeInput.allowBlank = false;
                                                modeInput.clearValue();
                                                modeInput.markInvalid('Required');
                                                modeInput.setHideTrigger(false);
                                                modeInput.setEmptyText(
                                                    Ext.String.format(
                                                        '{0} *',
                                                        CMDBuildUI.locales.Locales.administration.importexport.texts
                                                            .selectmode
                                                    )
                                                );
                                                modeInput.enable();
                                            }
                                        } else {
                                            modeInput.allowBlank = true;
                                            modeInput.style = 'border: 0px solid!important';
                                            modeInput.setHideTrigger(true);
                                            modeInput.setEmptyText('');
                                            modeInput.disable();
                                        }
                                    },
                                    listeners: {
                                        change: function (combo, newValue, oldValue) {
                                            var vm = combo.lookupViewModel();
                                            var columnNameInput = combo.up('grid').down('#newAttributeColumnName');
                                            var attributeInput = combo.up('grid').down('#selectAttributeForGrid');
                                            var allAttributes = vm.get('allAttributesStore');
                                            var attribute = allAttributes.findRecord('name', attributeInput.getValue());
                                            if (attribute) {
                                                if (
                                                    attribute.get('_subtype') === 'reference' &&
                                                    newValue ===
                                                        CMDBuildUI.model.importexports.GateAttribute.relativelocation &&
                                                    allAttributes &&
                                                    allAttributes.getData().length
                                                ) {
                                                    columnNameInput.setValue('CM_RELATIVE_LOCATION');
                                                    columnNameInput.setDisabled(true);
                                                } else {
                                                    columnNameInput.style =
                                                        'color:#57585C!important; border: 1px solid #cf4c35';
                                                    if (columnNameInput.getValue() === 'CM_RELATIVE_LOCATION') {
                                                        columnNameInput.setValue('');
                                                    }
                                                    columnNameInput.allowBlank = false;
                                                    columnNameInput.setDisabled(false);
                                                    columnNameInput.setEmptyText('');
                                                    columnNameInput.focus();
                                                    Ext.asap(function () {
                                                        columnNameInput.blur();
                                                    });
                                                }
                                            } else {
                                                columnNameInput.allowBlank = true;
                                                columnNameInput.style = 'border: 0px solid!important';
                                                columnNameInput.setValue('');
                                                columnNameInput.setDisabled(false);
                                                columnNameInput.setEmptyText('');
                                            }
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'default',
                                align: 'left',
                                flex: 1,

                                widget: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    itemId: 'newAttributeDefaultValue',
                                    ui: 'reordergrid-editor-combo',
                                    bind: {
                                        disabled: '{!isImport}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-importexport-attribute-defaultvalue'
                                    }
                                },
                                renderer: Ext.util.Format.htmlEncode,
                                bind: {
                                    disabled: '{!isImport}'
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
                                itemId: 'actionColumnMoveUpNew',
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
                                itemId: 'actionColumnMoveDownNew',
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
                                            'data-testid': 'administration-importexport-attribute-addBtn'
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                ' data-testid="administration-importexport-attribute-addBtn"',
                                                -7
                                            );
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid');
                                        },

                                        handler: function (grid, rowIndex, colIndex) {
                                            var vm = grid.lookupViewModel();
                                            var attributeName = grid.up('grid').down('#selectAttributeForGrid');
                                            var columnName = grid.up('grid').down('#newAttributeColumnName');
                                            var attributeMode = grid.up('grid').down('#newComboMode');
                                            var defaultValue = grid.up('grid').down('#newAttributeDefaultValue');
                                            if (Ext.isEmpty(attributeName.getValue())) {
                                                attributeName.focus();
                                                attributeName.expand();
                                                return false;
                                            }
                                            if (Ext.isEmpty(columnName.getValue())) {
                                                columnName.focus();
                                                return false;
                                            }
                                            if (!attributeMode.isValid()) {
                                                attributeMode.focus();
                                                attributeMode.expand();
                                                return false;
                                            }
                                            Ext.suspendLayouts();
                                            var mainGrid = grid.up('form').down('#importExportAttributeGrid');
                                            var attributeStore = mainGrid.getStore();

                                            var newAttribute = CMDBuildUI.model.importexports.GateAttribute.create({
                                                attribute: attributeName.getValue(),
                                                columnName: columnName.getValue(),
                                                mode: attributeMode.getValue(),
                                                default: defaultValue.getValue(),
                                                index: attributeStore.getRange().length
                                            });

                                            attributeStore.add(newAttribute);
                                            attributeName.clearValue();
                                            columnName.reset();
                                            attributeMode.clearValue();
                                            attributeMode.allowBlank = true;
                                            defaultValue.reset();
                                            Ext.resumeLayouts();
                                            mainGrid.getView().refresh();
                                            vm.filterFreeAttributes();
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    this.getDataFormatFieldset()
                ]
            };
        },
        getDataFormatFieldset: function () {
            return {
                title: CMDBuildUI.locales.Locales.administration.importexport.texts.dataformat,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.dataformat'
                },
                fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                layout: 'column',
                collapsible: true,
                hidden: true,
                bind: {
                    hidden: '{dataFormatHidden}'
                },
                items: [
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 1,
                        items: [
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('dateFormat', {
                                dateFormat: {
                                    fieldcontainer: {
                                        hidden: true,
                                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                                        localized: {
                                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
                                        },
                                        bind: {
                                            hidden: '{dataFormatDateHidden}'
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theGateTemplate.dateFormat}'
                                        },
                                        renderer: function (value) {
                                            var store = this.lookupViewModel().get('dateFormats');
                                            if (store) {
                                                var record = store.findRecord('value', value);
                                                value = record ? record.get('label') : value;
                                            }
                                            return value
                                                ? value
                                                : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                        }
                                    },
                                    combofield: {
                                        emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                        localized: {
                                            emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                        },
                                        forceSelection: true,
                                        autoSelect: false,
                                        triggers: {
                                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                        },
                                        bind: {
                                            store: '{dateFormats}',
                                            value: '{theGateTemplate.dateFormat}'
                                        }
                                    }
                                }
                            }),
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('timeFormat', {
                                timeFormat: {
                                    fieldcontainer: {
                                        hidden: true,
                                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                                        localized: {
                                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
                                        },
                                        bind: {
                                            hidden: '{dataFormatTimeHidden}'
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theGateTemplate.timeFormat}'
                                        },
                                        renderer: function (value) {
                                            var store = this.lookupViewModel().get('timeFormats');
                                            if (store) {
                                                var record = store.findRecord('value', value);
                                                value = record ? record.get('label') : value;
                                            }
                                            return value
                                                ? value
                                                : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                        }
                                    },
                                    combofield: {
                                        emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                        localized: {
                                            emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                        },
                                        forceSelection: true,
                                        autoSelect: false,
                                        triggers: {
                                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                        },
                                        bind: {
                                            store: '{timeFormats}',
                                            value: '{theGateTemplate.timeFormat}'
                                        }
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
                                xtype: 'displayfield',
                                hidden: true,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat'
                                },
                                bind: {
                                    hidden: '{dataFormatDateTimeHidden}',
                                    value: '{dateTimeFormat}'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 1,
                        items: [
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('decimalSeparator', {
                                decimalSeparator: {
                                    fieldcontainer: {
                                        hidden: true,
                                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                                        },
                                        bind: {
                                            hidden: '{dataFormatDecimalHidden}'
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theGateTemplate.decimalSeparator}'
                                        },
                                        renderer: function (value) {
                                            var store = this.lookupViewModel().get('decimalsSeparators');
                                            if (store) {
                                                var record = store.findRecord('value', value);
                                                value = record ? record.get('label') : value;
                                            }
                                            return value
                                                ? value
                                                : CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                                        }
                                    },
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                    },
                                    forceSelection: true,
                                    autoSelect: false,
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    },
                                    bind: {
                                        store: '{decimalsSeparators}',
                                        value: '{theGateTemplate.decimalSeparator}'
                                    }
                                }
                            })
                        ]
                    }
                ]
            };
        },
        getImportCriteriaFieldset: function () {
            return {
                title: CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria'
                },
                fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
                itemId: 'importcriteriafieldset',
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                layout: 'column',
                collapsible: true,

                bind: {
                    hidden: '{!isImport}'
                },
                listeners: {
                    hide: function (fieldset) {
                        var form = fieldset.up('form');
                        if (form) {
                            fieldset.down('#hiddenImportKeyAttribute').allowBlank = true;
                            form.form.checkValidity();
                        }
                    },
                    show: function (fieldset) {
                        var form = fieldset.up('form');
                        if (form) {
                            fieldset.down('#hiddenImportKeyAttribute').allowBlank = false;
                            form.form.checkValidity();
                        }
                    }
                },
                items: [
                    {
                        xtype: 'textfield',
                        hidden: true,
                        itemId: 'hiddenImportKeyAttribute',
                        bind: {
                            value: '{theGateTemplate._importKeyAttribute}'
                        }
                    },
                    this.createContainer([
                        this.createContainer(
                            [
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
                                    itemId: 'importExportAttributeKeyGrid',
                                    selModel: {
                                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                                    },
                                    viewConfig: {
                                        markDirty: false
                                    },

                                    columnWidth: 1,
                                    autoEl: {
                                        'data-testid':
                                            'administration-content-importexport-gatetemplates-keyattributes-grid'
                                    },

                                    forceFit: true,
                                    loadMask: true,

                                    labelWidth: 'auto',
                                    bind: {
                                        store: '{keyAttributesStore}'
                                    },
                                    columns: [
                                        {
                                            flex: 1,
                                            dataIndex: 'description',
                                            align: 'left',
                                            editor: {
                                                xtype: 'displayfield',
                                                height: 19,
                                                minHeight: 19,
                                                maxHeight: 19,
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo'
                                            },
                                            renderer: function (value, cell, record) {
                                                var vm = this.lookupViewModel();

                                                function recoveryDescription(_vm, _value, _record) {
                                                    _vm.bind(
                                                        {
                                                            bindTo: {
                                                                rawAttributeStore: '{allAttributesStore}'
                                                            },
                                                            sigle: true
                                                        },
                                                        function (data) {
                                                            if (!_value) {
                                                                var attribute = data.rawAttributeStore.findRecord(
                                                                    'name',
                                                                    _record.get('attribute')
                                                                );
                                                                if (attribute) {
                                                                    _record.set(
                                                                        '_attribute_description',
                                                                        attribute.get('description')
                                                                    );
                                                                } else {
                                                                    _record.set(
                                                                        '_attribute_description',
                                                                        _record.get('attribute')
                                                                    );
                                                                }
                                                            }
                                                        }
                                                    );
                                                }
                                                recoveryDescription(vm, value, record);
                                                return value;
                                            }
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
                                                    handler: function (grid, rowIndex, colIndex) {
                                                        var vm = grid.lookupViewModel();
                                                        var store = grid.getStore();
                                                        var record = store.getAt(rowIndex);
                                                        store.remove(record);
                                                        var keys = [];
                                                        store.each(function (item) {
                                                            keys.push(item.get('name'));
                                                        });
                                                        vm.set('theGateTemplate._importKeyAttribute', keys);
                                                        grid.lookupViewModel().filterFreeAttributes();
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
                                                                ' data-testid="administration-importexport-attribute-removeBtn-{0}"',
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
                                    itemId: 'importExportAttributeKeyGridNew',
                                    selModel: {
                                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                                    },
                                    columnWidth: 1,
                                    autoEl: {
                                        'data-testid':
                                            'administration-content-importexport-datatemplates-grid-newrecord'
                                    },

                                    forceFit: true,
                                    loadMask: true,

                                    labelWidth: 'auto',
                                    bind: {
                                        store: '{newKeyAttributesStore}',
                                        hidden: '{actions.view}'
                                    },

                                    columns: [
                                        {
                                            xtype: 'widgetcolumn',
                                            dataIndex: 'attribute',
                                            align: 'left',
                                            flex: 1,
                                            widget: {
                                                xtype: 'combo',
                                                queryMode: 'local',
                                                typeAhead: true,
                                                editable: true,
                                                forceSelection: true,
                                                emptyText:
                                                    CMDBuildUI.locales.Locales.administration.importexport.texts
                                                        .selectanattribute,
                                                localized: {
                                                    emptyText:
                                                        'CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute'
                                                },
                                                itemId: 'selectKeyAttributeForGrid',
                                                displayField: 'description',
                                                valueField: 'name',
                                                bind: {
                                                    store: '{freeKeyAttributesStore}'
                                                },
                                                autoEl: {
                                                    'data-testid': 'administration-importexport-attributekey-name'
                                                },
                                                height: 19,
                                                minHeight: 19,
                                                maxHeight: 19,
                                                padding: 0,
                                                ui: 'reordergrid-editor-combo',
                                                listeners: {
                                                    afterrender: function (combo) {
                                                        var vm = combo.lookupViewModel();
                                                        var form = combo.up('form');
                                                        vm.bind(
                                                            {
                                                                bindTo: '{theGateTemplate._importKeyAttribute}'
                                                            },
                                                            function (keyAttributes) {
                                                                if (!keyAttributes || !keyAttributes.length) {
                                                                    combo.allowBlank = false;
                                                                    combo.markInvalid(
                                                                        CMDBuildUI.locales.Locales.administration.common
                                                                            .messages.thisfieldisrequired
                                                                    );
                                                                } else {
                                                                    combo.allowBlank = true;
                                                                    combo.clearInvalid();
                                                                }
                                                                form.form.checkValidity();
                                                                CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(
                                                                    form.form
                                                                );
                                                            }
                                                        );
                                                    },
                                                    select: function (combo, record) {
                                                        var vm = combo.lookupViewModel();
                                                        var _keyAttributes = vm.get(
                                                            'theGateTemplate._importKeyAttribute'
                                                        );
                                                        var keyAttributes = Ext.Array.from(_keyAttributes);
                                                        keyAttributes.push(combo.getValue());
                                                        vm.set(
                                                            'theGateTemplate._importKeyAttribute',
                                                            keyAttributes.join(',')
                                                        );
                                                        combo.clearValue();
                                                    }
                                                }
                                            }
                                        }
                                    ]
                                }
                            ],
                            {
                                columnWidth: 0.5,
                                xtype: 'fieldcontainer',
                                style: 'margin-top: 20px; margin-right: 10px',
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.importexport.fieldlabels
                                        .importkeattributes,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes'
                                },
                                autoEl: {
                                    'data-testid': 'administration-importexport-attributeKeys-grid'
                                }
                            }
                        )
                    ]),
                    this.createContainer([
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode', {
                            mergeMode: {
                                allowBlank: false,
                                bind: {
                                    value: '{theGateTemplate.mergeMode}',
                                    store: '{mergeModesStore}',
                                    disabled: '{mergeModeDisabled}'
                                },
                                fieldcontainer: {
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.importexport.fieldlabels
                                            .missingrecords,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords'
                                    },
                                    listeners: {
                                        beforeshow: function () {
                                            var input = this.down('#mergeMode_input');
                                            if (input) {
                                                input.forceSelection = true;
                                            }
                                        },
                                        beforehide: function () {
                                            var input = this.down('#mergeMode_input'),
                                                vm = this.lookupViewModel();
                                            if (input) {
                                                input.forceSelection = false;
                                            }
                                            if (input.getValue() === null) {
                                                input.setValue(
                                                    CMDBuildUI.model.importexports.Template.missingRecords.nomerge
                                                );
                                                vm.set('theGateTemplate.mergeMode_when_missing_update_attr', null);
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    ]),

                    // TODO
                    this.createContainer([
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                            'mergeMode_when_missing_update_attr',
                            {
                                mergeMode_when_missing_update_attr: {
                                    fieldcontainer: {
                                        bind: {
                                            hidden: '{!isModifyCard}'
                                        },
                                        fieldLabel:
                                            CMDBuildUI.locales.Locales.administration.importexport.texts
                                                .attributetoedit,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.importexport.texts.attributetoedit'
                                        }
                                    },
                                    allowBlank: true,
                                    valueField: 'name',

                                    combofield: {
                                        queryMode: 'local',
                                        displayField: 'description',
                                        triggers: {
                                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                        },
                                        bind: {
                                            store: '{allClassAttributesStore}',
                                            value: '{theGateTemplate.mergeMode_when_missing_update_attr}'
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theGateTemplate.mergeMode_when_missing_update_attr}'
                                        }
                                    },
                                    listeners: {
                                        beforeselect: function (combo, attributename, oldValue) {
                                            if (oldValue) {
                                                var valueField = this.up('form')
                                                    .getForm()
                                                    .getFields()
                                                    .findBy(function (item) {
                                                        return (
                                                            item.itemId === 'mergeMode_when_missing_update_value_input'
                                                        );
                                                    });
                                                if (valueField) {
                                                    valueField.up('#valueContainer').removeAll();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        ),

                        {
                            xtype: 'fieldcontainer',
                            itemId: 'valueContainer',
                            layout: 'column',
                            columnWidth: 0.5,
                            bind: {
                                hidden: '{!isModifyCard}'
                            },
                            hidden: true,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value'
                            },
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                                    'mergeMode_when_missing_update_value',
                                    {
                                        mergeMode_when_missing_update_value: {
                                            columnWidth: 1,
                                            fieldcontainer: {},
                                            htmlEncode: true,
                                            allowBlank: true,
                                            bind: {
                                                value: '{theGateTemplate._mergeMode_when_missing_update_value_description}'
                                            }
                                        }
                                    }
                                )
                            ],
                            listeners: {
                                hide: function (fieldcontainer) {
                                    var vm = fieldcontainer.lookupViewModel();
                                    vm.set('theGateTemplate._mergeMode_when_missing_update_value_description', '');
                                    vm.set('theGateTemplate.mergeMode_when_missing_update_attr', '');
                                    fieldcontainer.removeAll();
                                }
                            }
                        }
                    ])
                ]
            };
        },

        createContainer: function (items, config) {
            var container = Ext.merge(
                {},
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: items
                },
                config || {}
            );

            return container;
        }
    }
);
