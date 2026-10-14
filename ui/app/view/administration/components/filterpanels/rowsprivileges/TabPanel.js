Ext.define('CMDBuildUI.view.administration.components.filterpanels.rowsprivileges.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-components-filterpanels-rowsprivileges-tabpanel',
    controller: 'administration-components-filterpanels-rowsprivileges-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.components.filterpanels.rowsprivileges.TabPanelController'
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
       
    },
    bind: {
        activeTab: '{activeTab}'
    },

    defaults: {
        height: 25
    }
});