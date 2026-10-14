Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.View', {
    // extend: 'CMDBuildUI.components.tab.FormPanel',
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.configuration.ViewController',
        'CMDBuildUI.view.administration.content.localizations.configuration.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-configuration-view',
    controller: 'administration-content-localizations-configuration-view',
    viewModel: {
        type: 'administration-content-localizations-configuration-view'
    },
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
    scrollable: 'y',
    tools: [
        {
            // it will set the correct heigth
            xtype: 'button',
            itemId: 'spacer',
            width: 0,
            style: {
                visibility: 'hidden'
            }
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tool',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            hidden: true,
            bind: {
                hidden: '{!actions.view}',
                disabled: '{!toolAction._canUpdate}'
            },
            callback: 'onEditBtnClick',
            autoEl: {
                'data-testid': 'administration-content-localizations-localization-tabpanel-tool-editbtn'
            }
        }
    ],

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.languageconfiguration,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.languageconfiguration'
            },
            items: [
                {
                    xtype: 'container',
                    columnWidth: 1,
                    autoEl: {
                        'data-testid':
                            'administration-content-localizations-configuration-view-languageConfigurationContainer'
                    },
                    items: [
                        {
                            xtype: 'combo',
                            width: '50%',
                            forceSelection: true,
                            queryMode: 'local',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.defaultlanguage,
                            displayField: 'description',
                            valueField: 'code',
                            allowBlank: false,
                            itemId: 'defaultLanguageCombo',
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.defaultlanguage'
                            },
                            autoEl: {
                                'data-testid':
                                    'administration-content-localizations-configuration-view-languageConfigurationTextfield'
                            },
                            disabled: true,
                            bind: {
                                disabled: '{actions.view}',
                                value: '{defaultlanguage}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.showlanguagechoice,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.showlanguagechoice'
                    },
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{languageprompt}'
                    },
                    autoEl: {
                        'data-testid': 'administration-content-localizations-configuration-view-languageChoiceCheckbox'
                    }
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.enabledlanguages,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.enabledlanguages'
            },
            items: [
                {
                    xtype: 'checkboxgroup',
                    columns: 4,
                    columnWidth: 1,
                    itemId: 'languagescheckboxGroup',
                    vertical: true,
                    labelAlign: 'top',
                    items: []
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            bind: {
                hidden: '{!languageprompt}'
            },
            title: CMDBuildUI.locales.Locales.administration.localizations.loginlanguages,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.loginlanguages'
            },
            items: [
                {
                    xtype: 'checkboxgroup',
                    columns: 4,
                    columnWidth: 1,
                    itemId: 'loginlanguagescheckboxGroup',
                    vertical: true,
                    labelAlign: 'top',
                    items: []
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
        }
    ],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.localizations.configuration);
        this.callParent(arguments);
    }
});
