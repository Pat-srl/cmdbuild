Ext.define('CMDBuildUI.view.administration.content.domains.TabPanel', {
    extend: 'Ext.tab.Panel',

    requires: ['CMDBuildUI.view.administration.content.domains.TabPanelController'],

    alias: 'widget.administration-content-domains-tabpanel',
    controller: 'administration-content-domains-tabpanel',

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
