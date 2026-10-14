Ext.define('CMDBuildUI.view.administration.content.dependencytrees.NodeSettingsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dependencytrees-nodesettings',

    data: {
        treeNode: null
    },

    formulas: {
        borderSourceData: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.colorbase,
                    value: CMDBuildUI.view.dependencytree.Mixin.default
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.colorstatic,
                    value: CMDBuildUI.view.dependencytree.Mixin.static
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.colortextlookup,
                    value: CMDBuildUI.view.dependencytree.Mixin.textColor
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.coloriconlookup,
                    value: CMDBuildUI.view.dependencytree.Mixin.iconColor
                }
            ];
        },

        iconSourceData: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.noicon,
                    value: CMDBuildUI.view.dependencytree.Mixin.default
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.staticicon,
                    value: CMDBuildUI.view.dependencytree.Mixin.static
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.lookupicon,
                    value: CMDBuildUI.view.dependencytree.Mixin.iconFont
                }
            ];
        },

        iconColorModeData: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.colorbase,
                    value: CMDBuildUI.view.dependencytree.Mixin.default
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.colorstatic,
                    value: CMDBuildUI.view.dependencytree.Mixin.static
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dependencytrees.coloriconlookup,
                    value: CMDBuildUI.view.dependencytree.Mixin.iconColor
                }
            ];
        }
    },

    stores: {
        borderSource: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{borderSourceData}'
        },

        iconSource: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{iconSourceData}'
        },

        iconColorMode: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{iconColorModeData}'
        },

        lookupAttributesList: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{lookupAttributesListData}'
        }
    }
});
