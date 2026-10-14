Ext.define('CMDBuildUI.view.administration.navigation.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.navigation.ContainerController',
        'CMDBuildUI.view.administration.navigation.ContainerModel',

        'CMDBuildUI.view.administration.navigation.Tree'
    ],

    xtype: 'administration-navigation-container',
    controller: 'administration-navigation-container',
    viewModel: {
        type: 'administration-navigation-container'
    },

    layout: 'container',
    width: 250,
    scrollable: true,

    ui: 'administration-navigation',
    autoEl: {
        'data-testid': 'administration-navigation-container'
    },

    items: [{
        xtype: 'administration-navigation-tree',
        store:'administration.MenuAdministration',
        itemId: 'navigation-tree',
        scrollable: true,
        bind:{
            selected: '{startingNode}'
        }
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        itemId: 'navTopBar',
        ui: 'admin-navigation-search',
        items: [{
            xtype: 'groupedcombo',
            itemId: 'navSearchField',
            flex: 1,

            displayField: 'text',
            hideLabel: true,
            hideTrigger: true,
            anyMatch: true,
            queryMode: 'local',
            queryDelay: 250,
            cls: Ext.baseCSSPrefix + 'navigation-search-input',

            emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
            },

            listConfig: {
                emptyText: CMDBuildUI.locales.Locales.errors.notfound,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.errors.notfound'
                }
            },

            bind: {
                store: '{menusearch}'
            }
        }]
    }]
});