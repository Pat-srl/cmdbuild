
Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.Attributes', {
    // extend: 'Ext.grid.Panel',
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.AttributesController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.AttributesModel'
    ],
    alias: 'widget.administration-components-attributes-fieldsmanagement-group-attributes',
    controller: 'administration-components-attributes-fieldsmanagement-group-attributes',
    viewModel: {
        type: 'administration-components-attributes-fieldsmanagement-group-attributes'
    },
    autoMask: false,
    config: {
        isAllAttributes: false,
        group: null
    },
    padding: 5,
    
    layout: {
        type: 'fit'
    },

    style: 'width: 100%'
});
