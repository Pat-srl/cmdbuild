Ext.define('CMDBuildUI.model.attachments.Category', {
    extend: 'CMDBuildUI.model.base.Base',
    
    fields: [{ 
        name: 'description', 
        type: 'string'
    }],

    proxy: {
        url: '/configuration/attachments/categories/',
        type: 'baseproxy'
    }
});
