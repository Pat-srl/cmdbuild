Ext.define('CMDBuildUI.view.management.TitleController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-title',

    listen: {
        global: {
            modifyfavouriteicontitle: 'onModifyFavouriteIconTitle'
        }
    },

    /**
     * On favourites icon click by mouse
     */
    onFavouritesIconClick: function () {
        this.toggleFavourite();
    },

    /**
     * On favourites icon down by keyboard.
     *
     * @param {Event} e
     */
    onFavouritesIconKeyDown: function (e) {
        if (e.getKey() === e.ENTER || e.getKey() === e.SPACE) {
            e.preventDefault();
            this.toggleFavourite();
        }
    },

    /**
     * Modify favourite icon on title
     */
    onModifyFavouriteIconTitle: function () {
        this.getView().updateFavouritesIcon();
    },

    privates: {
        /**
         * On favourites icon toggle (click or keyboard).
         */
        toggleFavourite: function () {
            var view = this.getView();

            var favourites = CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems() || [];
            var inFavourites = CMDBuildUI.util.helper.UserPreferences.isItemInFavourites(
                view.getMenuType(),
                view.getObjectTypeName()
            );

            if (inFavourites) {
                // remove item
                Ext.Array.remove(favourites, inFavourites);
            } else {
                // add item
                Ext.Array.push(favourites, {
                    menuType: view.getMenuType(),
                    objectTypeName: view.getObjectTypeName()
                });
            }

            // save changes
            CMDBuildUI.util.helper.UserPreferences.updateFavouritesMenuItems(favourites);
            Ext.GlobalEvents.fireEventArgs('favouritesmenuchange', [favourites]);

            // update icon in title bar
            view.updateFavouritesIcon();
        }
    }
});
