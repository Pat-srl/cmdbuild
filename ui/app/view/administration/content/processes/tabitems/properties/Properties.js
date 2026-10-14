Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-processes-tabitems-properties-properties',
    controller: 'administration-content-processes-tabitems-properties-properties',
    viewModel: {},
    bind: {
        hidden: '{!theProcess}'
    },
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    stores: [
        'processes.ProcessVersions'
    ],
    layout: 'border',
    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-xpdlfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-processparametersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-attachmentsfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-groupingsordersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-mobilefieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-formpropertiesfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-triggersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-contextmenusfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-contentmanagementfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-iconfieldset'
        }]
    }],
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'top',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true, // #editBtn set true for show the button
            'delete': true, // #deleteBtn set true for show the button
            activeToggle: true // #enableBtn and #disableBtn set true for show the buttons
        },

            /* testId */
            'process',

            /* viewModel object needed only for activeTogle */
            'theProcess',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            [{

                xtype: 'button',
                align: 'right',
                itemId: 'versionBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                cls: 'administration-tool',
                text: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip',
                    tooltip: 'CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip'
                },
                tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip,

                menu: {
                    items: []
                },
                visible: false,
                autoEl: {
                    'data-testid': 'administration-process-versionBtn'
                },
                bind: {
                    disabled: '{toolbarHiddenButtons.version}'
                }
            }, {

                xtype: 'button',
                align: 'right',
                itemId: 'printBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('print', 'solid'),
                cls: 'administration-tool',
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.tooltip,
                menu: {
                    items: [{
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.printAsPdf,
                        listeners: {
                            click: 'onPrintMenuItemClick'
                        },
                        fileType: 'PDF',
                        cls: 'menu-item-nospace',
                        autoEl: {
                            'data-testid': 'administration-class-print-pdf'
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.printAsOdt,
                        listeners: {
                            click: 'onPrintMenuItemClick'
                        },
                        fileType: 'ODT',
                        cls: 'menu-item-nospace',
                        autoEl: {
                            'data-testid': 'administration-class-print-odf'
                        }
                    }]
                },
                visible: false,
                autoEl: {
                    'data-testid': 'administration-class-print'
                },
                bind: {
                    disabled: '{toolbarHiddenButtons.print}'
                }
            }]
        ),
        bind: {
            hidden: '{formtoolbarHidden}'
        }
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]

});