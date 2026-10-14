
Ext.define('CMDBuildUI.view.map.tab.tabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.tabPanelModel'
    ],

    alias: 'widget.map-tab-tabpanel',
    statics: {
        tabIndex: {
            'map-tab-cards-navigationtree': 0,
            'map-tab-cards-list': 1,
            'map-tab-cards-card': 2,
            'map-tab-cards-layers': 3,
            'map-tab-cards-legend': 4
        }
    },

    viewModel: {
        type: 'map-tab-tabpanel'
    },

    mixins: [
        'CMDBuildUI.view.map.Mixing'
    ],

    bind: {
        activeTab: '{activeItemCalculation}'
    },

    ui: 'managementlighttabpanel',
    deferredRender: false,
    collapseDirection: 'left',

    items: [{
        xtype: 'map-tab-cards-navigationtree',
        title: CMDBuildUI.locales.Locales.gis.tree,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.tree'
        },
        tabConfig: {
            hidden: true,
            bind: {
                hidden: '{hiddenTabNavigationTree}',
                disabled: '{disableTabNavigationTree}'
            }
        }
    }, {
        xtype: 'map-tab-cards-list',
        title: CMDBuildUI.locales.Locales.gis.list,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.list'
        },
        tabConfig: {
            bind: {
                disabled: '{disableTabList}'
            }
        }
    }, {
        xtype: 'map-tab-cards-card',
        title: CMDBuildUI.locales.Locales.gis.card,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.card'
        },
        tabConfig: {
            bind: {
                disabled: '{disableTabCards}'
            },
            listeners: {
                disable: function (tab, eOpts) {
                    if (tab.getRefOwner().activeTab == tab) {
                        tab.tabBar.getRefOwner().setActiveTab(1);
                    }
                }
            }
        }
    }, {
        xtype: 'map-tab-cards-layers',
        title: CMDBuildUI.locales.Locales.gis.layers,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.layers'
        },
        tabConfig: {
            bind: {
                disabled: '{disableTabLayer}'
            }
        }
    }, {
        xtype: 'map-tab-cards-legend',
        title: CMDBuildUI.locales.Locales.thematism.legend,
        localized: {
            title: 'CMDBuildUI.locales.Locales.thematism.legend'
        },
        tabConfig: {
            hidden: true,
            bind: {
                hidden: '{hideTabLegend}',
                disabled: '{disableTabLegend}'
            },
            listeners: {
                hide: function (tab, eOpts) {
                    if (tab.active) {
                        //if this tab is currently active, sets as active the first tab available
                        const items = tab.getRefOwner().getRefItems();

                        for (var i = 0; i < items.length; i++) {
                            const item = items[i];
                            if (item.isVisible()) {
                                tab.getRefOwner().getRefOwner().setActiveTab(i);
                                return;
                            }
                        }
                    }
                },
                show: function (tab, eOpts) {
                    //set itself as active on show events
                    tab.getRefOwner().getRefOwner().setActiveTab(CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-legend']);
                }
            }
        }
    }]
});
