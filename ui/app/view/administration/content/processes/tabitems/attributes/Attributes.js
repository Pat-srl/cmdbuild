Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.attributes.Attributes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.*',
        'CMDBuildUI.view.administration.content.processes.tabitems.attributes.AttributesController',
        'CMDBuildUI.view.administration.content.processes.tabitems.attributes.AttributesModel'
    ],

    alias: 'widget.administration-content-processes-tabitems-attributes-attributes',
    controller: 'administration-content-processes-tabitems-attributes-attributes',
    viewModel: {
        type: 'administration-content-processes-tabitems-attributes-attributes'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-processes-tabitems-attributes-attributes'
    },
    items: [{
        xtype: 'administration-components-attributes-grid-grid'
    }]
});