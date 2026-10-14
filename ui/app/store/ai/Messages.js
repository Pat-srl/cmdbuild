Ext.define('CMDBuildUI.store.ai.Messages', {
    extend: 'CMDBuildUI.store.Base',
    requires: ['CMDBuildUI.store.Base', 'CMDBuildUI.model.ai.Message'],
    alias: 'store.ai-messages',
    model: 'CMDBuildUI.model.ai.Message',
    storeId: 'ai-messages',

    sorters: [
        {
            property: 'timestamp',
            direction: 'ASC'
        }
    ],

    pageSize: 0, // disable pagination
    autoLoad: false,
    autoDestroy: false
});
