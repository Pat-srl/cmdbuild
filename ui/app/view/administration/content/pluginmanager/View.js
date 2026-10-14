Ext.define('CMDBuildUI.view.administration.content.pluginmanager.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.pluginmanager.ViewController',
        'CMDBuildUI.view.administration.content.pluginmanager.ViewModel'
    ],

    alias: 'widget.administration-content-pluginmanager-view',
    controller: 'administration-content-pluginmanager-view',
    viewModel: {
        type: 'administration-content-pluginmanager-view'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        /*********************  Add plugin **********************/
        {
            xtype: 'container',
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{!actions.add}'
            },
            items: [
                {
                    xtype: 'filefield',
                    itemId: 'pluginFile',
                    columnWidth: 0.5,
                    padding: '10',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.plugin.addplugin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.plugin.addplugin'
                    }
                }
            ],
            listeners: {
                show: function (container, eOpts) {
                    const fileField = container.down('#pluginFile');
                    fileField.allowBlank = false;
                    fileField.validate();
                }
            }
        },
        /*********************  Overview **********************/
        {
            xtype: 'container',
            padding: 10,
            height: '100%',
            hidden: true,
            layout: 'hbox',
            bind: {
                hidden: '{actions.add}'
            },
            items: [
                {
                    xtype: 'administration-content-pluginmanager-template',
                    width: '100%',
                    height: '100%'
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            //  Hide all overview on add
            bind: {
                hidden: '{actions.add}'
            },
            items: [
                /*********************  Plugin warning message **********************/
                {
                    margin: '0 10 10 0',
                    ui: 'messagewarning',
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            flex: 1,
                            ui: 'custom',
                            xtype: 'panel',
                            html: CMDBuildUI.locales.Locales.administration.common.messages.reloadsystem,
                            localized: {
                                html: 'CMDBuildUI.locales.Locales.administration.common.messages.reloadsystem'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'reloadButton',
                            ui: 'administration-warning-action-small',
                            text: CMDBuildUI.locales.Locales.administration.common.messages.reload,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.messages.reload'
                            }
                        }
                    ],
                    hidden: true,
                    bind: {
                        hidden: '{!isNecessaryReload}'
                    }
                },
                /*********************  Show warning on plugin page **********************/
                {
                    margin: '0 10 10 0',
                    ui: 'messagewarning',
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            flex: 1,
                            ui: 'custom',
                            xtype: 'panel',
                            html: 'CMDBuildUI.locales.Locales.administration.plugin.attentionrequired',
                            localized: {
                                html: 'CMDBuildUI.locales.Locales.administration.plugin.attentionrequired'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'filterWarningsBtn',
                            ui: 'administration-warning-action-small',
                            text: CMDBuildUI.locales.Locales.administration.plugin.filterplugin,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.plugin.filterplugin'
                            },
                            hidden: false,
                            bind: {
                                hidden: '{overview.isFilteredByWarnings}'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'resetFilterWarningsBtn',
                            ui: 'administration-warning-action-small',
                            text: CMDBuildUI.locales.Locales.administration.plugin.removefilter,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.plugin.removefilter'
                            },
                            hidden: true,
                            bind: {
                                hidden: '{!overview.isFilteredByWarnings}'
                            }
                        }
                    ],
                    hidden: true,
                    bind: {
                        hidden: '{!isPatchesAvailable}'
                    }
                },
                /*********************  Overview actions **********************/
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'button',
                            ui: 'administration-action-small',
                            itemId: 'addplugin',
                            text: CMDBuildUI.locales.Locales.administration.plugin.addplugin,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.plugin.addplugin'
                            }
                        },
                        {
                            xtype: 'textfield',
                            itemId: 'searchtext',
                            name: 'search',
                            padding: '0 0 0 10',
                            width: 250,
                            cls: 'administration-input',
                            bind: {
                                value: '{overview.search}'
                            },

                            emptyText: CMDBuildUI.locales.Locales.administration.plugin.emptyText.search,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.plugin.emptyText.search'
                            },

                            listeners: {
                                change: 'onSearchChange'
                            },
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: 'onSearchClear',
                                    autoEl: {
                                        'data-testid': 'administration-pluginmanager-overview-toolbar-search-clear'
                                    }
                                }
                            },
                            autoEl: {
                                'data-testid': 'administration-pluginmanager-overview-toolbar-search'
                            }
                        },
                        {
                            xtype: 'combobox',
                            name: 'tags',
                            padding: '0 0 0 10',
                            itemId: 'tagsCombo',
                            width: 300,
                            cls: 'administration-input',
                            displayField: 'text',
                            valueField: 'code',
                            emptyText: CMDBuildUI.locales.Locales.administration.plugin.emptyText.tags,
                            triggers: {
                                clear: {
                                    cls: 'x-form-clear-trigger',
                                    handler: function (combo, trigger, eOpts) {
                                        combo.fireEvent('cleartrigger', combo, trigger, eOpts);
                                    }
                                }
                            },
                            bind: {
                                value: '{overview.tagscombo.value}',
                                store: '{tags}'
                            },
                            autoEl: {
                                'data-testid': 'administration-pluginmanager-overview-toolbar-filterbytags'
                            },
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.plugin.emptyText.tags'
                            }
                        },
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'tbtext',
                            dock: 'right',
                            itemId: 'pluginsCount'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view || hideButtons}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.plugin);
        this.callParent(arguments);
    }
});
