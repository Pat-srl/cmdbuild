
Ext.define('CMDBuildUI.view.administration.content.classes.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-classes-tabpanel',
    controller: 'administration-content-classes-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.TabPanelController'
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

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});
