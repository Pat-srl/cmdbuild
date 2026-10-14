Ext.define('CMDBuildUI.view.management.navigation.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-navigation-container',

    requires: ['CMDBuildUI.view.management.navigation.Favourites'],

    listen: {
        global: {
            objecttypechanged: 'onGlobalObjectTypeNameChanged',
            favouritesmenuchange: 'onGlobalFavouritesMenuChange'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#navTopBar': {
            show: 'onNavTopBarShow'
        },
        '#navSearchField': {
            select: 'onNavSearchFieldSelect'
        }
    },

    /**
     * On before render.
     *
     * @param {CMDBuildUI.view.management.navigation.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        var fstore = vm.get('favourites');

        // create root node with favourites items
        var items = CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems();
        fstore.setRoot({
            objectdescription: CMDBuildUI.locales.Locales.menu.favourites,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid'),
            menutype: CMDBuildUI.model.menu.MenuItem.types.favourites,
            children: this.getFavouritesMenuNodes(items),
            expanded: !CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuCollapsed()
        });

        // update menu visibility
        vm.set('visibility.favourites', items.length > 0);

        // add favourites menu before or after the navigation, depending on configuration
        var position = 0;
        if (
            CMDBuildUI.util.helper.UserPreferences.menuPosition.after ===
            CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuPosition()
        ) {
            position = 1;
        }
        view.insert(position, {
            xtype: 'management-navigation-favourites',
            hidden: true,
            bind: {
                hidden: '{!visibility.favourites}'
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.management.navigation.Container} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const store = Ext.getStore('menu.Menu');

        const noDefaultPageConfigured = Ext.isEmpty(
            CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startingpage)
        );

        const isHomepageEnabled = CMDBuildUI.util.helper.Configurations.get(
            CMDBuildUI.model.Configuration.homepage.enabled
        );

        // Route to homepage if no default page is configured and homepage is enabled
        if (noDefaultPageConfigured && isHomepageEnabled) {
            const homepageNode = store.findNode('menutype', CMDBuildUI.model.menu.MenuItem.types.homepage);
            if (homepageNode) {
                view.openResourceByNode(homepageNode);
                return;
            }
        }

        // select menu item
        var startingMenuItem = CMDBuildUI.util.helper.UserPreferences.getPreferences().get(
            CMDBuildUI.model.users.Preference.startingpage_actual
        );

        if (Ext.String.startsWith(startingMenuItem, 'process:')) {
            startingMenuItem = startingMenuItem.replace('process:', 'processclass:');
        }
        // get startin node
        var startingNode;
        if (startingMenuItem) {
            startingNode = store.findNode('findcriteria', startingMenuItem);
        }
        if (!startingNode) {
            const elemFavourites = view.getViewModel().get('favourites').getRoot().childNodes;
            if (
                CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuPosition() ===
                    CMDBuildUI.util.helper.UserPreferences.menuPosition.before &&
                !Ext.isEmpty(elemFavourites)
            ) {
                startingNode = elemFavourites[0];
            } else {
                startingNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
            }
        }

        if (!startingNode) {
            CMDBuildUI.util.Navigation.addIntoManagementContainer('panel', {
                title: '&nbsp;',
                layout: 'fit'
            });
            return;
        }
        view.openResourceByNode(startingNode);
    },

    /**
     *
     * @param {CMDBuildUI.view.fields.groupedcombobox.GroupedComboBox} combo
     * @param {Ext.data.Model} record
     * @param {Object} eOpts
     */
    onNavSearchFieldSelect: function (combo, record, eOpts) {
        if (record) {
            this.getView().openResourceByNode(record);
            this.getViewModel().set('showsearchfield', false);
            combo.setValue();
        }
    },

    /**
     * @param {Ext.toolbar.Toolbar} tbar
     * @param {Object} eOpts
     */
    onNavTopBarShow: function (tbar, eOpts) {
        // focus on input on top bar show
        var input = tbar.down('#navSearchField');
        if (input) {
            input.focus();
        }
    },

    /**
     * Update navigation selection
     *
     * @param {String} newObjectTypeName
     */
    onGlobalObjectTypeNameChanged: function (newObjectTypeName) {
        var vm = this.getViewModel();
        var me = this;

        var favselected = vm.get('selected.favourites');
        var navselected = vm.get('selected.navigation');
        if (
            !(favselected || navselected) ||
            (favselected && favselected.get('objecttypename') !== newObjectTypeName) ||
            (navselected && navselected.get('objecttypename') !== newObjectTypeName)
        ) {
            // the first time wait for the stores to select the nodes
            // the other times select nodes immediately
            if (!me._storesloaded) {
                var binding = vm.bind(
                    {
                        bindTo: {
                            favourites: '{favourites}',
                            navigation: '{menuItems}'
                        }
                    },
                    function (stores) {
                        if (stores.favourites && stores.navigation) {
                            me._storesloaded = true;
                            me.selectNode();
                            binding.destroy();
                        }
                    }
                );
            } else {
                me.selectNode();
            }
        }
    },

    /**
     * On global event favourites menu change.
     *
     * @param {Object[]} newValue The new favourites menu array.
     * @param {Object[]} oldValue The old favourites menu array.
     */
    onGlobalFavouritesMenuChange: function (newValue, oldValue) {
        var vm = this.getViewModel();
        var fstore = vm.get('favourites');
        var root = fstore.getRoot();
        root.removeAll();
        var items = this.getFavouritesMenuNodes(newValue);

        items.forEach(function (n) {
            root.appendChild(n);
        });

        vm.set('visibility.favourites', !!items.length);
    },

    privates: {
        _storesloaded: false,

        /**
         * @param {CMDBuildUI.model.menu.MenuItem[]} items
         * @return {CMDBuildUI.model.menu.MenuItem} First selectable menu item
         */
        getFirstSelectableMenuItem: function (items) {
            var item;
            var i = 0;
            while (!item && i < items.length) {
                var node = items[i];
                if (node.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                    item = node;
                } else {
                    item = this.getFirstSelectableMenuItem(node.childNodes);
                }
                i++;
            }
            return item;
        },

        /**
         * Selects the node in favourites menu or in navigation menu.
         */
        selectNode: function () {
            var vm = this.getViewModel();
            var context = CMDBuildUI.util.Navigation.getCurrentContext();

            // search selected item in favourites menu
            var favstore = vm.get('favourites');
            var favselected = favstore.findNode('objecttypename', context.objectTypeName);
            if (
                favselected &&
                CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuPosition() ==
                    CMDBuildUI.util.helper.UserPreferences.menuPosition.before
            ) {
                vm.set('selected.favourites', favselected);
                vm.set('selected.navigation', null);
            } else {
                // search selected item in navigation menu
                var navstore = vm.get('menuItems');
                var newselected = navstore.findNode('objecttypename', context.objectTypeName);
                if (newselected) {
                    vm.set('selected.navigation', newselected);
                    vm.set('selected.favourites', null);
                }
            }
        },

        /**
         * Get the nodes list to add to favourites menu.
         *
         * @param {Object[]} items The list of favourites objects.
         * @returns {CMDBuildUI.model.menu.MenuItem[]} A list of menu items.
         */
        getFavouritesMenuNodes: function (items) {
            var nodes = [];
            var store = Ext.getStore('menu.Menu');

            items.forEach(function (item, index) {
                var mnode = store.findNode(
                    'findcriteria',
                    Ext.String.format('{0}:{1}', item.menuType, item.objectTypeName)
                );

                if (mnode) {
                    nodes.push({
                        objectdescription: mnode.get('objectdescription'),
                        objectdescription_translation: mnode.get('objectdescription_translation'),
                        isInPreferiteMenu: true,
                        objecttypename: mnode.get('objecttypename'),
                        menutype: mnode.get('menutype'),
                        leaf: true
                    });
                }
            });

            return nodes;
        }
    }
});
