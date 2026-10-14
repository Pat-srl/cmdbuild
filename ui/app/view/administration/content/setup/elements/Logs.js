Ext.define('CMDBuildUI.view.administration.content.setup.elements.Logs', {
    extend: 'Ext.tab.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LogsController',
        'CMDBuildUI.view.administration.content.setup.elements.LogsModel'
    ],

    alias: 'widget.administration-content-setup-elements-logs',
    controller: 'administration-content-setup-elements-logs',
    viewModel: {
        type: 'administration-content-setup-elements-logs'
    },
    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: false,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTabs.logs}'
    },

    items: []
});