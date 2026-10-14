Ext.define('CMDBuildUI.view.management.navigation.Favourites', {
    extend: 'Ext.tree.Panel',

    requires: ['CMDBuildUI.view.management.navigation.FavouritesController'],

    alias: 'widget.management-navigation-favourites',
    controller: 'management-navigation-favourites',

    autoEl: {
        'data-testid': 'management-navigation-favourites'
    },

    bind: {
        selection: '{selected.favourites}',
        store: '{favourites}'
    },
    animate: false,

    cls: Ext.baseCSSPrefix + 'favourites-menu',

    useArrows: true,
    bodyBorder: false,
    viewConfig: {
        plugins: {
            ptype: 'treeviewdragdrop',
            dragText: 'Drag and drop to reorganize'
        }
    },
    listeners: {
        drop: 'onDropRecords'
    }
});
