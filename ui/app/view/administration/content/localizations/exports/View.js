Ext.define('CMDBuildUI.view.administration.content.localizations.exports.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.exports.ViewController',
        'CMDBuildUI.view.administration.content.localizations.exports.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-exports-view',
    controller: 'administration-content-localizations-exports-view',
    viewModel: {
        type: 'administration-content-localizations-exports-view'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    items: [{
        ui: 'administration-formpagination',
        defaults: {
            padding: '0 15 10 15',
            layout: 'fit'
        },
        items: [{
                items: [{
                    anchor: '100%',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.section,
                    name: 'sectionCombobox',
                    id: 'sectionCombobox',
                    reference: 'sectionCombobox',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.localizations.section'
                    },
                    value: 'all',
                    bind: {
                        store: '{sectionsStore}'
                    },
                    forceSelection: true,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-exports-view-sectionCombobox'
                    }
                }]
            },

            {
                items: [{
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.languages,
                    itemId: 'activelanguagesgrid',
                    xtype: 'grid',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-exports-view-languagesGrid'
                    },
                    viewConfig: {
                        markDirty: false
                    },

                    selModel: {
                        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        selType: 'checkboxmodel',
                        mode: 'MULTI',
                        injectCheckbox: "last"
                    },
                    reference: 'activelanguagesgrid',
                    maxHeight: 200,

                    columns: [{
                        text: CMDBuildUI.locales.Locales.administration.localizations.languages,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.localizations.languages'
                        },
                        dataIndex: 'description',
                        flex: 1,
                        align: 'left',
                        renderer: function (value, metadata, record) {
                            var lang = record.get('description');
                            var code = record.get('code');
                            var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
                            return flag + lang;
                        }
                    }],
                    bind: {
                        store: '{languages}',
                        selection: '{selection}'
                    }
                }]
            },
            {
                items: [{
                    anchor: '100%',
                    value: 'CSV',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
                    },
                    name: 'importFormatCombobox',
                    id: 'importFormatCombobox',
                    reference: 'formatCombobox',
                    bind: {
                        store: '{formatsStore}'
                    },
                    disabled: true,
                    displayField: 'label',
                    valueField: 'value',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-exports-view-importFormatCombobox'
                    }
                }]
            },
            {
                items: [{
                    anchor: '100%',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.separator,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.separator'
                    },
                    name: 'importSeparatorCombobox',
                    id: 'importSeparatorCombobox',
                    reference: 'separatorCombobox',
                    value: ';',
                    bind: {
                        store: '{separatorsStore}'
                    },
                    forceSelection: true,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-exports-view-importFormatCombobox'
                    }
                }]
            },
            {
                items: [{
                    anchor: '100%',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.activeonly,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.activeonly'
                    },
                    xtype: 'checkbox',
                    reference: 'activeOnly',
                    itemId: 'localizationExportCheckboxActiveOnly',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-exports-view-localizationExportCheckboxActiveOnly'
                    }
                }]
            }
        ]
    }],
    buttons: [{
        text: CMDBuildUI.locales.Locales.administration.localizations.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.cancel'
        },
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'administration-secondary-action-small',
        autoEl: {
            'data-testid': 'administration-content-localizations-exports-view-cancelBtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.localizations.export,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.export'
        },
        reference: 'exportBtn',
        itemId: 'exportBtn',
        ui: 'administration-action-small',
        formBind: true,
        disabled: true,
        autoEl: {
            'data-testid': 'administration-content-localizations-exports-view-exportBtn'
        }
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.localizations.export);
        this.callParent(arguments);
    }
});