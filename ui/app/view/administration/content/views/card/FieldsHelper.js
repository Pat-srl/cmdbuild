Ext.define('CMDBuildUI.view.administration.content.views.card.FieldsHelper', {
    requires: ['Ext.util.Format', 'CMDBuildUI.util.helper.FieldsHelper'],
    singleton: true,

    getNameInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
            {
                name: {
                    allowBlank: false,
                    bind: {
                        value: '{theViewFilter.name}'
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
                    value: '{theViewFilter.description}'
                },
                fieldcontainer: {
                    userCls: 'with-tool',
                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClick'
                }
            }
        });
    },

    getFunctionsInput: function () {
        return {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!isSqlType}'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getFunctionsInput(
                    {
                        sourceFunction: {
                            fieldcontainer: {
                                allowBlank: false
                            },
                            allowBlank: false,
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theViewFilter.sourceFunction}',
                                store: '{getFunctionsStore}'
                            },
                            listeners: {
                                render: function (combo, e, eOpts) {
                                    var setHidden =
                                        combo.lookupViewModel().get('theViewFilter.type') !==
                                        CMDBuildUI.model.views.View.types.sql;
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                        combo,
                                        setHidden,
                                        combo.up('form')
                                    );
                                }
                            }
                        }
                    },
                    'sourceFunction'
                )
            ]
        };
    },

    getAllClassesInput: function () {
        return {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!isFilterType}'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                    {
                        sourceClassName: {
                            fieldcontainer: {
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass'
                                }
                            },
                            allowBlank: true,
                            withClasses: true,
                            withProcesses: true,
                            bind: {
                                value: '{theViewFilter.sourceClassName}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue) {
                                    if (oldValue) {
                                        combo.lookupViewModel().get('theViewFilter').set('filter', '');
                                    }
                                }
                            }
                        }
                    },
                    'sourceClassName'
                )
            ]
        };
    },

    getFakeFilterTextInput: function () {
        return {
            xtype: 'textareafield',
            name: 'filter_input',
            bind: {
                value: '{theViewFilter.filter}'
            },
            listeners: {
                render: function (fieldcontainer, e, eOpts) {
                    var setHidden =
                        fieldcontainer.lookupViewModel().get('theViewFilter.type') ===
                        CMDBuildUI.model.views.View.types.sql;
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                        fieldcontainer,
                        setHidden,
                        fieldcontainer.up('form')
                    );
                }
            }
        };
    },

    getScheduleDefinitions: function () {
        return {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!isScheduleType}'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('calendarDefinition', {
                    calendarDefinition: {
                        fieldcontainer: {
                            fieldLabel: 'Calendar definition'
                        },
                        displayField: '_comboDescription',
                        valueField: '_id',
                        bind: {
                            value: '{theViewFilter.sourceCalendarDefinition}',
                            store: '{calendarDefinitionsStore}'
                        },
                        listeners: {
                            render: function (combo, e, eOpts) {
                                var setHidden =
                                    combo.lookupViewModel().get('theViewFilter.type') !==
                                    CMDBuildUI.model.views.View.types.calendar;
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    combo,
                                    setHidden,
                                    combo.up('form')
                                );
                            }
                        }
                    }
                })
            ]
        };
    },

    getFiltersTools: function () {
        return {
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
            },
            columnWidth: 1,
            bind: {
                hidden: '{isSqlType}'
            },
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    style: 'border:none; margin-top: 5px',
                    items: [
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'tool',
                            align: 'right',
                            itemId: 'editFilterBtn',
                            cls: 'administration-tool margin-right5',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
                            autoEl: {
                                'data-testid': 'administration-searchfilter-tool-editfilterbtn'
                            },
                            bind: {
                                disabled: '{filterDisabled}'
                            }
                        },
                        {
                            xtype: 'tool',
                            align: 'right',
                            itemId: 'removeFilterBtn',
                            cls: 'administration-tool margin-right5',
                            iconCls: 'cmdbuildicon-filter-remove',
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                            autoEl: {
                                'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                            },
                            bind: {
                                disabled: '{removeFilterDisabled}'
                            }
                        },
                        {
                            // this field is hidden and used only for form validation!!
                            xtype: 'fieldcontainer',
                            hidden: true,
                            allowBlank: false,
                            itemId: 'fakefiltertextinput_container',
                            items: [
                                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getFakeFilterTextInput()
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getIconInput: function () {
        return {
            layout: 'column',
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('theViewFilter')]
        };
    },

    getActiveInput: function () {
        return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
            active: {
                bind: {
                    value: '{theViewFilter.active}'
                }
            }
        });
    }
});
