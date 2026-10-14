Ext.define('CMDBuildUI.model.base.Patch', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'category',
        type: 'string'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/boot/patches'
    }
});
