
Ext.define('CMDBuildUI.view.administration.content.processes.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-processes-tabpanel',
    controller: 'administration-content-processes-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.TabPanelController'
    ],

    viewModel: {},

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
    
    defaults: {
        height: 25
    },

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});
