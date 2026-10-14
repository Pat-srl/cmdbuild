Ext.define('CMDBuildUI.view.management.Title', {
    extend: 'Ext.panel.Title',

    requires: ['CMDBuildUI.view.management.TitleController'],

    alias: 'widget.management-title',
    controller: 'management-title',

    width: '100%',
    text: CMDBuildUI.util.Navigation.defaultManagementContentTitle,

    beforeRenderConfig: {
        /**
         * @cfg {Boolean} [showFavouritesAction=true]
         *
         * Show favourite action to the right side of the bar.
         */
        showFavouritesAction: true,

        /**
         * @cfg {String} objectTypeName
         *
         * The object type name for the current context.
         */
        objectTypeName: null,

        /**
         * @cfg {String} menuType
         *
         * The menu type for the current context.
         */
        menuType: null
    },

    _favIconWrapCls: Ext.baseCSSPrefix + 'titlebar-favourites-icon-wrapper',
    _favIconClsUnselected: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'regular'),
    _favIconClsSelected: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid'),

    childEls: ['textEl', 'iconEl', 'iconWrapEl', 'favIconEl'],

    renderTpl:
        '<tpl if="iconMarkup && iconBeforeTitle">{iconMarkup}</tpl>' +
        '<div id="{id}-textEl" data-ref="textEl"' +
        ' class="{textCls} {textCls}-{ui} {itemCls}{childElCls}" unselectable="on"' +
        '<tpl if="textElRole"> role="{textElRole}"</tpl>' +
        '>' +
        '{text}' +
        '</div>' +
        '<tpl if="iconMarkup && !iconBeforeTitle">{iconMarkup}</tpl>' +
        '<tpl if="favIconMarkup">{favIconMarkup}</tpl>',

    favIconTpl:
        '<div id="{id}-favIconWrapEl" role="presentation" class="{wrapCls}">' +
        '<div id="{id}-favIconEl" data-ref="favIconEl" role="presentation" unselectable="on" ' +
        'class="{baseIconCls} {baseIconCls}-{ui} {favIconCls}" data-qtip="">' +
        '</div>' +
        '</div>',

    listeners: {
        click: {
            fn: 'onFavouritesIconClick',
            element: 'favIconEl'
        },
        keydown: {
            fn: 'onFavouritesIconKeyDown',
            element: 'favIconEl'
        }
    },

    /**
     * @override
     *
     * @returns {Object}
     */
    initRenderData: function () {
        var renderData = this.callParent();

        if (this.getShowFavouritesAction()) {
            renderData.favIconMarkup = this.getFavIconMarkup();
        }
        return renderData;
    },

    /**
     * Returns the template for favourite icon.
     *
     * @returns {Ext.Template}
     */
    getFavIconMarkup: function () {
        var inFavourites = CMDBuildUI.util.helper.UserPreferences.isItemInFavourites(
            this.getMenuType(),
            this.getObjectTypeName()
        );
        return this.lookupTpl('favIconTpl').apply({
            id: this.id,
            wrapCls: this._iconWrapCls + ' ' + this._favIconWrapCls + ' ' + this._iconAlignClasses.right,
            baseIconCls: this._baseIconCls,
            favIconCls: inFavourites ? this._favIconClsSelected : this._favIconClsUnselected
        });
    },

    /**
     * Refresh favourites icon on update object type name.
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateObjectTypeName: function (newValue, oldValue) {
        this.updateFavouritesIcon();
    },

    /**
     * Refresh favourites icon on update menu type.
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateMenuType: function (newValue, oldValue) {
        this.updateFavouritesIcon();
    },

    privates: {
        /**
         * Update favourites icon.
         */
        updateFavouritesIcon: function () {
            var inFavourites = CMDBuildUI.util.helper.UserPreferences.isItemInFavourites(
                this.getMenuType(),
                this.getObjectTypeName()
            );
            var addCls, removeCls, tooltip;
            if (inFavourites) {
                removeCls = this._favIconClsUnselected;
                addCls = this._favIconClsSelected;
                tooltip = CMDBuildUI.locales.Locales.menu.favouritesremove;
            } else {
                removeCls = this._favIconClsSelected;
                addCls = this._favIconClsUnselected;
                tooltip = CMDBuildUI.locales.Locales.menu.favouritesadd;
            }
            this.favIconEl.removeCls(removeCls);
            this.favIconEl.addCls(addCls);

            Ext.fly(this.favIconEl).set({
                'data-qtip': tooltip,
                'aria-label': tooltip,
                'aria-pressed': inFavourites ? 'true' : 'false',
                'role': 'button',
                'tabindex': 0
            });

            this.updateLayout();
        }
    }
});
