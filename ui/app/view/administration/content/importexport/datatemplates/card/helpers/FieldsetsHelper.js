Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper', {
    singleton: true,

    requires: ['CMDBuildUI.util.administration.helper.FormHelper', 'Ext.grid.plugin.DragDrop'],

    getGeneralPropertiesFieldset: function () {
        return {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            layout: 'column',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [
                this.createGeneralPropertiesContainer(),
                this.createTypeContainer(),
                this.createTargetContainer(),
                this.createFileFormatContainer(),
                this.createCsvContainer(),
                this.createRowContainer(),
                this.createSourceContainer(),
                this.createActiveContainer()
            ]
        };
    },

    createGeneralPropertiesContainer: function () {
        return this.createContainer([this.getCodeInput(), this.getDescriptionInput()]);
    },

    createTypeContainer: function () {
        return this.createContainer([this.getTypeInput()]);
    },

    createTargetContainer: function () {
        return this.createContainer([this.getTargetTypeInput(), this.getTargetNameInput()]);
    },

    createFileFormatContainer: function () {
        return this.createContainer([this.getFileFormatInput()]);
    },

    createCsvContainer: function () {
        return this.createContainer([this.getCsvSeparatorInput(), this.getCharsetInput()]);
    },

    createRowContainer: function () {
        return this.createContainer([this.getHeaderRowInput(), this.getDataRowInput()]);
    },

    createSourceContainer: function () {
        return this.createContainer([this.getSourceInput()]);
    },

    createActiveContainer: function () {
        return this.createContainer([this.getActiveInput()]);
    },

    getCodeInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput(
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
        );
    },

    getDescriptionInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
            description: {
                allowBlank: false,
                bind: {
                    value: '{theGateTemplate.description}'
                },
                fieldcontainer: {
                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateDescriptionClick'
                }
            }
        });
    },

    getTypeInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'type',
            {
                type: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                    forceSelection: true,
                    allowBlank: false,
                    bind: {
                        value: '{theGateTemplate.type}',
                        store: '{templateTypesStore}'
                    },
                    listeners: {
                        change: 'onTypeChange'
                    }
                }
            },
            true
        );
    },

    getTargetTypeInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'targetType',
            {
                targetType: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.applyon,
                    allowBlank: false,
                    forceSelection: true,
                    bind: {
                        value: '{theGateTemplate.targetType}',
                        store: '{targetTypesStore}'
                    },
                    listeners: {
                        change: 'onTargetTypeChange'
                    }
                }
            },
            true
        );
    },

    getTargetNameInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'targetName',
            {
                targetName: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
                    allowBlank: false,
                    displayField: 'description',
                    valueField: 'name',
                    forceSelection: true,
                    bind: {
                        store: '{allClassesOrDomainsOrViews}',
                        value: '{theGateTemplate.targetName}'
                    },
                    listeners: {
                        change: 'onTargetNameChange'
                    },
                    displayfield: {
                        xtype: 'displayfieldwithtriggers',
                        bind: {
                            value: '{theGateTemplate.targetName}',
                            hideTrigger: '{!theGateTemplate.targetName}'
                        },
                        triggers: {
                            open: {
                                cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                                handler: 'onOpenTargetTriggerClick'
                            }
                        }
                    }
                }
            },
            true
        );
    },

    getFileFormatInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fileFormat', {
            fileFormat: {
                allowBlank: true,
                forceSelection: true,
                bind: {
                    value: '{theGateTemplate.fileFormat}',
                    store: '{fileTypesStore}'
                },
                listeners: {
                    change: 'onFileFormatChange'
                },
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat,
                    bind: {
                        hidden: '{fileFormatHidden || "import_database" === theGateTemplate.type ||  "import_ifc" === theGateTemplate.type}'
                    },
                    hidden: true,
                    listeners: {
                        hide: 'onFileFormatContainerHide',
                        show: 'onFileFormatContainerShow'
                    }
                }
            }
        });
    },

    getCsvSeparatorInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('csv_separator', {
            csv_separator: {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isCsv}'
                    },
                    listeners: {
                        hide: 'onCsvSeparatorContainerHide',
                        show: 'onCsvSeparatorContainerShow'
                    }
                },
                forceSelection: true,
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.csvseparator,
                allowBlank: false,
                bind: {
                    value: '{theGateTemplate.csv_separator}',
                    store: '{csvSeparatorsStore}'
                }
            }
        });
    },

    getCharsetInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('charset', {
            charset: {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isCsv}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset'
                    },
                    listeners: {
                        hide: 'onCharsetContainerHide'
                    }
                },
                combofield: {
                    forceSelection: true,
                    bind: {
                        store: '{csvCharsetStore}',
                        value: '{theGateTemplate.charset}'
                    },
                    emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts['default'],
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.importexport.texts.default'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theGateTemplate._charset_description}'
                    },
                    renderer: function (value) {
                        if (!value) {
                            var me = this;
                            var vm = me.lookupViewModel();
                            vm.bind(
                                {
                                    bindTo: {
                                        storeLoaded: '{csvCharsetStoreLoaded}',
                                        value: '{theGateTemplate.charset}'
                                    }
                                },
                                function (data) {
                                    var description =
                                        CMDBuildUI.locales.Locales.administration.importexport.texts['default'];
                                    var store = vm.get('csvCharsetStore');

                                    if (!Ext.isEmpty(data.value) && store && store.getCount() > 0) {
                                        var record = store.findRecord('_id', data.value);
                                        description = record ? record.get('description') : description;
                                    }
                                    vm.set('theGateTemplate._charset_description', description);
                                }
                            );
                        }
                        return value;
                    }
                },
                displayField: 'description',
                valueField: '_id',
                queryMode: 'local',
                forceSelection: true,
                anyMatch: true,
                typeAhead: true,
                autoSelect: true,
                name: 'charset',
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }
        });
    },

    getHeaderRowInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('headerRow', {
            headerRow: {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isExcell}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.headerrownumber,
                allowBlank: false,
                minValue: 0,
                bind: {
                    value: '{theGateTemplate.headerRow}'
                }
            }
        });
    },

    getDataRowInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('dataRow', {
            dataRow: {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isExcell}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.datarownumber,
                allowBlank: false,
                minValue: 1,
                bind: {
                    value: '{theGateTemplate.dataRow}'
                }
            }
        });
    },

    getSourceInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('source', {
            source: {
                fieldcontainer: {
                    hidden: true,
                    bind: {
                        hidden: '{!isImportWithSource}'
                    },
                    listeners: {
                        hide: 'onSourceContainerHide',
                        show: 'onSourceContainerShow'
                    }
                },
                forceSelection: true,
                allowBlank: true,
                bind: {
                    value: '{theGateTemplate.source}',
                    fieldLabel: '{sourceInputLabel}'
                }
            }
        });
    },

    getActiveInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
            active: {
                bind: {
                    value: '{theGateTemplate.active}'
                }
            }
        });
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
            items: [this.getAttributesGrid(), this.getNewAttributesGrid(), this.getDataFormatFieldset()]
        };
    },

    getAttributesGrid: function () {
        return {
            xtype: 'grid',
            itemId: 'importExportAttributeGrid',
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
            selModel: {
                pruneRemoved: false
            },
            viewConfig: {
                markDirty: false
            },
            dockedItems: [this.getAttributesGridToolbar()],
            plugins: this.getAttributesGridPlugins(),
            columnWidth: 1,
            autoEl: {
                'data-testid': 'administration-content-importexport-datatemplates-grid'
            },
            forceFit: true,
            loadMask: true,
            labelWidth: 'auto',
            bind: {
                store: '{allSelectedAttributesStore}',
                hidden: '{isAttributeGridHidden}'
            },
            columns: this.getAttributesGridColumns()
        };
    },

    getAttributesGridToolbar: function () {
        return {
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
                    tooltip: CMDBuildUI.locales.Locales.administration.importexport.texts.addallattributes,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.importexport.texts.addallattributes'
                    },
                    autoEl: {
                        'data-testid': 'administration-importexpport-addAllFreeAttributesBtn'
                    }
                }
            ]
        };
    },

    getAttributesGridPlugins: function () {
        return {
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
        };
    },

    getAttributesGridColumns: function () {
        return [
            {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
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
                renderer: 'renderAttributeDescription'
            },
            {
                text: CMDBuildUI.locales.Locales.administration.importexport.texts.columnname,
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
                flex: 1,
                dataIndex: 'mode',
                align: 'left',
                editor: {
                    xtype: 'combo',
                    displayField: 'label',
                    valueField: 'value',
                    queryMode: 'local',
                    emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode,
                    editable: false,
                    height: 19,
                    minHeight: 19,
                    maxHeight: 19,
                    padding: 0,
                    ui: 'reordergrid-editor-combo',
                    forceSelection: true,
                    allowBlank: false,
                    bind: {
                        store: '{attributeModesReferenceStore}'
                    },
                    listeners: {
                        beforerender: 'onModeComboBeforeRender',
                        expand: 'onModeComboExpand'
                    }
                },
                renderer: 'renderAttributeMode'
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
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        handler: 'onEditAttributeClick',
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                        },
                        getClass: function (value, metadata, record) {
                            return record.get('editing')
                                ? CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid')
                                : CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
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
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                        },
                        handler: 'onMoveAttributeUp',
                        isActionDisabled: 'isMoveUpDisabled',
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            return record.get('editing')
                                ? CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid')
                                : CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid');
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
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                        },
                        handler: 'onMoveAttributeDown',
                        isActionDisabled: 'isMoveDownDisabled',
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            return record.get('editing')
                                ? CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid')
                                : CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid');
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
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                        },
                        handler: 'onRemoveAttribute',
                        isActionDisabled: 'isRemoveAttributeDisabled',
                        getClass: 'getRemoveAttributeClass'
                    }
                ]
            }
        ];
    },

    getNewAttributesGrid: function () {
        return {
            margin: '20 0 20 0',
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
            itemId: 'importExportAttributeGridNew',
            selModel: {
                pruneRemoved: false
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
                hidden: '{isAttributeGridNewHidden}'
            },
            columns: this.getNewAttributesGridColumns()
        };
    },

    getNewAttributesGridColumns: function () {
        return [
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
                    emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                    itemId: 'selectAttributeForGrid',
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        store: '{allClassOrDomainsAtributesFiltered}'
                    },
                    autoEl: {
                        'data-testid': 'administration-importexport-attribute-name'
                    },
                    listeners: {
                        afterrender: 'onSelectAttributeAfterRender',
                        change: 'onSelectAttributeChange'
                    },
                    setStoreFilter: function () {
                        var me = this;
                        var vm = me.lookupViewModel();
                        var allAttrStore = vm.get('allSelectedAttributesStore');
                        if (me.getStore() && me.getStore().source) {
                            me.getStore().source.rejectChanges();
                            me.getStore().clearFilter();
                            if (allAttrStore.getRange()) {
                                var allowedAttributes = [CMDBuildUI.util.helper.ModelHelper.extraFields.notes];
                                var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(
                                    CMDBuildUI.model.Configuration.multitenant.enabled
                                );
                                if (allowTenantAttribute) {
                                    allowedAttributes.push('IdTenant');
                                }
                                me.getStore().addFilter(function (item) {
                                    var isSelected = allAttrStore.findRecord(
                                        'attribute',
                                        item.get('name'),
                                        false,
                                        false,
                                        true,
                                        true
                                    );
                                    if (isSelected) {
                                        vm.getParent().getParent().manageDataFormatFieldset(item);
                                    }

                                    // some system attributes do not have a description
                                    // set the description equal to the name
                                    if (!item.get('description')) {
                                        item.set('description', item.get('name'));
                                    }

                                    var notAllowedTypes = [
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                    ];
                                    var isAttributeTypeAllowed = notAllowedTypes.indexOf(item.get('type')) === -1;
                                    // if template type is not `import` we need to exclude password fields
                                    if (
                                        vm.get('theGateTemplate.type') !==
                                            CMDBuildUI.model.importexports.Template.types.import &&
                                        item.get('password')
                                    ) {
                                        return false;
                                    }

                                    return (
                                        item.canAdminShow(allowedAttributes) && !isSelected && isAttributeTypeAllowed
                                    );
                                });
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
                    bind: {
                        store: '{attributeModesReferenceStore}'
                    },
                    autoEl: {
                        'data-testid': 'administration-importexport-attribute-mode'
                    },
                    listeners: {
                        expand: 'onNewComboModeExpand'
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
                minWidth: 30,
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
                minWidth: 30,
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
                minWidth: 30,
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
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                        autoEl: {
                            'data-testid': 'administration-importexport-attribute-addBtn'
                        },
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.add;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            metadata.value = Ext.String.insert(
                                metadata.value,
                                ' data-testid="administration-importexport-attribute-addBtn"',
                                -7
                            );
                            return CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid');
                        },
                        handler: 'onAddNewAttribute'
                    }
                ]
            }
        ];
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
                this.createDataFormatContainer(),
                this.createDateTimeFormatContainer(),
                this.createDecimalSeparatorContainer()
            ]
        };
    },

    createDataFormatContainer: function () {
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [this.getDateFormatInput(), this.getTimeFormatInput()]
        };
    },

    getDateFormatInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('dateFormat', {
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
                        return value || CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
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
        });
    },

    getTimeFormatInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('timeFormat', {
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
                        return value || CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
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
        });
    },

    createDateTimeFormatContainer: function () {
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [
                {
                    xtype: 'displayfield',
                    hidden: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.datetimeformat'
                    },
                    bind: {
                        hidden: '{dataFormatDateTimeHidden}',
                        value: '{dateTimeFormat}'
                    }
                }
            ]
        };
    },

    createDecimalSeparatorContainer: function () {
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [this.getDecimalSeparatorInput()]
        };
    },

    getDecimalSeparatorInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('decimalSeparator', {
            decimalSeparator: {
                fieldcontainer: {
                    hidden: true,
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
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
                        return value || CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
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
        });
    },

    getImportCriteriaFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.importcriteria'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'importfieldset',
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            bind: {
                hidden: '{!isImport}'
            },
            items: [
                this.getHiddenImportKeyAttribute(),
                this.createImportModeContainer(),
                this.createImportMergeSkipContainer(),
                this.createKeyAttributesContainer(),
                this.createMergeModeContainer(),
                this.createMissingRecordsContainer()
            ]
        };
    },

    getHiddenImportKeyAttribute: function () {
        return {
            xtype: 'textfield',
            hidden: true,
            itemId: 'hiddenImportKeyAttribute',
            bind: {
                value: '{theGateTemplate._importKeyAttribute}'
            },
            listeners: {
                afterrender: 'onHiddenImportKeyAttributeAfterRender'
            }
        };
    },

    createImportModeContainer: function () {
        return this.createContainer([this.getImportModeInput()]);
    },

    createImportMergeSkipContainer: function () {
        return this.createContainer([this.getImportMergeSkipInput()]);
    },

    getImportModeInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('_importMode', {
            _importMode: {
                fieldcontainer: {
                    hidden: true,
                    bind: {
                        hidden: '{isDomain}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importmode,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.importmode'
                },
                allowBlank: false,
                displayField: 'label',
                valueField: 'value',
                bind: {
                    store: '{importModesStore}',
                    value: '{theGateTemplate._importMode}'
                }
            }
        });
    },

    getImportMergeSkipInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonChekboxInput(
            {
                _importMergeSkip: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.mergeskip,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.mergeskip'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{theGateTemplate._importMode !== "merge"}'
                        }
                    },
                    bind: {
                        value: '{theGateTemplate.enableCreate === "skip"}'
                    },
                    listeners: {
                        change: 'onMergeSkipChange'
                    }
                }
            },
            '_importMergeSkip'
        );
    },

    createKeyAttributesContainer: function () {
        return this.createContainer([this.getKeyAttributesGrid(), this.getNewKeyAttributesGrid()], {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            style: 'margin-top: 20px; margin-right: 10px',
            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes'
            },
            bind: {
                hidden: '{theGateTemplate.targetType === "domain" || theGateTemplate._importMode === "add"}'
            },
            autoEl: {
                'data-testid': 'administration-importexport-attributeKeys-grid'
            }
        });
    },

    getKeyAttributesGrid: function () {
        return {
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
                pruneRemoved: false
            },
            viewConfig: {
                markDirty: false
            },
            columnWidth: 1,
            autoEl: {
                'data-testid': 'administration-content-importexport-gatetemplates-keyattributes-grid'
            },
            forceFit: true,
            loadMask: true,
            labelWidth: 'auto',
            bind: {
                store: '{keyAttributesStore}'
            },
            columns: this.getKeyAttributesGridColumns()
        };
    },

    getKeyAttributesGridColumns: function () {
        return [
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
                renderer: 'renderKeyAttributeDescription'
            },
            {
                xtype: 'actioncolumn',
                itemId: 'actionColumnCancel',
                bind: {
                    hidden: '{actions.view}'
                },
                width: 30,
                minWidth: 30,
                maxWidth: 30,
                align: 'center',
                items: [
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            var vm = grid.up('form').getViewModel();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            store.remove(record);
                            var keys = [];
                            store.each(function (item) {
                                keys.push(item.get('name'));
                            });
                            vm.set('theGateTemplate._importKeyAttribute', keys);
                            if (!keys.length) {
                                grid.up('fieldcontainer')
                                    .down('#selectKeyAttributeForGrid')
                                    .markInvalid(
                                        CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired
                                    );
                            }
                            vm.filterFreeAttributes();
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
                                return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                            }
                            return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                        }
                    }
                ]
            }
        ];
    },

    getNewKeyAttributesGrid: function () {
        return {
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
                pruneRemoved: false
            },
            columnWidth: 1,
            autoEl: {
                'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
            },
            forceFit: true,
            loadMask: true,
            labelWidth: 'auto',
            bind: {
                store: '{newKeyAttributesStore}',
                hidden: '{actions.view}'
            },
            columns: this.getNewKeyAttributesGridColumns()
        };
    },

    getNewKeyAttributesGridColumns: function () {
        return [
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
                    emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute'
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
                        afterrender: 'onSelectKeyAttributeAfterRender',
                        select: 'onSelectKeyAttribute'
                    }
                }
            }
        ];
    },

    createMergeModeContainer: function () {
        return this.createContainer([this.getMergeModeInput(), this.getHandleMissingRecordsOnErrorInput()]);
    },

    getMergeModeInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode', {
            mergeMode: {
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords'
                },
                allowBlank: false,
                bind: {
                    value: '{theGateTemplate.mergeMode}',
                    store: '{mergeModesStore}',
                    disabled: '{theGateTemplate.fileFormat === "ifc"}'
                },
                fieldcontainer: {
                    bind: {
                        hidden: '{isAddMode}'
                    },
                    listeners: {
                        beforeshow: 'onMergeModeBeforeShow',
                        beforehide: 'onMergeModeBeforeHide'
                    }
                },
                listeners: {
                    change: 'onMergeModeChange'
                }
            }
        });
    },

    getHandleMissingRecordsOnErrorInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
            'handleMissingRecordsOnError',
            {
                handleMissingRecordsOnError: {
                    fieldcontainer: {
                        fieldLabel:
                            CMDBuildUI.locales.Locales.administration.importexport.fieldlabels
                                .handlemissingrecordsonerror,
                        localized: {
                            fieldLabel:
                                'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.handlemissingrecordsonerror'
                        },
                        hidden: true,
                        bind: {
                            hidden: Ext.String.format(
                                '{({0} !== "{1}" && {0} !== "{2}") ||  ("database" === theGateTemplate.fileFormat || "ifc" === theGateTemplate.fileFormat)}',
                                'theGateTemplate.mergeMode',
                                CMDBuildUI.model.importexports.Template.missingRecords['delete'],
                                CMDBuildUI.model.importexports.Template.missingRecords.modifycard
                            )
                        },
                        listeners: {
                            hide: 'onHandleMissingRecordsOnErrorHide'
                        }
                    },
                    bind: {
                        disabled: '{actions.view}',
                        value: '{theGateTemplate.handleMissingRecordsOnError}'
                    }
                }
            }
        );
    },

    createMissingRecordsContainer: function () {
        return this.createContainer([
            this.getMergeModeWhenMissingUpdateAttrInput(),
            this.getMergeModeWhenMissingUpdateValueInput()
        ]);
    },

    getMergeModeWhenMissingUpdateAttrInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'mergeMode_when_missing_update_attr',
            {
                mergeMode_when_missing_update_attr: {
                    fieldcontainer: {
                        hidden: false,
                        bind: {
                            hidden: '{!isModifyCard}'
                        },
                        listeners: {
                            hide: 'onMergeModeWhenMissingUpdateAttrHide',
                            show: 'onMergeModeWhenMissingUpdateAttrShow'
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.attributetoedit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.attributetoedit'
                    },
                    allowBlank: false,
                    queryMode: 'local',
                    itemId: 'combo_mergeMode_when_missing_update_attr',
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        store: '{allClassOrDomainsAtributes}',
                        value: '{theGateTemplate.mergeMode_when_missing_update_attr}'
                    },
                    listeners: {
                        beforeselect: 'onMergeModeWhenMissingUpdateAttrBeforeSelect',
                        change: 'onMergeModeWhenMissingUpdateAttrChange'
                    },
                    displayfield: {
                        bind: {
                            value: '{theGateTemplate._mergeMode_when_missing_update_attr_description}'
                        }
                    }
                }
            }
        );
    },

    getMergeModeWhenMissingUpdateValueInput: function () {
        return {
            xtype: 'fieldcontainer',
            itemId: 'valueContainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: false,
            bind: {
                hidden: '{!isModifyCard}'
            },
            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value'
            },
            listeners: {
                hide: 'onMergeModeWhenMissingUpdateValueHide',
                show: 'onMergeModeWhenMissingUpdateValueShow'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                    'mergeMode_when_missing_update_value',
                    {
                        mergeMode_when_missing_update_value: {
                            columnWidth: 1,
                            htmlEncode: true,
                            bind: {
                                value: '{theGateTemplate._mergeMode_when_missing_update_value_description}'
                            }
                        }
                    }
                )
            ]
        };
    },

    getExportCriteriaFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.exportfilter,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.exportfilter'
            },
            xtype: 'fieldset',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'exportfilterfieldset',
            ui: 'administration-formpagination',
            collapsible: true,
            bind: {
                hidden: '{!isExport || isDomain}'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
                                    },
                                    columnWidth: 1,
                                    items: [
                                        {
                                            xtype: 'components-administration-toolbars-formtoolbar',
                                            style: 'border:none; margin-top: 5px',
                                            items: [
                                                this.getEditFilterButton('export'),
                                                this.getRemoveFilterButton('export')
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getImportFilterFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.importfilter,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.importfilter'
            },
            xtype: 'fieldset',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'importfilterfieldset',
            ui: 'administration-formpagination',
            collapsible: true,
            bind: {
                hidden: '{!isImport}'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
                                    },
                                    columnWidth: 1,
                                    items: [
                                        {
                                            xtype: 'components-administration-toolbars-formtoolbar',
                                            style: 'border:none; margin-top: 5px',
                                            items: [
                                                this.getEditFilterButton('import'),
                                                this.getRemoveFilterButton('import')
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getEditFilterButton: function (type) {
        return {
            xtype: 'tool',
            align: 'right',
            itemId: Ext.String.format('edit{0}FilterBtn', Ext.String.capitalize(type)),
            cls: 'administration-tool margin-right5',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters'
            },
            autoEl: {
                'data-testid': 'administration-searchfilter-tool-editfilterbtn'
            },
            bind: {
                disabled: '{!theGateTemplate.targetName}'
            }
        };
    },

    getRemoveFilterButton: function (type) {
        return {
            xtype: 'tool',
            align: 'right',
            itemId: Ext.String.format('remove{0}FilterBtn', Ext.String.capitalize(type)),
            cls: 'administration-tool margin-right5',
            iconCls: 'cmdbuildicon-filter-remove',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
            },
            autoEl: {
                'data-testid': 'administration-searchfilter-tool-removefilterbtn'
            },
            bind: {
                disabled: '{actions.view}'
            }
        };
    },

    getErrorsManagementFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.notifications,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.notifications'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [this.getNotificationTemplateInput(), this.getErrorAccountInput()]
                }
            ]
        };
    },

    getNotificationTemplateInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('notificationTemplate', {
            notificationTemplate: {
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.notificationtemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.notificationtemplate'
                },
                fieldcontainer: {
                    columnWidth: 0.5
                },
                allowBlank: true,
                displayField: 'description',
                valueField: 'name',
                forceSelection: true,
                combofield: {
                    bind: {
                        store: '{notificationEmailTemplates}',
                        value: '{theGateTemplate.notificationTemplate}'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theGateTemplate._notificationEmailTemplate_description}'
                    },
                    renderer: function (value) {
                        var me = this;
                        var vm = me.lookupViewModel();
                        vm.bind(
                            {
                                bindTo: {
                                    store: '{notificationEmailTemplatesStore}',
                                    value: '{theGateTemplate.notificationTemplate}'
                                },
                                single: true
                            },
                            function (data) {
                                if (!Ext.isEmpty(data.value) && data.store) {
                                    var record = data.store.findRecord('name', data.value);
                                    vm.set(
                                        'theGateTemplate._notificationEmailTemplate_description',
                                        record.get('description')
                                    );
                                } else {
                                    vm.set('theGateTemplate._notificationEmailTemplate_description', value || null);
                                }
                            }
                        );
                        return value;
                    }
                }
            }
        });
    },

    getErrorAccountInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorAccount', {
            errorAccount: {
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.account,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.texts.account'
                },
                fieldcontainer: {
                    columnWidth: 0.5
                },
                displayField: 'name',
                valueField: 'name',
                forceSelection: true,
                bind: {
                    store: '{allEmailAccounts}',
                    value: '{theGateTemplate.errorAccount}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                },
                displayfield: {
                    bind: {
                        value: '{theGateTemplate._errorEmailAccount_description}'
                    },
                    renderer: function (value) {
                        var me = this;
                        var vm = me.lookupViewModel();
                        vm.bind(
                            {
                                bindTo: {
                                    store: '{allEmailAccounts}',
                                    value: '{theGateTemplate.errorAccount}'
                                },
                                single: true
                            },
                            function (data) {
                                if (!vm.destroyed && !Ext.isEmpty(data.value) && data.store) {
                                    var record = data.store.findRecord('name', data.value);
                                    if (record) {
                                        vm.set('theGateTemplate._errorEmailAccount_description', record.get('name'));
                                    }
                                }
                            }
                        );
                        return value;
                    }
                }
            }
        });
    },

    createContainer: function (items, config) {
        var container = Ext.merge(
            {},
            {
                xtype: 'container',
                layout: 'column',
                columnWidth: 1,
                items: items
            },
            config || {}
        );

        return container;
    }
});
