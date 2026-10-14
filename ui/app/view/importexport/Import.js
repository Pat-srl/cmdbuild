
Ext.define('CMDBuildUI.view.importexport.Import', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.importexport.ImportController',
        'CMDBuildUI.view.importexport.ImportModel'
    ],

    alias: 'widget.importexport-import',
    controller: 'importexport-import',
    viewModel: {
        type: 'importexport-import'
    },

    config: {
        /**
         * @cfg {CMDBuildUI.model.importexports.Template []}
         * Allowed templates for data import.
         */
        templates: [],

        /**
         * @cfg {CMDBuildUI.model.classes.Class}
         * Class instance
         */
        object: null
    },

    publish: [
        'templates'
    ],

    twoWayBindable: [
        'templates'
    ],

    bind: {
        'templates': '{templatesList}'
    },
    scrollable: true,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items: [{
        xtype: 'combobox',
        fieldLabel: CMDBuildUI.locales.Locales.importexport.template,
        allowNull: false,
        valueField: '_id',
        displayField: 'description_composed',
        queryMode: 'local',
        anyMatch: true,
        itemId: 'templatescombo',
        localized: {
            fieldLabel: "CMDBuildUI.locales.Locales.importexport.template"
        },
        bind: {
            store: '{templates}',
            value: '{values.template}',
            disabled: '{disabled.template}'
        }
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'combobox',
            fieldLabel: CMDBuildUI.locales.Locales.importexport.ifc.sourcetype,
            allowBlank: false,
            valueField: 'value',
            displayField: 'label',
            flex: 1,
            style: {
                marginRight: "15px"
            },
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.importexport.ifc.sourcetype"
            },
            hidden: true,
            bind: {
                store: '{sourcetypes}',
                value: '{values.sourcetype}',
                hidden: '{hidden.sourcetype}',
                disabled: '{disabled.sourcetype}'
            }
        }, {
            xtype: 'combobox',
            fieldLabel: CMDBuildUI.locales.Locales.importexport.ifc.project,
            allowBlank: false,
            valueField: '_id',
            displayField: 'description',
            flex: 1,
            hidden: true,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.importexport.ifc.project"
            },
            bind: {
                store: '{bimprojects}',
                value: '{values.bimproject}',
                hidden: '{hidden.bimproject}',
                disabled: '{disabled.bimproject}'
            }
        }, {
            xtype: 'filefield',
            reference: 'filefield',
            name: 'file',
            fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
            allowBlank: false,
            anchor: '100%',
            flex: 1,
            hidden: true,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.attachments.file"
            },
            bind: {
                value: '{values.file}',
                hidden: '{hidden.file}',
                disabled: '{disabled.file}'
            },
            autoEl: {
                'data-testid': 'attachmentform-file'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        hidden: true,
        itemId: 'ifcCardContainer',
        defaults: {
            flex: 1
        },
        items: []
    }, {
        xtype: 'formpaginationfieldset',
        title: CMDBuildUI.locales.Locales.importexport.templatedefinition,
        reference: 'templatedefinition',
        hidden: true,
        collapsible: true,
        localized: {
            title: "CMDBuildUI.locales.Locales.importexport.templatedefinition"
        },
        bind: {
            hidden: '{!values.template}'
        }
    }, {
        xtype: 'fieldset',
        title: CMDBuildUI.locales.Locales.importexport.importresponse,
        hidden: true,
        ui: 'formpagination',
        localized: {
            title: "CMDBuildUI.locales.Locales.importexport.importresponse"
        },
        bind: {
            hidden: '{hidden.response}'
        },
        items: [{
            xtype: 'panel',
            padding: '0 0 15px 0',
            bind: {
                html: '{responseText}'
            }
        }, {
            xtype: 'grid',
            title: CMDBuildUI.locales.Locales.importexport.response.errors,
            localized: {
                title: 'CMDBuildUI.locales.Locales.importexport.response.errors'
            },
            cls: 'import-errors-grid',
            hidden: true,
            forceFit: true,
            scrollable: true,
            columns: [{
                text: CMDBuildUI.locales.Locales.importexport.response.recordnumber,
                dataIndex: 'recordNumber',
                align: 'left',
                menuDisabled: true,
                sortable: false,
                minWidth: 150,
                localized: {
                    text: "CMDBuildUI.locales.Locales.importexport.response.recordnumber"
                }
            }, {
                text: CMDBuildUI.locales.Locales.importexport.response.linenumber,
                dataIndex: 'lineNumber',
                align: 'left',
                menuDisabled: true,
                sortable: false,
                minWidth: 150,
                localized: {
                    text: "CMDBuildUI.locales.Locales.importexport.response.linenumber"
                }
            }, {
                text: CMDBuildUI.locales.Locales.importexport.response.message,
                dataIndex: 'message',
                flex: 1,
                align: 'left',
                menuDisabled: true,
                sortable: false,
                localized: {
                    text: "CMDBuildUI.locales.Locales.importexport.response.message"
                }
            }],
            bind: {
                store: '{errors}',
                hidden: '{!response.hasErrors}'
            },
            plugins: [{
                pluginId: 'forminrowwidget',
                ptype: 'forminrowwidget',
                id: 'forminrowwidget',
                expandOnDblClick: true,
                removeWidgetOnCollapse: true,
                widget: {
                    xtype: 'container',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    html: '',
                    listeners: {
                        beforerender: 'onBeforePanelRender'
                    }
                }
            }]
        }]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        ui: 'secondary-action',
        itemId: 'closebtn',
        bind: {
            disabled: '{disabled.closebtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-closebtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.importexport.downloadreport,
        localized: {
            text: 'CMDBuildUI.locales.Locales.importexport.downloadreport'
        },
        ui: 'management-primary-outline',
        itemId: 'downloadreportbtn',
        reference: 'downloadreportbtn',
        hidden: true,
        bind: {
            hidden: '{hidden.downloadreportbtn}'
        }
    }, {
        text: CMDBuildUI.locales.Locales.importexport.sendreport,
        localized: {
            text: 'CMDBuildUI.locales.Locales.importexport.sendreport'
        },
        ui: 'management-primary-outline',
        itemId: 'sendreportbtn',
        hidden: true,
        bind: {
            hidden: '{hidden.sendreportbtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-sendreportbtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.importexport.import,
        localized: {
            text: 'CMDBuildUI.locales.Locales.importexport.import'
        },
        ui: 'management-primary',
        itemId: 'importbtn',
        disabled: true,
        bind: {
            hidden: '{hidden.importbtn}',
            disabled: '{disabled.importbtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-importbtn'
        }
    }]
});
