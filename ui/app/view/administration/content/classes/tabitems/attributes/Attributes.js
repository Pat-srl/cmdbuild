Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.attributes.Attributes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.*',
        'CMDBuildUI.view.administration.content.classes.tabitems.attributes.AttributesController',
        'CMDBuildUI.view.administration.content.classes.tabitems.attributes.AttributesModel'
    ],

    alias: 'widget.administration-content-classes-tabitems-attributes-attributes',
    controller: 'administration-content-classes-tabitems-attributes-attributes',
    viewModel: {
        type: 'administration-content-classes-tabitems-attributes-attributes'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-classes-tabitems-attributes-attributes'
    },
    items: [{
        xtype: 'administration-components-attributes-grid-grid'
    }]
});