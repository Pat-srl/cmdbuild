Ext.define('CMDBuildUI.view.joinviews.configuration.Configuration', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.ConfigurationController',
        'CMDBuildUI.view.joinviews.configuration.ConfigurationModel'
    ],
    alias: 'widget.joinviews-configuration-configuration',
    controller: 'joinviews-configuration-configuration',
    viewModel: {
        type: 'joinviews-configuration-configuration'
    },

    forceFit: true,
    layout: 'fit',

    items: [
        {
            xtype: 'tabpanel',
            itemId: 'joinviewtabpanel',
            viewModel: {
                formulas: {
                    panelTitleManager: {
                        bind: '{panelTitle}',
                        get: function (panelTitle) {
                            this.getParent().set('panelTitle', panelTitle);
                        }
                    }
                },
                /**
                 *
                 * @param {Number} currrentTabIndex
                 */
                toggleEnableTabs: function (currrentTabIndex) {
                    const me = this;
                    const view = me.getView();
                    const tabs = view.items.items;

                    tabs.forEach(function (tab) {
                        if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                            me.set('disabledTabs.' + tab.reference, true);
                        }
                    });
                }
            },
            tabPosition: 'top',
            tabRotation: 0,
            cls: 'administration-mainview-tabpanel',
            ui: 'administration-tabandtools',
            scrollable: true,
            forceFit: true,
            layout: 'fit',
            bind: {
                hidden: '{actions.empty}',
                activeTab: '{activeTab}'
            }
        }
    ],

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    },

    dockedItems: [
        {
            dock: 'top',
            borderBottom: 0,
            xtype: 'toolbar',
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',

            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.joinviews.addview,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.addview'
                    },
                    reference: 'addBtn',
                    itemId: 'addBtn',
                    ui: 'administration-action-small',
                    autoEl: {
                        'data-testid': 'conifgurablesviews-masterClassAlias-addBtn'
                    },
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                },
                {
                    xtype: 'tbfill'
                }
            ]
        }
    ]
});
