Ext.define('CMDBuildUI.model.map.navigation.Tree', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'cardType',
        type: 'string'
    }, {
        name: 'cardId',
        type: 'string'
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'navId',
        type: 'string'
    }, {
        name: 'text',
        type: 'string'
    }],
    proxy: {
        type: 'memory'
    }
});
//FIXME: change model as the one in relationGraph
