Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.models.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-dms-models-tabitems-properties-properties',
    controller: 'administration-content-dms-models-tabitems-properties-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    layout: 'border',
    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-generaldatafieldset'
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-dmsmodelparametersfieldset' /*, bind: { hidden: '{actions.add}' } */
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-attachmentsfieldset'
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-groupingsordersfieldset'
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-valididationfieldset' /*, bind: { hidden: '{actions.add}' }*/
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-triggersfieldset' /*, bind: { hidden: '{actions.add}' } */
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-formwidgetfieldset' /*, bind: { hidden: '{actions.add}' }*/
            },
            {
                xtype: 'administration-content-dms-models-tabitems-properties-fieldsets-iconfieldset'
            }
        ]
    }],
    bind: {                
            hidden: '{actions.empty}'
        
    },
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
            'class',

            /* viewModel object needed only for activeTogle */
            'theModel',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            [{

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
                    'data-testid': 'administration-dms-models-print'
                },
                bind: {
                    disabled: '{toolbarHiddenButtons.print}'
                }
            }]
        )
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