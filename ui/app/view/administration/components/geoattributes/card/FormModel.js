Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-geoattributes-card-form',

    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        },
        type: {
            isLine: false,
            isPoint: false,
            isPolygon: false
        },
        treeStoreData: []
    },

    formulas: {
        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        subtypeManager: {
            bind: '{theGeoAttribute.subtype}',
            get: function (subtype) {
                this.set('type.isLine', subtype === 'linestring');
                this.set('type.isPoint', subtype === 'point');
                this.set('type.isPolygon', subtype === 'polygon');
            }
        },

        types: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributeTypes();
        },

        subtypes: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributeSubtypes();
        },

        fillTypes: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesFillTypes();
        },

        fillPatterns: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesFillPatterns();
        },

        strokeStyles: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesStrokeStyles();
        }
    },

    stores: {
        typesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{types}',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            autoDestroy: true
        },

        subtypesStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{subtypes}',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            autoDestroy: true
        },

        fillTypeStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{fillTypes}',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            autoDestroy: true
        },

        strokeDashStyleStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{strokeStyles}',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            autoDestroy: true
        },

        gridStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            root: '{treeStoreData}'
        },

        icons: {
            model: 'CMDBuildUI.model.icons.Icon',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format('{0}/uploads/?path=images/gis', CMDBuildUI.util.Config.baseUrl),
                type: 'baseproxy'
            },
            pagination: 0
        },

        linkAttributesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{linkAttributes}',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            autoDestroy: true
        }
    }
});
