Ext.define('CMDBuildUI.view.administration.content.searchfilters.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-searchfilters-tabpanel',
    controller: 'administration-content-searchfilters-tabpanel',
    viewModel: {
        type: 'administration-content-searchfilters-tabpanel'
    },
    requires: [
        'CMDBuildUI.view.administration.content.searchfilters.TabPanelController',
        'CMDBuildUI.view.administration.content.searchfilters.TabPanelModel',
        'CMDBuildUI.view.administration.content.searchfilters.Form',
        'CMDBuildUI.view.administration.content.searchfilters.Permissions'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },

    dockedItems: [{
        dock: 'top',
        xtype: 'toolbar',
        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.viewfilters.texts.addfilter, // Add filter
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.viewfilters.texts.addfilter'
            },
            ui: 'administration-action-small',
            reference: 'addBtn',
            itemId: 'addBtn',
            autoEl: {
                'data-testid': 'administration-searchfilter-toolbar-addBtn'
            },
            disabled: true,
            bind: {
                disabled: '{!theSession.rolePrivileges.admin_searchfilters_modify}'
            }
        }]

    }],
    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating',
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.dashboards);
        this.callParent(arguments);
    }
});
