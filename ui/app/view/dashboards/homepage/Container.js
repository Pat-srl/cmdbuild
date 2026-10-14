Ext.define('CMDBuildUI.view.dashboards.homepage.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dashboards.homepage.ContainerController',
        'CMDBuildUI.view.dashboards.homepage.ContainerModel'
    ],

    alias: 'widget.homepage-container',
    controller: 'homepage-container',
    viewModel: {
        type: 'homepage-container'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: CMDBuildUI.locales.Locales.menu.homepage,

    bodyPadding: '0 15 15 0',

    scrollable: true,

    bodyStyle: {
        'background-color': '#e7e7e7'
    }
});
