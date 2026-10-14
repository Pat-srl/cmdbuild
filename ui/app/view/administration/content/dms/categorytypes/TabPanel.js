Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-dms-dmscategorytypes-tabpanel',
    controller: 'administration-content-dms-dmscategorytypes-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.TabPanelController'
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
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});