Ext.define('CMDBuildUI.view.administration.components.viewfilters.card.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.viewfilters.card.FormController',
        'CMDBuildUI.view.administration.components.viewfilters.card.FormModel',
        'CMDBuildUI.view.administration.components.viewfilters.card.FieldsHelper'
    ],
    alias: 'widget.administration-components-viewfilters-card-form',
    controller: 'administration-components-viewfilters-card-form',
    viewModel: {
        type: 'administration-components-viewfilters-card-form'
    },
    modelValidation: true,
    config: {
        theViewFilter: null
    },

    bind: {
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },

    layout: {
        type: 'fit'
    },
    padding: '0',
    items: [
        {
            ui: 'administration-formpagination',
            modelValidation: true,
            xtype: 'fieldset',
            layout: 'hbox',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            forceFit: true,
            items: [
                {
                    flex: 1,
                    xtype: 'panel',
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
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
                        ),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                            description: {
                                allowBlank: false,
                                bind: {
                                    value: '{theViewFilter.description}'
                                },
                                fieldcontainer: {
                                    userCls: 'with-tool',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateClick'
                                }
                            }
                        }),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                            {
                                target: {
                                    fieldcontainer: {
                                        allowBlank: false,
                                        fieldLabel:
                                            CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels
                                                .targetclass,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass'
                                        }
                                    },
                                    allowBlank: false,
                                    withClasses: true,
                                    withProcesses: true,
                                    bind: {
                                        value: '{theViewFilter.target}'
                                    }
                                }
                            },
                            'target'
                        ),

                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                            active: {
                                bind: {
                                    value: '{theViewFilter.active}'
                                }
                            }
                        }),
                        {
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters, // Filters
                            allowBlank: false,
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
                                        {
                                            xtype: 'tbfill'
                                        },
                                        {
                                            xtype: 'tool',
                                            align: 'right',
                                            itemId: 'editFilterBtn',
                                            cls: 'administration-tool margin-right5',
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                                            tooltip:
                                                CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips
                                                    .filters,
                                            localized: {
                                                tooltip:
                                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters'
                                            },
                                            autoEl: {
                                                'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                            },
                                            bind: {
                                                disabled: '{!theViewFilter.target}'
                                            }
                                        },
                                        {
                                            xtype: 'tool',
                                            align: 'right',
                                            itemId: 'removeFilterBtn',
                                            cls: 'administration-tool margin-right5',
                                            iconCls: 'cmdbuildicon-filter-remove',
                                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                                            localized: {
                                                tooltip:
                                                    'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                                            },
                                            autoEl: {
                                                'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                            },
                                            bind: {
                                                disabled: '{removeFilterBtnDisabled}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    layout: {
                        type: 'vbox',
                        align: 'stretch',
                        vertical: true
                    },
                    flex: 1,
                    scrollable: 'y',
                    items: [
                        {
                            listeners: {
                                afterlayout: function () {
                                    this.setHeight(this.up().up().getHeight() - 60);
                                }
                            },
                            xtype: 'grid',
                            scrollable: 'y',

                            bind: {
                                store: '{rolesStore}'
                            },
                            viewConfig: {
                                markDirty: false
                            },
                            forceFit: true,
                            autoHeight: true,
                            sortable: false,
                            sealedColumns: false,
                            sortableColumns: false,
                            enableColumnHide: false,
                            enableColumnMove: false,
                            enableColumnResize: false,
                            menuDisabled: true,
                            stopSelect: true,
                            columns: [
                                {
                                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.defaultforgroup,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.defaultforgroup'
                                    },
                                    dataIndex: 'description',
                                    flex: 1,
                                    align: 'left'
                                },
                                {
                                    xtype: 'checkcolumn',
                                    dataIndex: 'active',
                                    align: 'center',
                                    bind: {
                                        disabled: '{actions.view}'
                                    },
                                    listeners: {
                                        checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                                            var vm = check.up('form').getViewModel();
                                            var currentGroups = vm.get('theViewFilter.userGroups') || [];
                                            switch (checked) {
                                                case true:
                                                    currentGroups.push({
                                                        _id: record.get('_id'),
                                                        name: record.get('name'),
                                                        description: record.get('description')
                                                    });
                                                    break;
                                                case false:
                                                    var index = currentGroups
                                                        .map(function (group) {
                                                            if (group.isModel) {
                                                                return group.get('_id');
                                                            }
                                                            return group._id;
                                                        })
                                                        .indexOf(record.get('_id'));
                                                    currentGroups.splice(index, 1);
                                                    break;
                                            }
                                            vm.set('userGroups', currentGroups);
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            bind: {
                hidden: '{hideForm}'
            }
        }
    ],

    dockedItems: [
        {
            dock: 'top',
            xtype: 'container',
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                        {
                            edit: true,
                            delete: true,
                            activeToggle: true
                        },
                        'searchfilter',
                        'theViewFilter'
                    ),
                    bind: {
                        hidden: '{formtoolbarHidden}'
                    }
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
            items: [
                {
                    xtype: 'component',
                    flex: 1
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    ui: 'administration-secondary-action-small',
                    itemId: 'cancelbtn',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                    },
                    handler: 'onCancelBtnClick'
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                    ui: 'administration-action-small',
                    itemId: 'savebtn',

                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
                    },
                    bind: {
                        disabled: '{!theViewFilter.configuration}'
                    },
                    handler: 'onSaveBtnClick'
                }
            ]
        }
    ],
    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        this.callParent(arguments);
    },
    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
        }
    }
});
