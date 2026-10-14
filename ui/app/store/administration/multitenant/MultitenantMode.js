Ext.define('CMDBuildUI.store.administration.multitenant.MultitenantMode', {
    extend: 'Ext.data.Store',
    requires: ['CMDBuildUI.model.base.ComboItem'],
    alias: 'store.multitenant-multitenantmode',
    model: 'CMDBuildUI.model.base.ComboItem',
    autoLoad: true,    
    fields: ['value', 'label'],
    proxy: {
        type: 'memory'
    }

});