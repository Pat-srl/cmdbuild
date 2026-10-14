Ext.define('CMDBuildUI.model.Translation', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: ['_id', 'en', 'it'],

    proxy:{
        type:'baseproxy',
        url: '/translations'
    }
});
