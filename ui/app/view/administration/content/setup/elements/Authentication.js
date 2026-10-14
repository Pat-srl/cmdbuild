
Ext.define('CMDBuildUI.view.administration.content.setup.elements.Authentication',{
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.AuthenticationController',
        'CMDBuildUI.view.administration.content.setup.elements.AuthenticationModel'
    ],

    alias: 'widget.administration-content-setup-elements-authentication',
    controller: 'administration-content-setup-elements-authentication',
    viewModel: {
        type: 'administration-content-setup-elements-authentication'
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

    items: []
});
