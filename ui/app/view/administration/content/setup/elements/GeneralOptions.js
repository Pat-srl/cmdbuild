Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptions', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsController',
        'CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsModel',
        'CMDBuildUI.util.administration.helper.RendererHelper'
    ],

    alias: 'widget.administration-content-setup-elements-generaloptions',
    controller: 'administration-content-setup-elements-generaloptions',
    viewModel: { type: 'administration-content-setup-elements-generaloptions' },

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals' },
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // instance name
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.instancename,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.instancename'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            name: 'instanceName',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__instance_name}',
                                                hidden: '{!actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-instancename_display'
                                            }
                                        },
                                        {
                                            xtype: 'textfield',
                                            name: 'instanceName',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__instance_name}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-instancename_input'
                                            },
                                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'flag',
                                                'solid'
                                            ),
                                            labelToolIconQtip:
                                                CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                            labelToolIconClick: 'onTranslateClick'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    items: [
                                        {
                                            // initial page
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{_startingClass_description}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-defaultpage_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'allelementscombo',
                                                    name: 'initialPage',
                                                    valueField: '_id',
                                                    displayField: 'label',
                                                    queryMode: 'local',
                                                    typeAhead: true,
                                                    withClasses: true,
                                                    withProcesses: true,
                                                    withDashboards: true,
                                                    withCustompages: true,
                                                    withViews: true,
                                                    withMenuNavTrees: true,
                                                    typePrefix: true,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__startingclass}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-defaultpage_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // relation limit
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__relationlimit}',
                                                hidden: '{!actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-relationlimit_display'
                                            }
                                        },
                                        {
                                            xtype: 'numberfield',
                                            name: 'relationLimit',
                                            minValue: 0, //prevents negative numbers
                                            step: 10,
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__relationlimit}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-relationlimit_input'
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    items: [
                                        {
                                            // reference combo limit
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig
                                                    .referencecombolimit,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.referencecombolimit'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__referencecombolimit}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-referencecombolimit_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    name: 'referenceComboLimit',
                                                    minValue: 0, //prevents negative numbers
                                                    step: 100,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__referencecombolimit}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-referencecombolimit_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // session timeout
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__session__DOT__timeout}',
                                                hidden: '{!actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-sessiontimeout_display'
                                            }
                                        },
                                        {
                                            xtype: 'numberfield',
                                            name: 'sessionTimeout',
                                            minValue: 60, //prevents negative numbers
                                            step: 60,
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__session__DOT__timeout}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-sessiontimeout_input'
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    items: [
                                        {
                                            // ajax timeout
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeout}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-ajaxtimeout_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    name: 'ajaxTimeout',
                                                    minValue: 0, //prevents negative numbers
                                                    step: 60,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeout}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-ajaxtimeout_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__dateFormat}',
                                                hidden: '{!actions.view}'
                                            },
                                            renderer: function (value) {
                                                var me = this;
                                                var vm = me.lookupViewModel();
                                                var store = vm.getStore('dateFormats');
                                                if (store) {
                                                    var record = store.findRecord('value', value);
                                                    if (record) {
                                                        return record.get('label');
                                                    }
                                                }
                                                return value;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-dateFormat_display'
                                            }
                                        },
                                        {
                                            xtype: 'combobox',
                                            displayField: 'label',
                                            valueField: 'value',
                                            forceSelection: true,
                                            editable: false,
                                            autoSelect: false,
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            },
                                            bind: {
                                                store: '{dateFormats}',
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__dateFormat}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-dateFormat_input'
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    layout: 'anchor',
                                    items: [
                                        {
                                            xtype: 'fieldcontainer',
                                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeFormat}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    renderer: function (value) {
                                                        var me = this;
                                                        var vm = me.lookupViewModel();
                                                        var store = vm.getStore('timeFormats');
                                                        if (store) {
                                                            var record = store.findRecord('value', value);
                                                            if (record) {
                                                                return record.get('label');
                                                            }
                                                        }
                                                        return value;
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-timeFormat_display'
                                                    }
                                                },
                                                {
                                                    // Time format
                                                    xtype: 'combobox',

                                                    // padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                                    displayField: 'label',
                                                    valueField: 'value',
                                                    forceSelection: true,
                                                    editable: false,
                                                    autoSelect: false,
                                                    triggers: {
                                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                                    },
                                                    bind: {
                                                        store: '{timeFormats}',
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeFormat}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-timeFormat_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.firstdayofweek,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.firstdayofweek'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: { value: '{_startDay_description}', hidden: '{!actions.view}' },
                                            renderer: function (value) {
                                                var me = this;
                                                var vm = me.lookupViewModel();
                                                if (!value) {
                                                    vm.bind(
                                                        {
                                                            bindTo: {
                                                                store: '{startDaysStore}',
                                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__startDay}'
                                                            },
                                                            single: true
                                                        },
                                                        function (data) {
                                                            var record = data.store.findRecord('value', data.value);
                                                            if (record) {
                                                                vm.set('_startDay_description', record.get('label'));
                                                            }
                                                        }
                                                    );
                                                }
                                                return value;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-startDay_display'
                                            }
                                        },
                                        {
                                            xtype: 'combobox',
                                            displayField: 'label',
                                            valueField: 'value',
                                            forceSelection: true,
                                            editable: false,
                                            autoSelect: false,
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            },
                                            bind: {
                                                store: '{startDaysStore}',
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__startDay}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-startDay_input'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator}',
                                                hidden: '{!actions.view}'
                                            },
                                            renderer: function (value) {
                                                var me = this;
                                                var vm = me.lookupViewModel();
                                                var store = vm.getStore('decimalsSeparators');
                                                if (store) {
                                                    var record = store.findRecord('value', value);
                                                    if (record) {
                                                        return record.get('label');
                                                    }
                                                }
                                                return value;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-decimalsSeparator_display'
                                            }
                                        },
                                        {
                                            // Time format
                                            xtype: 'combobox',
                                            displayField: 'label',
                                            valueField: 'value',
                                            forceSelection: true,
                                            editable: false,
                                            autoSelect: false,
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            },
                                            bind: {
                                                store: '{decimalsSeparators}',
                                                validation: '{validations.decimalsSeparator}',
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator}',
                                                hidden: '{actions.view}'
                                            },
                                            validator: function (label) {
                                                var vm = this.lookupViewModel();
                                                var value = this.getValue();
                                                vm.get('theSetup').org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator =
                                                    value;
                                                vm.set('values.decimalsSeparator', value);
                                                return true;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-decimalsSeparator_input'
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator}',
                                                hidden: '{!actions.view}'
                                            },
                                            renderer: function (value) {
                                                var me = this;
                                                var vm = me.lookupViewModel();
                                                var store = vm.getStore('thousandsSeparators');
                                                if (store) {
                                                    var record = store.findRecord('value', value);
                                                    if (record) {
                                                        return record.get('label');
                                                    }
                                                }
                                                return value;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-thousandsSeparator_display'
                                            }
                                        },
                                        {
                                            xtype: 'combobox',

                                            displayField: 'label',
                                            valueField: 'value',
                                            forceSelection: true,
                                            editable: false,
                                            autoSelect: false,
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            },
                                            bind: {
                                                store: '{thousandsSeparators}',
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator}',
                                                hidden: '{actions.view}',
                                                validation: '{validations.thousandsSeparator}'
                                            },

                                            validator: function (label) {
                                                var vm = this.lookupViewModel();
                                                var value = this.getValue();
                                                vm.get(
                                                    'theSetup'
                                                ).org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator = value;
                                                vm.set('values.thousandsSeparator', value);
                                                return true;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-thousandsSeparator_input'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',

                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.csvseparator,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.csvseparator'
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    bind: { value: '{_preferredCsvSeparator_description}', hidden: '{!actions.view}' },
                                    renderer: function (value) {
                                        var me = this;
                                        var vm = me.lookupViewModel();
                                        if (Ext.isEmpty(value)) {
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        separator:
                                                            '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredCsvSeparator}',
                                                        store: '{csvSeparatorsStore}'
                                                    },
                                                    deep: true,
                                                    single: true
                                                },
                                                function (data) {
                                                    if (data.store) {
                                                        var record = data.store.findRecord('value', data.separator);
                                                        if (record) {
                                                            vm.set(
                                                                '_preferredCsvSeparator_description',
                                                                record.get('label')
                                                            );
                                                        }
                                                    }
                                                }
                                            );
                                        }

                                        return value;
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-csvseparator_display'
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'preferredCsvSeparator',
                                    valueField: 'value',
                                    displayField: 'label',
                                    queryMode: 'local',
                                    typeAhead: true,
                                    allowBlank: false,
                                    forceSelection: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredCsvSeparator}',
                                        store: '{csvSeparatorsStore}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-csvseparator_input'
                                    }
                                }
                            ]
                        },
                        {
                            // empty right side
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            flex: '0.5',
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.preferredfilecharset,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.preferredfilecharset'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredFileCharset}',
                                                hidden: '{!actions.view}'
                                            },
                                            renderer: function (value) {
                                                var me = this;
                                                var vm = me.lookupViewModel();
                                                var store = vm.getStore('preferredFileCharsetStore');
                                                if (store) {
                                                    var record = store.findRecord('_id', value);
                                                    if (record) {
                                                        return record.get('description');
                                                    }
                                                }
                                                return value;
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-preferredFileCharset_display'
                                            }
                                        },
                                        {
                                            xtype: 'combobox',
                                            name: 'preferredFileCharset',
                                            valueField: '_id',
                                            displayField: 'description',
                                            queryMode: 'local',
                                            allowBlank: false,
                                            forceSelection: true,
                                            typeAhead: true,
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredFileCharset}',
                                                store: '{preferredFileCharsetStore}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-preferredFileCharset_input'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',

                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.preferredofficesuite,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.preferredofficesuite'
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredOfficeSuite}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer: function (value) {
                                        var me = this;
                                        var vm = me.lookupViewModel();
                                        var store = vm.getStore('preferredOfficeSuite');
                                        if (store) {
                                            var record = store.findRecord('value', value);
                                            if (record) {
                                                return record.get('label');
                                            }
                                        }
                                        return value;
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-preferredOfficeSuite_display'
                                    }
                                },
                                {
                                    xtype: 'combo',
                                    name: 'preferredOfficeSuite',
                                    valueField: 'value',
                                    displayField: 'label',
                                    queryMode: 'local',
                                    typeAhead: true,
                                    allowBlank: false,
                                    forceSelection: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredOfficeSuite}',
                                        store: '{preferredOfficeSuite}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-preferredOfficeSuite_input'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // detail window width
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.detailwindowwidth,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.detailwindowwidth'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__detailWindow__DOT__width}',
                                                hidden: '{!actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-detailwindowwidth_display'
                                            }
                                        },
                                        {
                                            xtype: 'numberfield',
                                            name: 'detailWindowWidth',
                                            minValue: 0,
                                            maxValue: 100,
                                            step: 5,
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__detailWindow__DOT__width}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-detailwindowwidth_input'
                                            }
                                        }
                                    ]
                                },
                                {
                                    // popup window height
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    items: [
                                        {
                                            // detail window height
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig
                                                    .detailwindowheight,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.detailwindowheight'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__detailWindow__DOT__height}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-detailwindowheight_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    name: 'detailWindowHeight',
                                                    minValue: 0,
                                                    maxValue: 100,
                                                    step: 5,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__detailWindow__DOT__height}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-detailwindowheight_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // popup window width
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.popupwidth,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.popupwidth'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__popupWindow__DOT__width}',
                                                hidden: '{!actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-popupwidth_display'
                                            }
                                        },
                                        {
                                            xtype: 'numberfield',
                                            name: 'detailPopupWidth',
                                            minValue: 0,
                                            maxValue: 100,
                                            step: 5,
                                            bind: {
                                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__popupWindow__DOT__width}',
                                                hidden: '{actions.view}'
                                            },
                                            autoEl: {
                                                'data-testid':
                                                    'administration-systemconfig-generalproperties-popupwidth_input'
                                            }
                                        }
                                    ]
                                },
                                {
                                    // popup window height
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                                    items: [
                                        {
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig.popupheight,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.popupheight'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__popupWindow__DOT__height}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-popupheight_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    name: 'detailPopupHeight',
                                                    minValue: 0,
                                                    maxValue: 100,
                                                    step: 5,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__popupWindow__DOT__height}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-popupheight_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            layout: 'column',
                            items: [
                                {
                                    // popup window height
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.5,
                                    flex: '0.5',
                                    layout: 'anchor',
                                    items: [
                                        {
                                            xtype: 'fieldcontainer',
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig.inlinecardheight,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.systemconfig.inlinecardheight'
                                            },
                                            items: [
                                                {
                                                    xtype: 'displayfield',
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__inlineCard__DOT__height}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-inlinecardheight_display'
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    name: 'inlineCardHeight',
                                                    minValue: 0,
                                                    maxValue: 100,
                                                    step: 5,
                                                    bind: {
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__inlineCard__DOT__height}',
                                                        hidden: '{actions.view}'
                                                    },
                                                    autoEl: {
                                                        'data-testid':
                                                            'administration-systemconfig-generalproperties-inlinecardheight_input'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            // keepFilterOnUpdatedCard
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.keepfilteronupdatedcard,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.keepfilteronupdatedcard'
                            },

                            items: [
                                {
                                    xtype: 'checkbox',
                                    name: 'keepFilterOnUpdatedCard',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__keepFilterOnUpdatedCard}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-keepfilteronupdatedcard_input'
                                    }
                                }
                            ]
                        },
                        {
                            // empty right side
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    itemId: 'fulltext',
                                    valueField: 'value',
                                    displayField: 'label',
                                    allowBlank: false,
                                    forceSelection: true,
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__fulltextsearch__DOT__enabled}',
                                        store: '{fulltextStore}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-fulltext_input'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__fulltextsearch__DOT__enabled}',
                                        hidden: '{!actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-fulltext_display'
                                    },
                                    renderer: function (value) {
                                        return CMDBuildUI.util.administration.helper.RendererHelper.getSearchfieldInGridsOptionsLabel(
                                            value
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
            // Relations navigation fieldset
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.relationsnavigation,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.relationsnavigation' },

            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    // Open subclass
                                    xtype: 'checkbox',
                                    id: 'openSubclassCheckBox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.opensubclass,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.opensubclass'
                                    },
                                    name: 'opensubclass',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__redirectTo__DOT__subclass__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-opensubclass'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: { paddingLeft: '15px' },
                            items: [
                                {
                                    // Open in details window
                                    xtype: 'checkbox',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.opendetailswindow,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.opendetailswindow'
                                    },
                                    name: 'opendetailswindow',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__redirectTo__DOT__view__DOT__enabled}',
                                        readOnly: '{isDetailsWindowReadOnly}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-opendetailswindow'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.lockmanagement,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.lockmanagement' },

            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'lockcardenabled',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__cardlock__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-cardlockenabled_input'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: { paddingLeft: '15px' },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.showcardlockerusername,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.showcardlockerusername'
                                    },
                                    name: 'lockcarduservisible',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__cardlock__DOT__showuser}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-showcardlockerusername_input'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'displayfield',
                                    name: 'lockcardtimeout',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime'
                                    },
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcardtimeout}',
                                        hidden: '{!actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-maxlocktime_input'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    name: 'lockcardtimeout',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime'
                                    },
                                    minValue: 0, //prevents negative numbers
                                    step: 10,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcardtimeout}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-lockcardtimeout_input'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.gridautorefresh,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.gridautorefresh' },
            hidden: true, // TODO: Temporarily disabled (Fabio 5/11/18)
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'lockcardenabled',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefresh}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-gridAutorefresh_input'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: { paddingLeft: '15px' },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    name: 'lockcardtimeout',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.frequency,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.frequency'
                                    },
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefreshFrequency}',
                                        hidden: '{!actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-gridAutorefreshFrequency_display'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    name: 'lockcardtimeout',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.frequency,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.frequency'
                                    },
                                    minValue: 0, //prevents negative numbers
                                    step: 10,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefreshFrequency}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-gridAutorefreshFrequency_input'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.webservices,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.webservices' },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.enabledwebsockets,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.enabledwebsockets'
                                    },
                                    name: 'enablewebsocket',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    invalidCls: Ext.baseCSSPrefix + 'form-invalid',
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-websocketenabled_input'
                                    },
                                    getErrors: function (value) {
                                        var me = this,
                                            errors = [],
                                            vm = me.lookupViewModel();
                                        if (
                                            vm.get('chatEnabled') &&
                                            !vm.get(
                                                'theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__enabled'
                                            )
                                        ) {
                                            errors.push(
                                                CMDBuildUI.locales.Locales.administration.systemconfig
                                                    .chatactiveandwebsocketsenabledwarning
                                            );
                                        }
                                        if (errors.length) {
                                            me.displayEl.addCls('x-form-invalid-field');
                                        } else {
                                            me.displayEl.removeCls('x-form-invalid-field');
                                        }
                                        return errors;
                                    },
                                    listeners: {
                                        change: function () {
                                            var me = this;
                                            Ext.asap(function () {
                                                me.getErrors();
                                                me.up('form').isValid();
                                            });
                                        }
                                    }
                                }
                            ]
                        },

                        {
                            columnWidth: 0.5,
                            style: { paddingLeft: '15px' },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    name: 'pollinginterval',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pollinginterval,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.pollinginterval'
                                    },
                                    bind: { value: '{pollingIntervalDisplay}', hidden: '{!actions.view}' },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-pollingintervaldisplay_input'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    name: 'pollinginterval',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pollinginterval,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.pollinginterval'
                                    },
                                    minValue: 5,
                                    step: 1,
                                    value: 5,
                                    bind: {
                                        value: '{pollingIntervalMinutes}',
                                        hidden: '{actions.view}',
                                        disabled: '{isPollingDisabled}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-pollingintervalfield_input'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enablerestv2,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.enablerestv2'
                                    },
                                    name: 'enablerestv2',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__services__DOT__rest__DOT__v2__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-restv2enabled_input'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.bulkactions,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.bulkactions' },
            hidden: false,
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkedit,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkedit'
                            },

                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'combo',
                                    itemId: 'bulkEdit',
                                    valueField: 'value',
                                    displayField: 'label',
                                    allowBlank: false,
                                    forceSelection: true,
                                    hidden: true,
                                    bind: {
                                        hidden: '{actions.view}',
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__card__DOT__bulk__DOT__update__DOT__enabled__DOT__default}',
                                        store: '{defaultForCardEditStore}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforcardsbulkedit_input'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    name: 'lockcardtimeout',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__card__DOT__bulk__DOT__update__DOT__enabled__DOT__default}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer:
                                        CMDBuildUI.util.administration.helper.RendererHelper.getBulkSettingsComboLabel,
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforcardsbulkedit_display'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            xtype: 'fieldcontainer',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkdeletion,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkdeletion'
                            },

                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'combo',
                                    itemId: 'bulkDeletion',
                                    valueField: 'value',
                                    displayField: 'label',
                                    allowBlank: false,
                                    forceSelection: true,
                                    hidden: true,
                                    bind: {
                                        hidden: '{actions.view}',
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__card__DOT__bulk__DOT__delete__DOT__enabled__DOT__default}',
                                        store: '{defaultForCardDeletionStore}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforcardsbulkdeletion_input'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__card__DOT__bulk__DOT__delete__DOT__enabled__DOT__default}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer:
                                        CMDBuildUI.util.administration.helper.RendererHelper.getBulkSettingsComboLabel,
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforcardsbulkdeletion_display'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.systemconfig.defaultforworkflowbuldabort,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforworkflowbuldabort'
                            },

                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'combo',
                                    itemId: 'bulkAbort',
                                    valueField: 'value',
                                    displayField: 'label',
                                    hidden: true,
                                    allowBlank: false,
                                    forceSelection: true,
                                    bind: {
                                        hidden: '{actions.view}',
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__bulk__DOT__abort__DOT__enabled__DOT__default}',
                                        store: '{defaultForBulkAbortStore}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforworkflowbulkabort_input'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    name: 'lockcardtimeout',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__bulk__DOT__abort__DOT__enabled__DOT__default}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer:
                                        CMDBuildUI.util.administration.helper.RendererHelper.getBulkSettingsComboLabel,
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-defaultforworkflowbuldabort_display'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.companylogo,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.companylogo' },
            hidden: false,
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.logo,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.logo' },

                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'filefield',
                                    itemId: 'iconFile',
                                    emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                                    localized: {
                                        emptyText:
                                            'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                                    },
                                    accept: '.png',
                                    buttonConfig: { ui: 'administration-secondary-action-small' },
                                    hidden: true,
                                    bind: { hidden: '{actions.view}' },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-companylogo_input'
                                    }
                                },
                                {
                                    columnWidth: 0.5,
                                    xtype: 'previewimage',
                                    hidden: true,
                                    src: 'logo',
                                    alt: CMDBuildUI.locales.Locales.administration.systemconfig.logo,
                                    localized: { alt: 'CMDBuildUI.locales.Locales.administration.systemconfig.logo' },
                                    resetKey: 'theSetup.org__DOT__cmdbuild__DOT__core__DOT__companyLogo',
                                    itemId: 'customerLogoContainer',
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-companylogo_display'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.chat,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.chat' },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enablechat,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enablechat'
                            },

                            items: [
                                {
                                    xtype: 'checkbox',
                                    name: 'chatenabled',
                                    bind: { value: '{chatEnabled}', readOnly: '{actions.view}' },
                                    invalidCls: Ext.baseCSSPrefix + 'form-invalid',
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-enablechat_input'
                                    },

                                    getErrors: function (value) {
                                        var me = this,
                                            errors = [],
                                            vm = me.lookupViewModel();
                                        if (
                                            value &&
                                            !vm.get(
                                                'theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__enabled'
                                            )
                                        ) {
                                            errors.push(
                                                CMDBuildUI.locales.Locales.administration.systemconfig
                                                    .chatactiveandwebsocketsenabledwarning
                                            );
                                        }
                                        if (errors.length) {
                                            me.displayEl.addCls('x-form-invalid-field');
                                        } else {
                                            me.displayEl.removeCls('x-form-invalid-field');
                                        }
                                        return errors;
                                    },
                                    listeners: {
                                        change: function () {
                                            var me = this;
                                            Ext.asap(function () {
                                                me.getErrors();
                                                me.up('form').isValid();
                                            });
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.filterusersbytenant,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.filterusersbytenant'
                            },
                            hidden: true,
                            bind: { hidden: '{!chatEnabled}' },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    name: 'filterusersbytenant',
                                    bind: { value: '{chatMultitenantMode}', readOnly: '{actions.view}' },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-filterusersbytenant_input'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            layout: { type: 'vbox', align: 'stretch' },
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.notificationssound,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.notificationssound'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    name: 'notificationSound',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__notifications__DOT__sound__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-notificationsoundenabled'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.inactiveusers,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.inactiveusers' },
            hidden: false,
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disableinactiveusers,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.disableinactiveusers'
                            },
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod !== null}',
                                readOnly: '{actions.view}'
                            },
                            listeners: {
                                change: function (checkbox, newValue, oldValue) {
                                    var vm = checkbox.lookupViewModel();
                                    if (newValue === false) {
                                        vm.set(
                                            'theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod',
                                            null
                                        );
                                    } else if (
                                        !vm.get(
                                            'theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod'
                                        )
                                    ) {
                                        vm.set(
                                            'theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod',
                                            '0M'
                                        );
                                    }
                                }
                            },
                            autoEl: {
                                'data-testid':
                                    'administration-systemconfig-generalproperties-disableinactiveusers_input'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            style: { paddingLeft: '15px' },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.monthsofinactivity,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.monthsofinactivity'
                                    },
                                    hidden: true,
                                    bind: {
                                        value: '{inactiveusers_value}',
                                        hidden: '{actions.edit || theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod === null}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-monthsofinactivity_display'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    reference: 'inactiveusers',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.monthsofinactivity,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.monthsofinactivity'
                                    },
                                    minValue: 0, //prevents negative numbers
                                    step: 1,
                                    hidden: true,
                                    validator: function (value) {
                                        var vm = this.lookupViewModel();
                                        value = vm.get('inactiveusers_value');

                                        if (this.isHidden()) {
                                            return true;
                                        }

                                        if (value === null) {
                                            return false;
                                        }
                                        return true;
                                    },
                                    bind: {
                                        value: '{inactiveusers_value}',
                                        hidden: '{actions.view || theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod === null}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-monthsofinactivity_input'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
