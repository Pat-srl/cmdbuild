
Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanel',{
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanelController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanelModel'
    ],

    alias: 'widget.administration-content-importexport-gatetemplates-tabpanel',
    controller: 'administration-content-importexport-gatetemplates-tabpanel',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-tabpanel'
    },
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

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});
