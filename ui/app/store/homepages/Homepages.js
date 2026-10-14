Ext.define('CMDBuildUI.store.homepages.Homepages', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.store.Base', 'CMDBuildUI.model.homepages.Homepage'],

    alias: 'store.homepages',

    model: 'CMDBuildUI.model.homepages.Homepage',
    pageSize: 0
});
