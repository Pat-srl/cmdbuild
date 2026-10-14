Ext.define('CMDBuildUI.view.administration.content.dashboards.common.FieldsetHelper', {
    singleton: true,

    requires: ['CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper'],

    getGeneralPropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: this.getGeneralPropertiesFields()
        };
    },

    getDataSourcePropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.datasourceproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.datasourceproperties'
            },
            layout: 'column',
            items: this.getDataSourcePropertiesFields(),
            hidden: true,
            bind: {
                hidden: '{isText || isTile || isSearch}'
            }
        };
    },

    getChartAndTextPropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.componenttypeproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.componenttypeproperties'
            },
            hidden: true,
            bind: {
                title: '{componentTypeFieldsetTitle}',
                hidden: '{isTable || isList || isTile || isCalendar || isSearch}'
            },
            items: this.getChartAndTextPropertiesFields()
        };
    },

    getListPropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.listproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.listproperties'
            },
            hidden: true,
            bind: {
                hidden: '{!isList}'
            },
            items: this.getListPropertiesFields()
        };
    },

    getTilePropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.tileproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.tileproperties'
            },
            hidden: true,
            bind: {
                hidden: '{!isTile}'
            },
            items: this.getTilePropertiesFields()
        };
    },

    getCalendarPropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.calendarproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.calendarproperties'
            },
            hidden: true,
            bind: {
                hidden: '{!isCalendar}'
            },
            items: this.getCalendarPropertiesFields()
        };
    },

    getSearchPropertiesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.searchproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.searchproperties'
            },
            hidden: true,
            bind: {
                hidden: '{!isSearch}'
            },
            items: this.getSearchPropertiesFields()
        };
    },

    privates: {
        getGeneralPropertiesFields: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
            const items = [
                form.getRow([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                        {
                            name: {
                                allowBlank: false,
                                bind: {
                                    value: '{theComponent.name}'
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
                                value: '{theComponent.description}'
                            },
                            fieldcontainer: {
                                userCls: 'with-tool',
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip:
                                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateComponentDescriptionClick'
                            }
                        }
                    })
                ]),
                form.getRow([
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.height,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.height'
                        },
                        bind: {
                            hidden: '{isSearch || isTile}'
                        },
                        items: [
                            {
                                columnWidth: 1,
                                xtype: 'numberfield',
                                itemId: 'headerRow_input',
                                emptyText: '500',
                                name: 'headerRow',
                                allowBlank: true,
                                bind: {
                                    value: '{theComponent.height}',
                                    minValue: '{isCalendar ? 500 : 0}',
                                    hidden: '{actions.view}'
                                }
                            },
                            {
                                columnWidth: 1,
                                xtype: 'displayfield',
                                itemId: 'headerRow_display',
                                bind: {
                                    value: '{theComponent.height}',
                                    hidden: '{!actions.view}'
                                },
                                renderer: function (value) {
                                    return Ext.isEmpty(value) ? '500' : value;
                                }
                            }
                        ]
                    },
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('autoLoad', {
                        autoLoad: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.autoload,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.autoload'
                                },
                                bind: {
                                    hidden: '{isList || isTile || isCalendar || isSearch || isText}'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.autoLoad}',
                                hidden: '{isList || isTile || isCalendar || isSearch || isText}'
                            }
                        }
                    })
                ]),
                form.getRow([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.active}'
                            }
                        }
                    }),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('hideComponentHeader', {
                        hideComponentHeader: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.hideComponentHeader,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.dashboards.hideComponentHeader'
                                },
                                bind: {
                                    hidden: '{isSearch}'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.hideComponentHeader}',
                                hidden: '{isSearch}'
                            }
                        }
                    }),
                    form.getRow(
                        [
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                'tilesPerRow',
                                {
                                    tilesPerRow: {
                                        fieldcontainer: {
                                            columnWidth: 1,
                                            allowBlank: true,
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.dashboards.tilesPerRow,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.dashboards.tilesPerRow'
                                            }
                                        },
                                        minValue: 1,
                                        columnWidth: 0.5,
                                        bind: {
                                            value: '{theComponent.tilesPerRow}'
                                        }
                                    }
                                }
                            )
                        ],
                        {
                            hidden: true,
                            bind: {
                                hidden: '{!isTile}'
                            }
                        }
                    )
                ])
            ];

            return items;
        },

        getDataSourcePropertiesFields: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
            const items = [
                // combo "data source type" - function | class or process | view
                form.getRow([form.getDataSourceTypeInput()], {
                    hidden: true,
                    bind: {
                        hidden: '{!isTable && !isList && !isCalendar}'
                    }
                }),

                // combo "function"
                form.getRow(
                    [
                        form.getRow([
                            CMDBuildUI.util.administration.helper.FieldsHelper.getFunctionsInput(
                                {
                                    dataSourceFuncktionName: {
                                        forceSelection: true,
                                        displayField: 'description',
                                        valueField: 'name',
                                        bind: {
                                            value: '{theComponent.dataSourceName}',
                                            store: '{functionsStore}'
                                        }
                                    }
                                },
                                'dataSourceFuncktionName'
                            )
                        ]),
                        form.getRow([
                            {
                                columnWidth: 1,
                                xtype: 'container',
                                itemId: 'functionparameterscontiner',
                                bind: {
                                    hidden: '{isList}'
                                }
                            }
                        ])
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: '{theComponent.dataSourceType !== "function"}'
                        }
                    }
                ),

                // combo "class or process"
                form.getRow([form.getClassesAndProcessesInput()], {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{theComponent.dataSourceType !== "{0}"}',
                            CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass
                        )
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down('#dataSourceClassName_input');
                            input.setValue('');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                input,
                                true,
                                input.up('form')
                            );
                        },
                        show: function (component, eOpts) {
                            var input = component.down('#dataSourceClassName_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                input,
                                false,
                                input.up('form')
                            );
                        }
                    }
                }),

                // combo "view"
                form.getRow(
                    [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            items: [form.getAllViewsDataSourceInput()]
                        }
                    ],
                    {
                        hidden: true,
                        bind: {
                            hidden: Ext.String.format(
                                '{theComponent.dataSourceType !== "{0}"}',
                                CMDBuildUI.model.dashboards.Component.dataSourceTypes.view
                            )
                        },
                        listeners: {
                            hide: function (component, eOpts) {
                                var input = component.down('#dataSourceViewName_input');
                                input.setValue('');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    true,
                                    input.up('form')
                                );
                            },
                            show: function (component, eOpts) {
                                var input = component.down('#dataSourceViewName_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    false,
                                    input.up('form')
                                );
                            }
                        }
                    }
                ),

                // input number "Rows limit"
                form.getRow([form.getDataSourceLimitInput()], {
                    hidden: true,
                    bind: {
                        hidden: '{!isList}'
                    }
                }),

                // buttons "filter"
                form.getRow([form.getFilterButtons()], {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{theComponent.dataSourceType !== "{0}"}',
                            CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass
                        )
                    }
                })
            ];
            return items;
        },

        getChartAndTextPropertiesFields: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
            const items = [
                form.getRow([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('useAccessibleStyle', {
                        useAccessibleStyle: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.useAccessiblestyle,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.dashboards.useAccessiblestyle'
                                },
                                bind: {
                                    hidden: '{!isPieChart}'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.useAccessibleStyle}',
                                hidden: '{!isPieChart}'
                            }
                        }
                    })
                ]),
                form.getRow([form.getShowLegendField()]),
                form.getRow([form.getOrientationField()]),
                form.getRow([form.getMinimumField(), form.getMaximumField()]),
                form.getRow([form.stepsField()]),
                form.getRow([form.getFgcolorField(), form.getBgcolorField()]),
                form.getCategoryAxisFiledset(),
                form.getValueAxisFiledset(),
                form.getRow([form.getLabelField(), form.singleSeriesField()]),
                form.getRow([form.getTextTextarea()])
            ];

            return items;
        },

        getListPropertiesFields: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
            const fieldsHelper = CMDBuildUI.util.administration.helper.FieldsHelper;

            const items = [
                form.getRow([
                    form.getListAttributesFieldForType('titleField', 'functionAttributesStoreString'),
                    form.getListAttributesFieldForType(
                        'descriptionField',
                        'functionAttributesStoreStringTextReferenceLookup'
                    )
                ]),
                form.getRow([
                    form.getListAttributesFieldForType('metaField', 'functionAttributesStoreMeta'),
                    form.getListAttributesFieldForType('tagField', 'functionAttributesStoreLookup')
                ]),

                form.getRow([
                    fieldsHelper.getCommonCheckboxInput('seeAllElements', {
                        seeAllElements: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.seeallelements,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.seeallelements'
                                },
                                bind: {
                                    disabled: '{!theComponent.dataSourceName}'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.seeAllElements}',
                                disabled: '{!theComponent.dataSourceName}'
                            },
                            listeners: {
                                change: 'onSeeAllElementsChange'
                            }
                        }
                    }),
                    fieldsHelper.getCommonComboInput('listSearchFilter', {
                        listSearchFilter: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.listSearchFilter,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.listSearchFilter'
                            },
                            queryMode: 'local',
                            displayField: 'description',
                            valueField: 'name',
                            fieldcontainer: {
                                bind: {
                                    hidden: Ext.String.format(
                                        '{!(theComponent.seeAllElements && theComponent.dataSourceType === "{0}")}',
                                        CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass
                                    )
                                }
                            },
                            bind: {
                                value: '{theComponent.listSearchFilter}',
                                store: '{listSearchFiltersFilteredStore}'
                            }
                        }
                    })
                ]),

                form.getRow([
                    fieldsHelper.getCommonCheckboxInput('linkToClass', {
                        linkToClass: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.linktoclass,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.linktoclass'
                                },
                                bind: {
                                    disabled: '{!theComponent.dataSourceName}'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theComponent.linkToClass}',
                                disabled: '{!theComponent.dataSourceName}'
                            }
                        }
                    })
                ]),
                form.getRow([
                    CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                        columnWidth: 1,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.emptylistmessage,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.emptylistmessage'
                        },
                        bind: {
                            value: '{theComponent.emptyListMessage}',
                            hidden: '{actions.view}'
                        },
                        hidden: true
                    }),
                    {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.emptylistmessage,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.emptylistmessage'
                        },
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theComponent.emptyListMessage}',
                            hidden: '{!actions.view}'
                        }
                    }
                ])
            ];

            return items;
        },

        getTilePropertiesFields: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
            const templateHelper = CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper;

            return [
                form.getRow([
                    {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        hidden: true,
                        bind: {
                            hidden: '{!isTile}'
                        },
                        listeners: {
                            hide: function (component, eOpts) {
                                var input = component.down('#layout_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    true,
                                    input.up('form')
                                );
                            },
                            show: function (component, eOpts) {
                                var input = component.down('#layout_input');
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    false,
                                    input.up('form')
                                );
                            }
                        },
                        items: [
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('layout', {
                                layout: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.layout,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.layout'
                                    },
                                    queryMode: 'local',
                                    editable: false,
                                    forceSelection: true,
                                    displayField: 'label',
                                    valueField: 'value',
                                    store: templateHelper.getLayoutStore(),
                                    bind: {
                                        value: '{theComponent.layout}'
                                    },
                                    listeners: {
                                        change: function (combo, newValue) {
                                            if (!newValue) return;

                                            let current = combo;
                                            let previewComp = null;

                                            while (current && !previewComp) {
                                                current = current.up('container') || current.up('panel');
                                                if (current) {
                                                    previewComp = current.down('#tilePreviewComponent');
                                                }
                                            }

                                            if (previewComp) {
                                                const newTpl = templateHelper.getLayoutTpl(newValue);
                                                const newLayoutCls = templateHelper.getLayoutCls(newValue);

                                                previewComp.tpl = newTpl;

                                                templateHelper.layouts.forEach(function (l) {
                                                    if (l.cls) {
                                                        previewComp.removeCls(l.cls);
                                                    }
                                                });

                                                if (newLayoutCls) {
                                                    previewComp.addCls(newLayoutCls);
                                                }

                                                if (previewComp.rendered) {
                                                    previewComp.update(previewComp.getData());
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                        ]
                    },
                    Ext.apply(
                        templateHelper.getLayoutComponent(
                            {
                                icon_font: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
                                label: 'To-do',
                                unit: 'Tickets',
                                value: '12'
                            },
                            'layout #1'
                        ),
                        {
                            itemId: 'tilePreviewComponent',
                            columnWidth: 0.25,
                            hidden: true,
                            bind: {
                                hidden: '{!theComponent.layout}'
                            }
                        }
                    )
                ]),
                {
                    xtype: 'container',
                    layout: 'anchor',
                    hidden: true,
                    bind: { hidden: '{actions.view}' },
                    columnWidth: 0.5,
                    margin: '25 0 25 0',
                    items: [
                        {
                            xtype: 'button',
                            itemId: 'addTileBtn',
                            text: CMDBuildUI.locales.Locales.administration.dashboards.tiles.addtile,
                            localized: { text: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.addtile' },
                            ui: 'administration-action-small'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    itemId: 'tilesContainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: []
                }
            ];
        },

        getSingleTileConfigurationFieldset: function () {
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;

            const items = [
                form.getRow([
                    // label
                    form.getStringInput({
                        propertyName: 'label',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.label,
                        label: {
                            allowBlank: false,
                            bind: {
                                value: '{theComponent.label}'
                            },
                            fieldcontainer: {
                                userCls: 'with-tool',
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip:
                                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateTileLabelClick'
                            }
                        }
                    }),
                    // icon
                    Ext.merge(CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('theComponent'), {
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.icon,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.icon'
                        }
                    })
                ]),
                form.getRow([
                    // Function
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('dataFunction', {
                        dataFunction: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                            },
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theComponent.dataSourceName}',
                                store: '{functionsStore}'
                            },
                            listeners: {
                                change: 'onTileFunctionChange'
                            }
                        }
                    }),
                    // Value field
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('valueField', {
                        valueField: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.valuefield,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.valuefield'
                            },
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!theComponent.dataSourceName}'
                                },
                                listeners: {
                                    hide: function (component, eOpts) {
                                        var input = component.down('#valueField_input');
                                        input.setValue('');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                            input,
                                            true,
                                            input.up('form')
                                        );
                                    },
                                    show: function (component, eOpts) {
                                        var input = component.down('#valueField_input');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                            input,
                                            false,
                                            input.up('form')
                                        );
                                    }
                                }
                            },
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theComponent.valueField}',
                                store: '{tileFunctionAttributesStore}'
                            }
                        }
                    })
                ]),
                form.getRow([
                    // Unit of measurement
                    form.getStringInput({
                        propertyName: 'unit',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.unit,
                        unit: {
                            bind: {
                                value: '{theComponent.unit}'
                            }
                        }
                    })
                ]),
                form.getRow([
                    // Color type
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('colortype', {
                        colortype: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.colortype,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.colortype'
                            },
                            queryMode: 'local',
                            editable: false,
                            forceSelection: true,
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                value: '{theComponent.colortype}',
                                store: '{tileColorTypeStore}'
                            },
                            listeners: {
                                change: 'onTileColorTypeChange'
                            }
                        }
                    }),
                    // Foreground color
                    {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        layout: 'anchor',
                        items: [
                            // Static color
                            {
                                xtype: 'fieldcontainer',
                                layout: 'column',
                                columnWidth: 0.5,
                                hidden: true,
                                bind: {
                                    hidden: '{hiddenTypeProperty.fgcolor || theComponent.colortype !== "static"}'
                                },
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.color,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.color'
                                },
                                items: [
                                    CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                                        alt: CMDBuildUI.locales.Locales.administration.dashboards.color,
                                        localized: {
                                            alt: 'CMDBuildUI.locales.Locales.administration.dashboards.color'
                                        },
                                        hidden: true,
                                        bind: {
                                            hidden: '{actions.view}',
                                            value: '{theComponent.fgcolor}'
                                        }
                                    })
                                ]
                            },
                            // Dynamic color
                            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fgcolorField', {
                                fgcolorField: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.color,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.color'
                                    },
                                    fieldcontainer: {
                                        bind: {
                                            hidden: '{theComponent.colortype !== "dynamic"}'
                                        }
                                    },
                                    queryMode: 'local',
                                    editable: false,
                                    forceSelection: true,
                                    displayField: 'description',
                                    valueField: 'name',
                                    bind: {
                                        value: '{theComponent.fgcolorField}',
                                        store: '{tileFunctionAttributesStoreString}'
                                    }
                                }
                            })
                        ]
                    }
                ]),

                form.getRow([
                    // Tile click destination
                    CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                        {
                            goto: {
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.goto,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.goto'
                                    }
                                },
                                withClasses: true,
                                withProcesses: true,
                                bind: {
                                    value: '{theComponent.goto}'
                                },
                                listeners: {
                                    change: 'onTileGoToChange'
                                }
                            }
                        },
                        'goto'
                    ),
                    // Navigation scope
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('scope', {
                        scope: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.scope,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.scope'
                            },
                            queryMode: 'local',
                            editable: false,
                            forceSelection: true,
                            displayField: 'label',
                            valueField: 'value',
                            fieldcontainer: {
                                hidden: true,
                                bind: {
                                    hidden: '{!theComponent.goto}'
                                }
                            },
                            bind: {
                                value: '{theComponent.scope}',
                                store: '{tileScopeStore}'
                            },
                            listeners: {
                                change: 'onTileScopeChange'
                            }
                        }
                    })
                ]),

                form.getRow([
                    // Filter applied when users click on the tile
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cardorfilter', {
                        cardorfilter: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.filter,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.filter'
                            },
                            queryMode: 'local',
                            displayField: 'description',
                            valueField: 'name',
                            fieldcontainer: {
                                itemId: 'cardorfilter_filtered_container',
                                hidden: true,
                                bind: {
                                    hidden: '{theComponent.scope !== "filtered"}'
                                }
                            },
                            bind: {
                                value: '{theComponent.cardorfilter}',
                                store: '{tileSearchFiltersFilteredStore}'
                            }
                        }
                    }),
                    // Card to open on tile click
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.tiles.card,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.tiles.card'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{theComponent.scope !== "selected"}'
                        },
                        items: [
                            {
                                columnWidth: 1,
                                hidden: true,
                                xtype: 'displayfield',
                                bind: {
                                    hidden: '{!actions.view}',
                                    value: '{theComponent._cardorfilterDescription}'
                                }
                            },
                            {
                                columnWidth: 1,
                                hidden: true,
                                xtype: 'fieldcontainer',
                                itemId: 'cardorfilter_selected_container',
                                items: [],
                                bind: {
                                    hidden: '{actions.view}'
                                }
                            }
                        ]
                    }
                ])
            ];

            return items;
        },

        getCalendarPropertiesFields: function () {
            const fieldsHelper = CMDBuildUI.util.administration.helper.FieldsHelper;
            const form = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;

            return [
                form.getRow([
                    fieldsHelper.getCommonComboInput('eventStartDateAttribute', {
                        eventStartDateAttribute: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventstartdate,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventstartdate'
                            },
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theComponent.eventStartDateAttribute}',
                                store: '{functionAttributesStoreDate}'
                            }
                        }
                    }),
                    fieldsHelper.getCommonComboInput('eventEndDateAttribute', {
                        eventEndDateAttribute: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventenddate,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventenddate'
                            },
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theComponent.eventEndDateAttribute}',
                                store: '{functionAttributesStoreDate}'
                            }
                        }
                    })
                ]),

                form.getRow([
                    fieldsHelper.getCommonComboInput('defaultView', {
                        defaultView: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.calendar.defaultview,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.calendar.defaultview'
                            },
                            queryMode: 'local',
                            editable: false,
                            forceSelection: true,
                            displayField: 'label',
                            valueField: 'value',
                            bind: { value: '{theComponent.defaultView}', store: '{calendarViewStore}' }
                        }
                    }),
                    fieldsHelper.getCommonComboInput('eventTitleAttribute', {
                        eventTitleAttribute: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventtitle,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.calendar.eventtitle'
                            },
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theComponent.eventTitleAttribute}',
                                store: '{functionAttributesStoreString}'
                            }
                        }
                    })
                ]),
                form.getRow([form.getFgcolorField()])
            ];
        },

        getSearchPropertiesFields: function () {
            return [
                {
                    columnWidth: 0.5,
                    layout: 'column',
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.logo,
                    localized: { fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.logo' },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'filefield',
                            itemId: 'searchLogoFile',
                            emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                            },
                            accept: '.png',
                            buttonConfig: { ui: 'administration-secondary-action-small' },
                            hidden: true,
                            bind: { hidden: '{actions.view}' },
                            autoEl: {
                                'data-testid': 'administration-dashboards-searchproperties-logo_input'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'image',
                            itemId: 'searchLogoPreview',
                            hidden: true,
                            bind: {
                                hidden: '{!theComponent.logo}',
                                src: '{theComponent.logo}',
                                style: {
                                    marginTop: '{actions.edit ? "0px" : "10px"}'
                                }
                            },
                            style: {
                                maxWidth: '100px',
                                maxHeight: '100px',
                                width: 'auto',
                                height: 'auto',
                                objectFit: 'contain',
                                borderRadius: '10px',
                                marginTop: '10px'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'searchLogoRemoveBtn',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                            cls: 'input-action-button',
                            ui: 'administration-secondary-action-small',
                            margin: '0 0 0 5',
                            hidden: true,
                            bind: {
                                hidden: '{actions.view || !theComponent.logo}'
                            },
                            autoEl: {
                                'data-testid': 'administration-dashboards-searchproperties-logo_remove'
                            }
                        }
                    ]
                }
            ];
        }
    }
});
