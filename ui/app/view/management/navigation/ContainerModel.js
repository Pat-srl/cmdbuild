Ext.define('CMDBuildUI.view.management.navigation.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-navigation-container',

    data: {
        selected: {
            navigation: null,
            favourites: null
        },
        visibility: {
            favourites: true
        },
        showsearchfield: false
    },

    formulas: {
        menuItems: {
            get: function () {
                return Ext.getStore('menu.Menu');
            }
        },

        searchStoreData: {
            bind: {
                menuStore: '{menuItems.count}' // wait for store loaded
            },
            get: function (data) {
                var data = [];
                var root = this.get('menuItems').getRoot();

                function extractChildren(node) {
                    node.childNodes.forEach(function (child) {
                        var menutype = child.get('menutype');
                        if (menutype && menutype !== 'folder' && !alreadyInList(child)) {
                            data.push(
                                Ext.apply(child.getData(), {
                                    _typedescription: CMDBuildUI.util.helper.ModelHelper.getTypeDescription(
                                        CMDBuildUI.model.menu.MenuItem.objecttypes[menutype]
                                    )
                                })
                            );
                        }
                        extractChildren(child);
                    });
                }

                function alreadyInList(menuitem) {
                    return Ext.Array.findBy(data, function (item, index) {
                        return menuitem.get('objecttypename') === item.objecttypename;
                    });
                }
                extractChildren(root);
                return data;
            }
        }
    },

    stores: {
        favourites: {
            type: 'tree',
            model: 'CMDBuildUI.model.menu.MenuItem',
            proxy: {
                type: 'memory'
            },
            rootVisible: true
        },

        menusearch: {
            proxy: 'memory',
            data: '{searchStoreData}',
            grouper: {
                property: '_typedescription'
            },
            remoteFilter: false,
            sorters: 'text'
        }
    }
});
