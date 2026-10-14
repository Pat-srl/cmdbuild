
Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.filters.attributes.Panel',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.ViewInRowController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.ViewInRowModel'
    ],

    alias: 'widget.administration-attributes-fieldsmanagement-viewinrow',
    controller: 'administration-attributes-fieldsmanagement-viewinrow',
    viewModel: {
        type: 'administration-attributes-fieldsmanagement-viewinrow'
    },
    scrollable: 'y',
    config: {
        activity: null
    },
    
    cls: 'administration',
    ui: 'administration-tabandtools',

    items: []
});
