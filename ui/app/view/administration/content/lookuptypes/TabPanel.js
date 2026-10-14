Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-lookuptypes-tabpanel',
    controller: 'administration-content-lookuptypes-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.TabPanelController'
    ],

    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',

    tabPosition: 'top',
    tabRotation: 0,
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    }
});