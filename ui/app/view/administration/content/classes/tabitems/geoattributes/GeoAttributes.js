Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.geoattributes.GeoAttributes', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-geoattributes-geoattributes',

    autoEl: {
        'data-testid': 'administration-content-classes-tabitems-geoattributes'
    },
    layout: 'card',
    viewModel: {},
    items: [{
        xtype: 'administration-components-geoattributes-grid'
    }]
});