Ext.define('CMDBuildUI.store.administration.common.WidgetTypes', {
    extend: 'CMDBuildUI.store.Base',
    requires: ['CMDBuildUI.model.base.ComboItem'],
    model: 'CMDBuildUI.model.base.ComboItem',

    alias: 'store.common-widgettypes',
    fields: ['value', 'label'],    
    sorters: ['label'],
    autoLoad: false
});