Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.Topbar', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.TopbarController'],

    alias: 'widget.administration-content-importexport-gatetemplates-tabitems-templates-topbar',
    controller: 'administration-content-importexport-gatetemplates-tabitems-templates-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',

            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.addtemplate,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.importexport.texts.addtemplate'
                    },
                    ui: 'administration-primary-outline-small',
                    reference: 'addgatetemplate',
                    itemId: 'addgatetemplate',
                    autoEl: {
                        'data-testid': 'administration-user-toolbar-addtemplate'
                    },
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'search',
                    width: 250,
                    emptyText:
                        CMDBuildUI.locales.Locales.administration.importexport.emptyTexts.searchgatetemplatesfield,
                    localized: {
                        emptyText:
                            'CMDBuildUI.locales.Locales.administration.importexport.emptyTexts.searchgatetemplatesfield'
                    },
                    cls: 'administration-input',
                    reference: 'searchtext',
                    itemId: 'searchtext',
                    bind: {
                        hidden: '{!canFilter}'
                    },
                    listeners: {
                        specialkey: 'onSearchSpecialKey',
                        change: 'onSearchSubmit'
                    },
                    triggers: {
                        search: {
                            cls: Ext.baseCSSPrefix + 'form-search-trigger',
                            handler: 'onSearchSubmit',
                            autoEl: {
                                'data-testid': 'administration-gistemplates-toolbar-form-search-trigger'
                            }
                        },
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: 'onSearchClear',
                            autoEl: {
                                'data-testid': 'administration-gistemplates-toolbar-form-clear-trigger'
                            }
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-gistemplates-toolbar-search-form'
                    }
                }
            ]
        }
    ]
});
