
Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-bus-descriptors-tabpanel',
    controller: 'administration-content-bus-descriptors-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.bus.descriptors.TabPanelController'
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
    }
});
