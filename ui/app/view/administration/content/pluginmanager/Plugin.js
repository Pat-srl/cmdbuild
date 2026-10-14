Ext.define('CMDBuildUI.view.administration.content.pluginmanager.Plugin', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.pluginmanager.PluginController',
        'CMDBuildUI.view.administration.content.pluginmanager.PluginModel'
    ],

    mixins: ['CMDBuildUI.view.administration.content.pluginmanager.Mixin'],

    alias: 'widget.administration-content-pluginmanager-plugin',
    controller: 'administration-content-pluginmanager-plugin',
    viewModel: {
        type: 'administration-content-pluginmanager-plugin'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        /*********************  Tools and info **********************/
        {
            xtype: 'container',
            padding: 10,
            hidden: true,
            layout: 'hbox',
            bind: {
                hidden: '{!thePlugin}'
            },
            items: [
                {
                    xtype: 'component',
                    bind: {
                        html: '{pluginInfo}'
                    }
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'component',
                    bind: {
                        html: '{pluginStatus}'
                    }
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
                            html: CMDBuildUI.locales.Locales.administration.plugin.availablepatches,
                            localized: {
                                html: 'CMDBuildUI.locales.Locales.administration.plugin.availablepatches'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'applyPatchesBtn',
                            ui: 'administration-warning-action-small',
                            text: CMDBuildUI.locales.Locales.administration.plugin.applypatches,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.plugin.applypatches'
                            }
                        }
                    ],
                    hidden: true,
                    bind: {
                        hidden: '{!isPatchesAvailable}'
                    }
                },
                /*********************  Plugin tools **********************/
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    hidden: true,
                    bind: {
                        hidden: '{actions.edit || !thePlugin}'
                    },
                    items: [
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'tool',
                            align: 'right',
                            cls: 'administration-tool',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                            },
                            callback: function (owner, tool, event) {
                                owner
                                    .lookupViewModel()
                                    .set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                            }
                        },
                        {
                            xtype: 'tool',
                            align: 'right',
                            itemId: 'patchesBtn',
                            cls: 'administration-tool',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clipboard-list', 'solid'),
                            tooltip: CMDBuildUI.locales.Locales.administration.plugin.pluginpatches,
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.plugin.pluginpatches'
                            }
                        }
                    ]
                }
            ]
        },
        CMDBuildUI.view.administration.content.pluginmanager.Mixin.getSaveCancelToolbar()
    ],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.plugin);
        this.callParent(arguments);
    }
});
