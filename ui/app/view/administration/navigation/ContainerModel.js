Ext.define('CMDBuildUI.view.administration.navigation.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-navigation-container',

    formulas: {
        navTitle: function () {
            return CMDBuildUI.locales.Locales.main.navigation;
        },

        searchStoreData: {
            get: function (data) {
                var data = [];
                var root = Ext.getStore('administration.MenuAdministration').getRoot();

                function extractChildren(node, type_description, type_index) {
                    node.childNodes.forEach(function (child, index) {
                        if (child.get('menutype') !== 'folder') {
                            data.push(
                                Ext.apply(
                                    {
                                        _typedescription: type_description,
                                        _typeindex: type_index
                                    },
                                    child.getData()
                                )
                            );
                        }
                        extractChildren(child, type_description || child.get('text'), type_index || index);
                    });
                }

                extractChildren(root);
                return data;
            }
        }
    },

    stores: {
        menusearch: {
            proxy: 'memory',
            data: '{searchStoreData}',
            grouper: {
                property: '_typedescription',
                sortProperty: '_typeindex'
            },
            remoteFilter: false,
            sorters: 'text'
        }
    }
});
