Ext.define('CMDBuildUI.model.domains.Reference', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_type',
        type: 'string'
    }, {
        name: 'Description',
        type: 'string'
    }],

    proxy: {
        type: 'baseproxy'
    }
});
