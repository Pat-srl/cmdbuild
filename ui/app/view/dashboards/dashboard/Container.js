Ext.define('CMDBuildUI.view.dashboards.dashboard.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dashboards.dashboard.ContainerController',
        'CMDBuildUI.view.dashboards.dashboard.ContainerModel'
    ],

    alias: 'widget.dashboards-container',
    controller: 'dashboards-container',
    viewModel: {
        type: 'dashboards-container'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: {
        xtype: 'management-title',
        bind: {
            text: '{title}',
            objectTypeName: '{objectTypeName}',
            menuType: '{menuType}'
        }
    },

    bodyPadding: '0 15 15 0',

    scrollable: true,

    bodyStyle: {
        'background-color': '#e7e7e7'
    }
});
