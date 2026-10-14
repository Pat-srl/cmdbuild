Ext.define('CMDBuildUI.view.administration.content.reports.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.reports.ViewController',
        'CMDBuildUI.view.administration.content.reports.ViewModel'
    ],
    alias: 'widget.administration-content-reports-view',
    controller: 'administration-content-reports-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-reports-view'
    },
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            xtype: 'panel',
            region: 'center',
            scrollable: 'y',
            hidden: true,
            bind: {
                hidden: '{hideForm}'
            },
            items: [
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                    },
                    items: [
                        {
                            layout: 'column',
                            items: [
                                {
                                    layout: 'column',
                                    columnWidth: 1,
                                    defaults: {
                                        layout: 'column',
                                        columnWidth: 0.5,
                                        xtype: 'fieldcontainer'
                                    },
                                    items: [
                                        {
                                            padding: '0 15 0 0',
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name, // Name
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                            },
                                            allowBlank: false,
                                            items: [
                                                {
                                                    /********************* theReport.code **********************/
                                                    xtype: 'displayfield',
                                                    hidden: true,
                                                    name: 'code',
                                                    bind: {
                                                        value: '{theReport.code}',
                                                        hidden: '{!actions.view}'
                                                    }
                                                },
                                                {
                                                    /********************* theReport.code **********************/
                                                    xtype: 'textfield',
                                                    vtype: 'nameInputValidation',
                                                    hidden: true,
                                                    allowBlank: false,
                                                    maxLength: 20,
                                                    name: 'code',
                                                    bind: {
                                                        value: '{theReport.code}',
                                                        hidden: '{actions.view}',
                                                        disabled: '{actions.edit}'
                                                    },
                                                    listeners: {
                                                        change: function (input, newVal, oldVal) {
                                                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                                                input,
                                                                newVal,
                                                                oldVal,
                                                                '[name="description"]'
                                                            );
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            columnWidth: 0.5,
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.common.labels.description,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.common.labels.description',
                                                labelToolIconQtip:
                                                    'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                                            },
                                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'flag',
                                                'solid'
                                            ),
                                            labelToolIconQtip:
                                                CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                            labelToolIconClick: 'onTranslateClick',
                                            allowBlank: false,
                                            items: [
                                                {
                                                    /********************* theReport.description **********************/
                                                    xtype: 'displayfield',
                                                    hidden: true,
                                                    name: 'description',
                                                    bind: {
                                                        value: '{theReport.description}',
                                                        hidden: '{!actions.view}'
                                                    }
                                                },
                                                {
                                                    /********************* theReport.description **********************/
                                                    xtype: 'textfield',
                                                    hidden: true,
                                                    allowBlank: false,
                                                    name: 'description',
                                                    bind: {
                                                        value: '{theReport.description}',
                                                        hidden: '{actions.view}'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                {
                                    layout: 'column',
                                    columnWidth: 1,
                                    defaults: {
                                        layout: 'column',
                                        columnWidth: 0.5
                                    },
                                    items: [
                                        {
                                            padding: '0 15 0 0',
                                            items: [
                                                {
                                                    /********************* theReport.config.processing **********************/
                                                    xtype: 'displayfield',
                                                    fieldLabel:
                                                        CMDBuildUI.locales.Locales.administration.reports.fieldlabels
                                                            .executionmode,
                                                    localized: {
                                                        fieldLabel:
                                                            'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.executionmode'
                                                    },
                                                    hidden: true,
                                                    bind: {
                                                        value: '{theReport.config___processing}',
                                                        hidden: '{!actions.view}'
                                                    },
                                                    renderer: function (value) {
                                                        if (value) {
                                                            var vm = this.lookupViewModel();
                                                            var store = vm.get('processingModesStore');
                                                            if (store) {
                                                                var record = store.findRecord('value', value);
                                                                if (record) {
                                                                    return record.get('label');
                                                                }
                                                            }
                                                        }
                                                        return value;
                                                    }
                                                },
                                                {
                                                    /********************* theReport.config.processing **********************/
                                                    xtype: 'combobox',
                                                    fieldLabel:
                                                        CMDBuildUI.locales.Locales.administration.reports.fieldlabels
                                                            .executionmode,
                                                    localized: {
                                                        fieldLabel:
                                                            'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.executionmode'
                                                    },
                                                    hidden: true,
                                                    allowBlank: false,
                                                    displayField: 'label',
                                                    valueField: 'value',
                                                    bind: {
                                                        store: '{processingModesStore}',
                                                        value: '{theReport.config___processing}',
                                                        hidden: '{actions.view}'
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                Ext.apply(
                                    CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('theReport'),
                                    { columnWidth: 0.5 }
                                )
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                {
                                    layout: 'column',
                                    columnWidth: 1,
                                    defaults: {
                                        layout: 'column',
                                        columnWidth: 0.5
                                    },
                                    items: [
                                        {
                                            items: [
                                                {
                                                    /********************* theReport.active **********************/
                                                    xtype: 'checkbox',
                                                    fieldLabel:
                                                        CMDBuildUI.locales.Locales.administration.common.labels.active,
                                                    localized: {
                                                        fieldLabel:
                                                            'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                                    },
                                                    hidden: true,
                                                    name: 'enabled',
                                                    bind: {
                                                        value: '{theReport.active}',
                                                        readOnly: '{actions.view}',
                                                        hidden: '{!theReport}'
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
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.reports.titles.file, // File
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.titles.file'
                    },
                    items: [
                        {
                            layout: 'column',
                            columnWidth: 0.5,
                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.custompages.fieldlabels.zipfile, // Zip file
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.custompages.fieldlabels.zipfile'
                                    },
                                    allowBlank: false,
                                    items: [
                                        {
                                            flex: 1,
                                            xtype: 'filefield',
                                            name: 'fileReport',
                                            msgTarget: 'side',
                                            emptyText:
                                                CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile,
                                            localized: {
                                                emptyText:
                                                    'CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile'
                                            },
                                            accept: '.zip',
                                            buttonConfig: {
                                                ui: 'administration-secondary-action-small'
                                            },
                                            bind: {
                                                hidden: '{actions.view}'
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
    ],

    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'top',
            borderBottom: 0,
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true, // #editBtn set true for show the button
                    sql: true,
                    delete: true, // #deleteBtn set true for show the button
                    activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
                    download: true // #downloadBtn set true for show the buttons
                },

                /* testId */
                'report',

                /* viewModel object needed only for activeTogle */
                'theReport',

                /* add custom tools[] on the left of the bar */
                [],

                /* add custom tools[] before #editBtn*/
                [],

                /* add custom tools[] after at the end of the bar*/
                []
            ),
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view || hideForm}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
        }
    ]
});
