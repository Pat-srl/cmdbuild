Ext.define('CMDBuildUI.view.main.header.Preferences', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.main.header.PreferencesController', 'CMDBuildUI.view.main.header.PreferencesModel'],

    alias: 'widget.main-header-preferences',
    controller: 'main-header-preferences',
    viewModel: {
        type: 'main-header-preferences'
    },

    scrollable: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    autoEl: {
        'data-testid': 'main-header-preferences'
    },

    items: [
        {
            xtype: 'fieldset',
            ui: 'formpagination',
            title: CMDBuildUI.locales.Locales.main.baseconfiguration,
            localized: {
                title: 'CMDBuildUI.locales.Locales.main.baseconfiguration'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    // Language
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labellanguage,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 1,
                                    displayField: 'description',
                                    valueField: 'code',
                                    queryMode: 'local',
                                    name: 'cm_user_language',
                                    forceSelection: true,
                                    autoSelect: false,
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{languages}',
                                        value: '{values.cm_user_language}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labellanguage'
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
                                    xtype: 'allelementscombo',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 2,
                                    withClasses: true,
                                    withProcesses: true,
                                    withDashboards: true,
                                    withCustompages: true,
                                    withMenuNavTrees: true,
                                    name: 'cm_ui_startingClass',
                                    withViews: true,
                                    typePrefix: true,
                                    showTranslatedDescriptions: true,
                                    bind: {
                                        value: '{values.cm_ui_startingClass}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
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
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 3,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    name: 'cm_ui_dateFormat',
                                    forceSelection: true,
                                    autoSelect: false,
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{dateFormats}',
                                        value: '{values.cm_ui_dateFormat}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
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
                                    // Time format
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 4,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_timeFormat',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{timeFormats}',
                                        value: '{values.cm_ui_timeFormat}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
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
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    // Time format
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.timezone,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 5,
                                    displayField: 'description',
                                    valueField: '_id',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_timezone',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{timezones}',
                                        value: '{values.cm_ui_timezone}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.timezone'
                                    }
                                }
                            ]
                        },
                        {
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    // First day of week
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.firstdayofweek,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 5,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_startDay',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{startDays}',
                                        value: '{values.cm_ui_startDay}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.firstdayofweek'
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
                            // Date format
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    // Decimals separator
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 6,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_decimalsSeparator',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{decimalsSeparators}',
                                        value: '{values.cm_ui_decimalsSeparator}',
                                        validation: '{validations.decimalsSeparator}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                                    },
                                    validator: function () {
                                        this.lookupViewModel().set('values.cm_ui_decimalsSeparator', this.getValue());
                                        return true;
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
                                    // Thousands separator
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 7,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    name: 'cm_ui_thousandsSeparator',
                                    autoSelect: false,
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{thousandsSeparators}',
                                        value: '{values.cm_ui_thousandsSeparator}',
                                        validation: '{validations.thousandsSeparator}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator'
                                    },
                                    validator: function () {
                                        this.lookupViewModel().set('values.cm_ui_thousandsSeparator', this.getValue());
                                        return true;
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
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labelcsvseparator,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 8,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_preferredCsvSeparator',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{csvSeparatorsStore}',
                                        value: '{values.cm_ui_preferredCsvSeparator}',
                                        validation: '{validations.preferredOfficeSuite}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labelcsvseparator'
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
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 9,
                                    displayField: 'description',
                                    valueField: '_id',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    anyMatch: true,
                                    typeAhead: true,
                                    autoSelect: true,
                                    name: 'cm_ui_preferredFileCharset',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{preferredFileCharset}',
                                        value: '{values.cm_ui_preferredFileCharset}',
                                        validation: '{validations.preferredFileCharset}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.preferredfilecharset'
                                    },
                                    validator: function () {
                                        this.lookupViewModel().set(
                                            'values.cm_ui_preferredFileCharset',
                                            this.getValue()
                                        );
                                        return true;
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
                            columnWidth: 0.5,
                            flex: '0.5',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.preferredofficesuite,
                                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                    tabIndex: 10,
                                    displayField: 'label',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    autoSelect: false,
                                    name: 'cm_ui_preferredOfficeSuite',
                                    triggers: {
                                        clear: {
                                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                            handler: function () {
                                                this.clearValue();
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{preferredOfficeSuite}',
                                        value: '{values.cm_ui_preferredOfficeSuite}',
                                        validation: '{validations.preferredOfficeSuite}'
                                    },
                                    localized: {
                                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.preferredofficesuite'
                                    },
                                    validator: function () {
                                        this.lookupViewModel().set(
                                            'values.cm_ui_preferredOfficeSuite',
                                            this.getValue()
                                        );
                                        return true;
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'formpagination',
            title: CMDBuildUI.locales.Locales.menu.favourites,
            localized: {
                title: 'CMDBuildUI.locales.Locales.menu.favourites'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        flex: '0.5',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        layout: 'anchor'
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.showcollapsed,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.showcollapsed'
                            },
                            name: '_favouritesmenucollpased',
                            layout: 'column',
                            items: [
                                {
                                    xtype: 'checkbox',
                                    bind: {
                                        value: Ext.String.format('{values.{0}}', 'cm_ui_preferredMenu.collapsed')
                                    },
                                    listeners: {
                                        change: function (checkbox, newValue, oldValue, eOpts) {
                                            var vm = checkbox.lookupViewModel();
                                            vm.set(
                                                Ext.String.format(
                                                    'values.{0}.collapsed',
                                                    CMDBuildUI.model.users.Preference.favouritesmenu
                                                ),
                                                newValue
                                            );
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.favouritemenulocation,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.favouritemenulocation'
                            },
                            name: '_favouritesmenucollpased',
                            layout: 'anchor',
                            items: [
                                {
                                    xtype: 'combobox',
                                    displayField: 'label',
                                    valueField: 'value',
                                    bind: {
                                        store: '{favouritesMenuLocationStore}',
                                        value: Ext.String.format('{values.{0}}', 'cm_ui_preferredMenu.position')
                                    },
                                    listeners: {
                                        change: function (checkbox, newValue, oldValue, eOpts) {
                                            var vm = checkbox.lookupViewModel();
                                            vm.set(
                                                Ext.String.format(
                                                    'values.{0}.position',
                                                    CMDBuildUI.model.users.Preference.favouritesmenu
                                                ),
                                                newValue
                                            );
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'formpagination',
            title: CMDBuildUI.locales.Locales.main.preferences.notificationslabel,
            localized: {
                title: 'CMDBuildUI.locales.Locales.main.preferences.notificationslabel'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        flex: '0.5',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        layout: 'anchor'
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                            },
                            layout: 'column',
                            items: [
                                {
                                    columnWidth: 1,
                                    xtype: 'draganddropfilefield',
                                    itemId: 'iconFile',
                                    maxFileSize: 2,
                                    allowedExtensions: ['png', 'jpg', 'jpeg']
                                },
                                {
                                    items: [
                                        {
                                            xtype: 'image',
                                            height: 45,
                                            width: 45,
                                            margin: '12 10',
                                            cls: 'logo',
                                            alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                                            localized: {
                                                alt: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                                            },
                                            hidden: true,
                                            itemId: 'previewimage',
                                            bind: {
                                                hidden: '{!values._icon}',
                                                src: '{values._icon}'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                            cls: 'input-action-button',
                                            ui: 'administration-secondary-action-small',
                                            itemId: 'removeimagebtn',
                                            margin: '10 0',
                                            height: 45,
                                            hidden: true,
                                            bind: {
                                                hidden: '{!values._icon}'
                                            },
                                            listeners: {
                                                click: 'onRemoveImageBtnClick'
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
                    items: [
                        {
                            xtype: 'combobox',
                            columnWidth: 0.5,
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.notificationssound,
                            emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.notificationssound',
                                emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                            },
                            name: CMDBuildUI.model.users.Preference.notifications.soundEnabled,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            forceSelection: true,
                            anyMatch: true,
                            typeAhead: true,
                            autoSelect: true,
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: function () {
                                        this.clearValue();
                                    }
                                }
                            },
                            bind: {
                                store: '{yesOrNo}',
                                value: Ext.String.format(
                                    '{values.{0}}',
                                    CMDBuildUI.model.users.Preference.notifications.soundEnabled
                                )
                            }
                        },
                        CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                            columnWidth: 0.5,
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.notificationssoundvolume,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.notificationssoundvolume'
                            },
                            name: CMDBuildUI.model.users.Preference.notifications.soundVolume,
                            minValue: 0,
                            maxValue: 100,
                            increment: 10,
                            bind: {
                                value: Ext.String.format(
                                    '{values.{0}}',
                                    CMDBuildUI.model.users.Preference.notifications.soundVolume
                                )
                            }
                        })
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    defaults: {
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.groupemailsbystatus,
                            emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.groupemailsbystatus',
                                emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                            },
                            name: CMDBuildUI.model.users.Preference.notifications.groupEmailByStatus,
                            tabIndex: 9,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            forceSelection: true,
                            anyMatch: true,
                            typeAhead: true,
                            autoSelect: true,
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: function () {
                                        this.clearValue();
                                    }
                                }
                            },
                            bind: {
                                store: '{yesOrNo}',
                                value: Ext.String.format(
                                    '{values.{0}}',
                                    CMDBuildUI.model.users.Preference.notifications.groupEmailByStatus
                                )
                            }
                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: CMDBuildUI.locales.Locales.main.preferences.sendcancellationperiod,
                            emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.sendcancellationperiod',
                                emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                            },
                            displayField: 'label',
                            valueField: 'value',
                            forceSelection: true,
                            queryMode: 'local',
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: function () {
                                        this.clearValue();
                                    }
                                }
                            },
                            bind: {
                                store: '{defaultEmailDelay}',
                                value: Ext.String.format(
                                    '{values.{0}}',
                                    CMDBuildUI.model.users.Preference.notifications.defaultEmailDelay
                                )
                            }
                        }
                    ]
                }
            ]
        }
    ],

    buttons: [
        {
            text: CMDBuildUI.locales.Locales.common.actions.cancel,
            itemId: 'cancelbtn',
            ui: 'secondary-action-small',
            autoEl: {
                'data-testid': 'main-header-preferences-cancel'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.common.actions.save,
            formBind: true, //only enabled once the form is valid
            disabled: true,
            itemId: 'savebtn',
            ui: 'management-primary-small',
            autoEl: {
                'data-testid': 'main-header-preferences-save'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.save'
            }
        }
    ]
});
