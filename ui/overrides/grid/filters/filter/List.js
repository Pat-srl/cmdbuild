Ext.define('Overrides.grid.filters.filter.List', {
    override: 'Ext.grid.filters.filter.List',

    /**
     * @override
     * 
     * @private
     * Creates the Menu for this filter.
     * @param {Object} config Filter configuration
     * @return {Ext.menu.Menu} 
     */
    createMenu: function (config) {
        var me = this,
            gridStore = me.getGridStore(),
            store = me.store,
            options = me.options,
            menu;

        if (store) {
            me.store = store = Ext.StoreManager.lookup(store);
        }

        me.callParent([config]);
        menu = me.menu;

        if (store) {
            if (!store.getCount() && !store.isLoaded()) {
                menu.add({
                    text: me.loadingText,
                    iconCls: Ext.baseCSSPrefix + 'mask-msg-text'
                });

                // Add a listener that will auto-load the menu store if `loadOnShow` is true (the default).
                // Don't bother with mon here, the menu is destroyed when we are
                menu.on('show', me.show, me);

                store.on('load', me.bindMenuStore, me, { single: true });
            } else {
                // if store is loaded and is empty we have to remove the loader
                if (store.isLoaded() && !store.getCount()) {
                    me.menu.removeAll();
                    me.menu.add({
                        cls: Ext.baseCSSPrefix + 'menu-item-empty'
                    });
                }
                me.createMenuItems(store);
            }

        }
        // If there are supplied options, then we know the store is local.
        else if (options) {
            me.bindMenuStore(options);
        }
        // A ListMenu which is completely unconfigured acquires its store from the unique values
        // of its field in the store. Note that the gridstore may have already been filtered on load
        // if the column filter had been configured as active with no items checked by default.
        else if (gridStore.getCount() || gridStore.data.filtered) {
            me.bindMenuStore(gridStore);
        }
        // If there are no records in the grid store, then we know it's async and we need to listen
        // for its 'load' event.
        else {
            gridStore.on('load', me.bindMenuStore, me, { single: true });
        }
    }
});