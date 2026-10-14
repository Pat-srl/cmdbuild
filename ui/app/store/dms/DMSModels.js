Ext.define('CMDBuildUI.store.dms.DMSModels', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.dms.DMSModel'
    ],

    alias: 'store.dmsmodels',

    model: 'CMDBuildUI.model.dms.DMSModel',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    filters: [
        function(item) {
            return item.get('name') !== CMDBuildUI.model.dms.DMSModel.masterParentClass;
        }
    ]


});