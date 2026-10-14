Ext.define('CMDBuildUI.store.map.Tree', {
    extend: 'Ext.data.TreeStore',

    requires: ['CMDBuildUI.model.map.navigation.Tree'],
    alias: 'store.map-tree',

    config: {
        rootVisible: false
    },
    model: 'CMDBuildUI.model.map.navigation.Tree',

    root: {
        text: CMDBuildUI.locales.Locales.gis.root,
        navId: 'root',
        cardType: 'root',
        cardId: 'root',
        leaf: false,
        children: [],
        checked: true,
        expanded: true,
        navId: null
    }
});