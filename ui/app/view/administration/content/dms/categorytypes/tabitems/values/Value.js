
Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.Values',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.ValuesController',
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.ValuesModel'
    ],
    
    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-values-values',
    controller: 'administration-content-dms-dmscategorytypes-tabitems-values-values',
    viewModel: {
        type: 'administration-content-dms-dmscategorytypes-tabitems-values-values'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-dms-dmscategorytypes-tabitems-values-values'
    },
    items:[{xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-grid-grid'}]
    
});
