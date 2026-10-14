
(function () {
    var elementId = 'CMDBuildMainContent';
    
    Ext.define('CMDBuildUI.view.main.content.Container', {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.main.content.ContainerController',
            'CMDBuildUI.view.main.content.ContainerModel',
            'CMDBuildUI.view.login.Container',
            'CMDBuildUI.view.management.MainContainer'
        ],

        statics: {
            elementId: elementId
        },

        xtype: 'main-content-container',
        controller: 'main-content-container',
        viewModel: {
            type: 'main-content-container'
        },

        layout: 'card',

        id: elementId

    });
})();
