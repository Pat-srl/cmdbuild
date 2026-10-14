Ext.define('CMDBuildUI.store.administration.common.Applicability', {
    extend: 'CMDBuildUI.store.Base',
    requires: ['CMDBuildUI.model.base.ComboItem'],
    model: 'CMDBuildUI.model.base.ComboItem',

    alias: 'store.common-applicability',
    fields: ['value', 'label'],
    proxy: {
        type: 'memory'
    }
});