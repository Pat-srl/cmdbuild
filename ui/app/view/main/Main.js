Ext.define('CMDBuildUI.view.main.Main', {
    extend: 'Ext.container.Viewport',

    requires: [
        'Ext.plugin.Viewport',
        'Ext.window.MessageBox',

        'CMDBuildUI.view.main.MainController',
        'CMDBuildUI.view.main.MainModel',
        'CMDBuildUI.view.main.header.Header',
        'CMDBuildUI.view.main.content.Container'
    ],

    alias: 'widget.app-main',
    controller: 'main',
    viewModel: {
        type: 'main'
    },

    layout: 'border',

    items: [{
        xtype: 'main-header-header',
        region: 'north'
    }, {
        xtype: 'main-content-container',
        scrollable: true,
        region: 'center'
    }]

});
