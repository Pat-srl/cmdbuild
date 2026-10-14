
Ext.define('CMDBuildUI.view.administration.content.dms.models.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-dms-models-tabpanel',
    controller: 'administration-content-dms-models-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.models.TabPanelController'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',
    hidden: true,
    bind: {
        // hidden: '{actions.empty}',
        activeTab: '{activeTab}'
    },

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});
