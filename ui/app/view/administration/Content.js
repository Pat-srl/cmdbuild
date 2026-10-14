
(function () {
    var elementId = 'CMDBuildAdministrationContent';

    Ext.define('CMDBuildUI.view.administration.Content', {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.administration.ContentController',
            'CMDBuildUI.view.administration.ContentModel',

            'CMDBuildUI.view.administration.navigation.Container',
            'CMDBuildUI.view.administration.DetailsWindow'
        ],

        statics: {
            elementId: elementId
        },
        autoEl: {
            'data-testid': 'administration-content'
        },
        xtype: 'administration-content',
        controller: 'administration-content',
        viewModel: {
            type: 'administration-content'
        },
        id: elementId,
        layout: 'card',
        ui:'administration',
        bind:{
            title:'{title}'
        },
        cls: Ext.baseCSSPrefix + 'panel-bold-header'
    });
})();

