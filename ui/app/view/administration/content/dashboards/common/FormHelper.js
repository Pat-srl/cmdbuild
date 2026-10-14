Ext.define('CMDBuildUI.view.administration.content.dashboards.common.FormHelper', {
    singleton: true,

    getIntegerFreeNumberfField: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('defaultValue', {
            defaultValue: {
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                },
                step: 0,
                minValue: 0,
                hideTrigger: true,
                bind: {
                    value: '{theParameter.defaultValue}',
                    readOnly: '{actions.view}'
                }
            }
        });
    },
    getIntegerFreeFieldContainer: function () {
        var me = this;

        return me.getRow(
            [
                {
                    xtype: 'container',
                    itemId: 'integerfreecontainer',
                    columnWidth: 0.5,
                    items: [me.getIntegerFreeNumberfField()]
                }
            ],
            {
                hidden: true,
                bind: {
                    hidden: '{hiddenfields.integerfree}'
                }
            }
        );
    },
    getIntegerLookupFieldContainer: function () {
        var me = this;

        return me.getRow(me.getIntegerLookupTypeFields(), {
            itemId: 'integerlookupcontainer',
            hidden: true,
            bind: {
                hidden: '{hiddenfields.lookup}'
            }
        });
    },
    getIntegerCardFieldContainer: function () {
        var me = this;
        return me.getRow(
            [
                me.getRow([
                    me.getClassesCombo(),
                    {
                        xtype: 'container',
                        columnWidth: 0.5,
                        itemId: 'carddefaultvaluecontainer',
                        items: []
                    }
                ])
            ],
            {
                hidden: true,
                bind: {
                    hidden: '{hiddenfields.card}'
                }
            }
        );
    },
    getParameterDefaultValueThreestateCheckbox: function () {
        var me = this;
        return me.getRow([
            me.getRow([
                {
                    xtype: 'threestatecheckboxfield',
                    disableLabelClick: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                    },
                    bind: {
                        value: '{theParameter.defaultValue}',
                        disabled: '{actions.view}'
                    }
                }
            ])
        ]);
    },

    getRow: function (items, config) {
        return CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getRow(items, config);
    },

    getLocalParameterFieldsContainer: function (items) {
        return {
            xtype: 'container',
            layout: 'column',
            columnWidth: 1,
            bind: {
                hidden: '{theParameter.useGlobalParameter}'
            },
            items: items
        };
    },

    privates: {
        getDataSourceTypeInput: function () {
            var fieldName = 'dataSourceType';
            var config = {};
            config[fieldName] = {
                fieldcontainer: {
                    layout: 'column',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sourcetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sourcetype'
                    }
                },
                displayField: 'label',
                valueField: 'value',
                bind: {
                    value: '{theComponent.dataSourceType}',
                    store: '{dataSourceTypeStore}'
                }
            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(fieldName, config);
        },

        getClassesAndProcessesInput: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                {
                    dataSourceClassName: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.localizations['classorprocess'],
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.classorprocess'
                            }
                        },
                        withClasses: true,
                        withProcesses: true,
                        bind: {
                            value: '{theComponent.dataSourceName}'
                        }
                    }
                },
                'dataSourceClassName'
            );
        },

        getStringInput: function (config, disabledOnEdit, copyToInput) {
            const fieldHelper = CMDBuildUI.util.administration.helper.FieldsHelper;

            const propertyName = config.propertyName;
            if (!propertyName || !config[propertyName]) return {};

            const subConfig = config[propertyName];
            const input = fieldHelper._getTextfield(subConfig, propertyName, false, disabledOnEdit, copyToInput);
            const display = fieldHelper._setTestId(fieldHelper._getDisplayfield(subConfig), propertyName);

            const fieldcontainer = Ext.merge(
                {},
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 0.5,
                    allowBlank: subConfig.allowBlank,
                    fieldLabel: config.fieldLabel,
                    localized: {
                        fieldLabel: config.fieldLabel
                    },
                    items: [input, display]
                },
                subConfig.fieldcontainer || {}
            );

            return fieldcontainer;
        },

        getListAttributesFieldForType: function (type, storeName) {
            const fieldsHelper = CMDBuildUI.util.administration.helper.FieldsHelper;
            const labelKey = type.replace(/Field$/, '');
            const dashboardConfig = {};
            dashboardConfig[type] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards[labelKey],
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.' + labelKey
                    },
                    bind: {
                        disabled: '{!theComponent.dataSourceName}'
                    }
                },
                displayField: 'description',
                valueField: 'name',
                bind: {
                    value: '{theComponent.' + type + '}',
                    store: '{' + storeName + '}',
                    disabled: '{!theComponent.dataSourceName}'
                }
            };
            return fieldsHelper.getCommonComboInput(type, dashboardConfig);
        },

        getAllViewsDataSourceInput: function () {
            var fieldName = 'dataSourceViewName';
            var config = {};
            config[fieldName] = {
                columnWidth: 1,
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.view,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.view'
                    }
                },
                displayField: 'description',
                valueField: 'name',
                bind: {
                    store: '{getAllViewsStore}',
                    value: '{theComponent.dataSourceName}'
                }
            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(fieldName, config);
        },
        getDataSourceLimitInput: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('dataSourceLimit', {
                dataSourceLimit: {
                    fieldcontainer: {
                        columnWidth: 1,
                        allowBlank: true,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.rowlimit,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.rowlimit'
                        }
                    },
                    minValue: 0,
                    columnWidth: 0.5,
                    bind: {
                        value: '{theComponent.dataSourceLimit}'
                    }
                }
            });
        },
        getDataSourceFilterTextarea: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('dataSourceFilter', {
                dataSourceFilter: {
                    xtype: 'textarea',
                    itemId: 'filter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                    },
                    resizable: {
                        handles: 's'
                    },
                    name: 'filter',
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{theComponent.dataSourceFilter}'
                    }
                }
            });
        },
        getClassesCombo: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                {
                    classToUseForReferenceWidget: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fromclass,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fromclass'
                            }
                        },
                        withClasses: true,
                        withProcesses: true,
                        bind: {
                            value: '{theParameter.classToUseForReferenceWidget}',
                            disabled: '{actions.view}'
                        },

                        listeners: {
                            change: function (combo, newValue, oldValue) {
                                var deafultValueContainer = combo.up('fieldset').down('#carddefaultvaluecontainer');
                                deafultValueContainer.removeAll();
                                if (newValue) {
                                    deafultValueContainer.add({
                                        xtype: 'referencecombofield',
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                                        },
                                        displayField: 'Description',
                                        itemId: 'defaultValue_input',
                                        name: 'defaultValue',
                                        valueField: '_id',
                                        style: 'padding-right: 15px',
                                        metadata: {
                                            targetType:
                                                CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(newValue),
                                            targetClass: newValue
                                        },
                                        hidden: true,
                                        bind: {
                                            hidden: '{actions.view}',
                                            value: '{theParameter.defaultValue}'
                                        }
                                    });
                                }
                            }
                        }
                    }
                },
                'classToUseForReferenceWidget'
            );
        },
        getTextTextarea: function () {
            return CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                columnWidth: 1,
                hidden: true,
                bind: {
                    value: '{theComponent.text}',
                    hidden: '{theComponent.type !== "text"}'
                }
            });
        },
        getParameterFilterTextarea: function (parameter) {
            return {
                columnWidth: 1,
                xtype: 'textarea',
                itemId: 'filter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                },
                resizable: {
                    handles: 's'
                },
                name: 'filter',
                hidden: true,
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theParameter.filter.expression}',
                    hidden: '{hiddenfields.cqlfilter}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get('theParameter.filter.expression') !== newValue) {
                            vm.get('theParameter.filter').expression = newValue;
                        }
                    }
                }
            };
        },

        getIntegerLookupTypeFields: function () {
            var me = this;
            var comboFieldName = 'lookupType';
            var comboConfig = {};
            comboConfig[comboFieldName] = {
                columnWidth: 1,
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.lookuptype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.lookuptype'
                    }
                },
                displayField: 'description',
                valueField: 'name',
                forceSelection: true,
                bind: {
                    value: '{theParameter.lookupType}',
                    store: '{integerLookupTypesStore}',
                    disabled: '{actions.view}'
                },
                listeners: {
                    change: function (_combo, newValue, oldValue) {
                        if (oldValue !== newValue) {
                            var valueContainer = _combo.up('fieldset').down('#lookupValuecontainer');
                            valueContainer.removeAll();
                            valueContainer.add(me.getIntegerLookupValueFields(newValue));
                        }
                    }
                }
            };

            var combo = CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                comboFieldName,
                comboConfig
            );
            return [
                {
                    xtype: 'container',
                    itemId: 'lookupTypecontainer',
                    columnWidth: 0.5,
                    items: [combo]
                },
                {
                    xtype: 'container',
                    itemId: 'lookupValuecontainer',
                    columnWidth: 0.5,
                    items: []
                }
            ];
        },

        getPreselectIfUnique: function () {
            var preselectIfUniqueFieldName = 'preselectIfUnique';
            var preselectIfUniqueConfig = {};
            preselectIfUniqueConfig[preselectIfUniqueFieldName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.preselectifunique,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.preselectifunique'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{theParameter.fieldType !== "card" && theParameter.fieldType !== "lookup"}'
                    }
                },
                bind: {
                    value: '{theParameter.preselectIfUnique}',
                    disabled: '{actions.view}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                preselectIfUniqueFieldName,
                preselectIfUniqueConfig
            );
        },
        getIntegerLookupValueFields: function (lookupType) {
            return {
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                },
                items: [
                    {
                        xtype: 'lookupfield',
                        metadata: {
                            lookupType: lookupType
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}',
                            value: '{theParameter.defaultValue}'
                        }
                    },
                    {
                        xtype: 'displayfield',
                        bind: {
                            value: '{theParameter.__description}',
                            hidden: '{!actions.view}'
                        }
                    }
                ]
            };
        },

        getShowLegendField: function (theComponent) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('legend', {
                legend: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.showlegend,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.showlegend'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{theComponent.type === "text" || theComponent.type === "gauge" || theComponent.type === "pie"}'
                        }
                    },

                    bind: {
                        disabled: '{actions.view}',
                        value: '{theComponent.legend}'
                    }
                }
            });
        },
        singleSeriesField: function (theComponent) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('singleSeriesField', {
                singleSeriesField: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.singleSeriesField}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                        }
                    },
                    displayField: 'name',
                    valueField: '_id',
                    bind: {
                        value: '{theComponent.singleSeriesField}',
                        store: '{functionAttributesStoreNumeric}'
                    }
                }
            });
        },
        getLabelField: function (theComponent) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('labelField', {
                labelField: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.labelField}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.labelfield,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.labelfield'
                        },
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onTranslateComponentLabelFieldClick'
                    },

                    displayField: 'name',
                    valueField: '_id',
                    bind: {
                        value: '{theComponent.labelField}',
                        store: '{functionAttributesStore}'
                    }
                }
            });
        },
        getMaximumField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maximum', {
                maximum: {
                    fieldcontainer: {
                        columnWidth: 0.5,
                        allowBlank: true,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.maximum,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.maximum'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.maximum}'
                        }
                    },
                    columnWidth: 1,
                    bind: {
                        value: '{theComponent.maximum}'
                    }
                }
            });
        },
        getMinimumField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('minimum', {
                minimum: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.minimum,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.minimum'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.maximum}'
                        },
                        columnWidth: 0.5
                    },
                    allowBlank: true,

                    bind: {
                        value: '{theComponent.minimum}'
                    }
                }
            });
        },
        stepsField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('steps', {
                steps: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.steps,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.steps'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.steps}'
                        },
                        columnWidth: 1
                    },
                    columnWidth: 0.5,
                    allowBlank: true,

                    bind: {
                        value: '{theComponent.steps}'
                    }
                }
            });
        },
        getFgcolorField: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.fgcolor}'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor'
                },
                items: [
                    CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                        alt: CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.dashboards.foregroundcolor'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}',
                            value: '{theComponent.fgcolor}'
                        }
                    })
                ]
            };
        },
        getBgcolorField: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.bgcolor}'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor'
                },
                items: [
                    CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                        alt: CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.dashboards.backgroundcolor'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}',
                            value: '{theComponent.bgcolor}'
                        }
                    })
                ]
            };
        },
        getCategoryAxisFiledset: function () {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.dashboards.categoryaxis,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.dashboards.categoryaxis'
                },
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.categoryAxisLabel || !theComponent.dataSourceName}'
                },
                items: [
                    this.getRow([
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                            'categoryAxisLabel',
                            {
                                categoryAxisLabel: {
                                    fieldcontainer: {
                                        bind: {
                                            hidden: '{hiddenTypeProperty.categoryAxisLabel}'
                                        },
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.title,
                                        localized: {
                                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.title'
                                        },
                                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                        labelToolIconQtip:
                                            CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                        labelToolIconClick: 'onTranslateCategoryAxisTitleClick'
                                    },

                                    allowBlank: true,
                                    bind: {
                                        value: '{theComponent.categoryAxisLabel}'
                                    }
                                }
                            }
                        ),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('categoryAxisField', {
                            categoryAxisField: {
                                fieldcontainer: {
                                    hidden: true,
                                    bind: {
                                        hidden: '{hiddenTypeProperty.categoryAxisField}'
                                    },
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                                    }
                                },

                                displayField: 'name',
                                valueField: '_id',
                                bind: {
                                    value: '{theComponent.categoryAxisField}',
                                    store: '{functionAttributesStore}'
                                }
                            }
                        })
                    ])
                ]
            };
        },
        getValueAxisFiledset: function () {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.dashboards.valueaxis,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.dashboards.valueaxis'
                },
                hidden: true,
                bind: {
                    hidden: '{hiddenTypeProperty.valueAxisLabel || !theComponent.dataSourceName}'
                },
                items: [
                    this.getRow([
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('valueAxisLabel', {
                            valueAxisLabel: {
                                fieldcontainer: {
                                    hidden: true,
                                    bind: {
                                        hidden: '{hiddenTypeProperty.valueAxisLabel}'
                                    },
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.title,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.title'
                                    },
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateValueAxisLabelClick'
                                },

                                allowBlank: true,
                                bind: {
                                    value: '{theComponent.valueAxisLabel}'
                                }
                            }
                        }),
                        {
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            columnWidth: 0.5,
                            hidden: true,
                            bind: {
                                hidden: '{hiddenTypeProperty.valueAxisFields}'
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.valuefield,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.valuefield'
                            },
                            items: [
                                {
                                    xtype: 'multiselectfield',
                                    hidden: true,
                                    width: '100%',
                                    columnWidth: 1,
                                    itemId: 'valueAxisFields',
                                    bind: {
                                        hidden: '{actions.view || !valueAxisFieldsMultiple}',
                                        store: '{functionAttributesStore}'
                                    },
                                    displayField: 'name',
                                    valueField: '_id',
                                    listeners: {
                                        change: function (input, newValue, oldValue) {
                                            var newSortedValue = [];
                                            input.getStore().each(function (storeItem) {
                                                if (newValue.indexOf(storeItem.get('name')) > -1) {
                                                    newSortedValue.push(storeItem.get('name'));
                                                }
                                            });
                                            input.setValue(newSortedValue);
                                        }
                                    }
                                },
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                                    'valueAxisFieldSingle',
                                    {
                                        valueAxisFieldSingle: {
                                            fieldcontainer: {
                                                hidden: true,
                                                bind: {
                                                    hidden: '{actions.view || valueAxisFieldsMultiple}'
                                                }
                                            },
                                            columnWidth: 1,
                                            displayField: 'name',
                                            valueField: '_id',
                                            bind: {
                                                value: '{valueAxisFieldsSingle}',
                                                store: '{functionAttributesStore}'
                                            }
                                        }
                                    }
                                )
                            ]
                        }
                    ])
                ]
            };
        },
        getOrientationField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('componentOrientation', {
                componentOrientation: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{hiddenTypeProperty.componentOrientation}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.componentorientation,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.componentorientation'
                        }
                    },
                    bind: {
                        value: '{theComponent.componentOrientation}',
                        store: '{componentOrientationStore}'
                    }
                }
            });
        },

        getParameterDescriptionField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('description', {
                description: {
                    allowBlank: false,
                    bind: {
                        value: '{theParameter.description}',
                        readOnly: '{actions.view}'
                    },
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.labeldescription,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.labeldescription'
                        },
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onParameterNameTranslateClick'
                    }
                }
            });
        },

        getParameterEmptyTextField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('emptyText', {
                emptyText: {
                    allowBlank: true,
                    bind: {
                        value: '{theParameter.emptyText}',
                        readOnly: '{actions.view}'
                    },
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.parameters.emptytext,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.parameters.emptytext'
                        },
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onParameterEmptyTextTranslateClick'
                    }
                }
            });
        },

        getGlobalParameterField: function () {
            const me = this;
            return me.getRow([
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('useGlobalParameter', {
                    useGlobalParameter: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.useglobalparameters,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.useglobalparameters'
                            },
                            bind: {
                                hidden: '{hideGlobalParameterFields}'
                            }
                        },
                        bind: {
                            disabled: '{actions.view}',
                            value: '{theParameter.useGlobalParameter}'
                        }
                    }
                }),
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('globalParameter', {
                    globalParameter: {
                        fieldcontainer: {
                            bind: {
                                hidden: '{hideGlobalParameterFields || !theParameter.useGlobalParameter}'
                            }
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.parameters.parameter,
                        displayField: 'description',
                        valueField: '_id',
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.parameters.parameter'
                        },
                        bind: {
                            store: '{globalParametersStore}',
                            value: '{theParameter.globalParameter}',
                            disabled: '{actions.view}'
                        },
                        triggers: {
                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                        }
                    }
                })
            ]);
        },

        getRequiredField: function (parameter) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('required', {
                required: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.required,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.required'
                        }
                    },
                    bind: {
                        disabled: '{actions.view}',
                        value: '{theParameter.required}'
                    }
                }
            });
        },
        getIntegerFieldTypeField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fieldType', {
                fieldType: {
                    fieldcontainer: {}, // config for fieldcontainer
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fieldtype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fieldtype'
                    },
                    bind: {
                        store: '{integerFieldTypesStore}',
                        value: '{theParameter.fieldType}',
                        disabled: '{actions.view}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }
            });
        },

        getStringFieldTypeField: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fieldType', {
                fieldType: {
                    fieldcontainer: {}, // config for fieldcontainer
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fieldtype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fieldtype'
                    },
                    bind: {
                        store: '{stringFieldTypesStore}',
                        value: '{theParameter.fieldType}',
                        disabled: '{actions.view}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                }
            });
        },

        getAllClassesFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                {
                    defaultValue: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.fromclass,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.fromclass'
                            }
                        },
                        withClasses: true,
                        withProcesses: true,
                        bind: {
                            value: '{theParameter.defaultValue}',
                            disabled: '{actions.view}'
                        }
                    }
                },
                'defaultValue'
            );
        },
        getCqlFilterFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('parameterFilter', {
                parameterFilter: {
                    xtype: 'textarea',
                    itemId: 'parameterFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.filter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.filter'
                    },
                    hidden: true,
                    name: 'parameterFilter',
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{theParameter.cqlfilter}',
                        hidden: '{hiddenfields.cqlfilter}'
                    }
                }
            });
        },
        getStringFreeFieldContainer: function () {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('defaultValue', {
                defaultValue: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.defaultvalue'
                    },
                    bind: {
                        value: '{theParameter.defaultValue}',
                        readOnly: '{actions.view}'
                    }
                }
            });
        },
        getFilterButtons: function () {
            return {
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
                },
                columnWidth: 1,
                items: [
                    {
                        xtype: 'components-administration-toolbars-formtoolbar',
                        style: 'border:none; margin-top: 5px',
                        items: [
                            {
                                xtype: 'tool',
                                itemId: 'editFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                                tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
                                localized: {
                                    tooltip:
                                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters'
                                },
                                bind: {
                                    disabled:
                                        '{!theComponent.dataSourceName || theComponent.dataSourceType !== "class"}'
                                }
                            },
                            {
                                xtype: 'tool',
                                itemId: 'removeFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: 'cmdbuildicon-filter-remove',
                                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                                localized: {
                                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                                },
                                bind: {
                                    disabled: '{removeFilterBtnDisabled}'
                                }
                            }
                        ]
                    }
                ]
            };
        }
    }
});
