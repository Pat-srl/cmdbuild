Ext.define('CMDBuildUI.store.views.JoinViews', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.views.View'
    ],

    alias: 'store.joinviews',

    model: 'CMDBuildUI.model.views.View',

    advancedFilter: {
        attributes:{
            type: [{operator: 'equal', value: ['join']}]
        }
    },

    remoteFilter: true,
    pageSize: 0, // disable pagination
    autoLoad: false
});