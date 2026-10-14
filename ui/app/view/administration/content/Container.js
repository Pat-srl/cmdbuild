
(function () {
    var elementId = 'CMDBuildAdministrationContentContainer';
    
    Ext.define('CMDBuildUI.view.administration.content.Container', {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.administration.content.ContainerController',
            'CMDBuildUI.view.administration.content.ContainerModel',
            'CMDBuildUI.view.login.Container',
            'CMDBuildUI.view.administration.MainContainer'
        ],

        statics: {
            elementId: elementId
        },

        xtype: 'administration-content-container',
        controller: 'administration-content-container',
        viewModel: {
            type: 'administration-content-container'
        },
        ui:'administration',
        layout: 'card',
        id: elementId

    });
})();
