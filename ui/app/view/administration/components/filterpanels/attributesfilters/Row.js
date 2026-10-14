
Ext.define('CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.Row', {
    extend: 'CMDBuildUI.view.filters.attributes.Row',

    requires: [
        'CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.RowController',
        'CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.RowModel'
    ],

    alias: 'widget.administration-components-filterpanels-attributes-row',
    controller: 'administration-components-filterpanels-attributes-row',
    viewModel: {
        type: 'administration-components-filterpanels-attributes-row'
    }
});
