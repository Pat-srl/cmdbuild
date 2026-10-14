
Ext.define('CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.Panel', {
    extend: 'CMDBuildUI.view.filters.attributes.Panel',
    requires: [
        'CMDBuildUI.view.filters.attributes.Panel',
        'CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.PanelController',
        'CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.PanelModel'
    ],

    alias: 'widget.administration-components-filterpanels-attributes-panel',
    controller: 'administration-components-filterpanels-attributes-panel',
    viewModel: {
        type: 'administration-components-filterpanels-attributes-panel'
    },
    config: {
        allowInputParameter: false,
        allowCurrentGroup: false,
        allowCurrentUser: false,
        onlyOneLevel: false,
        allowArbitraryAttributeName: false
    }
});
