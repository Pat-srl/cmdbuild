Ext.define('CMDBuildUI.store.classes.Classes', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.store.Base', 'CMDBuildUI.model.classes.Class'],

    alias: 'store.classes',

    model: 'CMDBuildUI.model.classes.Class',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,

    filters: [
        function (item) {
            return item.get('name') !== CMDBuildUI.model.classes.Class.masterParentClass;
        }
    ]
});
