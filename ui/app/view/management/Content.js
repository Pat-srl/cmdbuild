
(function () {
    var elementId = 'CMDBuildManagementContent';

    Ext.define('CMDBuildUI.view.management.Content', {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.management.ContentController',
            'CMDBuildUI.view.management.ContentModel',

            'CMDBuildUI.view.management.navigation.Container',
            'CMDBuildUI.view.management.DetailsWindow'
        ],

        statics: {
            elementId: elementId
        },

        xtype: 'management-content',
        controller: 'management-content',
        viewModel: {
            type: 'management-content'
        },
        id: elementId,
        layout: 'card',
        cls: Ext.baseCSSPrefix + 'panel-bold-header'
    });
})();

