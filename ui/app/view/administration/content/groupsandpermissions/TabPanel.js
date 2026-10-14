Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-groupsandpermissions-tabpanel',
    controller: 'administration-content-groupsandpermissions-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.TabPanelController'
    ],

    viewModel: {
    },
    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',
    config: {
        theGroup: {}        
    },
    bind: {
        activeTab: '{activeTab}',
        theGroup: '{theGroup}'
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