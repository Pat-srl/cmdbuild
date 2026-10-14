Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.attributes.Attributes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.*',
        'CMDBuildUI.view.administration.content.dms.models.tabitems.attributes.AttributesController',
        'CMDBuildUI.view.administration.content.dms.models.tabitems.attributes.AttributesModel'
    ],

    alias: 'widget.administration-content-dms-models-tabitems-attributes-attributes',
    controller: 'administration-content-dms-models-tabitems-attributes-attributes',
    viewModel: {
        type: 'administration-content-dms-models-tabitems-attributes-attributes'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-dms.models-tabitems-attributes-attributes'
    },
    items: [{
        xtype: 'administration-components-attributes-grid-grid'
    }]
});