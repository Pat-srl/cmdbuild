Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.Settings', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-settings',
    ui: 'administration-formpagination',
    items: [{
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.settings,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.settings'
        },
        hidden: false,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkedit,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkedit'
                },

                items: [{
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
                        value: '{bulkUpdate_value}',
                        store: '{bulkActionsStore}'
                    }
                }, {
                    xtype: 'displayfield',
                    bind: {
                        value: '{bulkUpdate_value}',
                        hidden: '{!actions.view}'
                    },
                    renderer: CMDBuildUI.util.administration.helper.RendererHelper.getBulkUiConfigurationsComboLabel
                }]
            }, {
                columnWidth: 0.5,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkdeletion,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforcardsbulkdeletion'
                },

                items: [{
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
                        value: '{bulkDelete_value}',
                        store: '{bulkActionsStore}'
                    }
                }, {
                    xtype: 'displayfield',
                    bind: {
                        value: '{bulkDelete_value}',
                        hidden: '{!actions.view}'
                    },
                    renderer: CMDBuildUI.util.administration.helper.RendererHelper.getBulkUiConfigurationsComboLabel
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultforworkflowbuldabort,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultforworkflowbuldabort'
                },

                items: [{
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
                        value: '{bulkAbort_value}',
                        store: '{bulkActionsStore}'
                    }
                }, {
                    xtype: 'displayfield',
                    bind: {
                        value: '{bulkAbort_value}',
                        hidden: '{!actions.view}'
                    },
                    renderer: CMDBuildUI.util.administration.helper.RendererHelper.getBulkUiConfigurationsComboLabel
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },

                // empty right side
                xtype: 'fieldcontainer',
                flex: '0.5',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids'
                },
                items: [{
                    xtype: 'combobox',
                    itemId: 'fulltext',
                    valueField: 'value',
                    displayField: 'label',
                    allowBlank: false,
                    forceSelection: true,
                    hidden: true,
                    bind: {
                        value: '{fulltext_value}',
                        store: '{fulltextStore}',
                        hidden: '{actions.view}'
                    }
                }, {
                    xtype: 'displayfield',
                    hidden: true,
                    bind: {
                        value: '{fulltext_value}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        return CMDBuildUI.util.administration.helper.RendererHelper.getSearchfieldInGridsOptionsLabel(value, true);
                    }
                }]

            }]
        }]
    }]
});