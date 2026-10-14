Ext.define('CMDBuildUI.view.administration.content.gis.layersorder.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.layersorder.GridController',
        'CMDBuildUI.view.administration.content.gis.layersorder.GridModel'
    ],

    alias: 'widget.administration-content-gis-layersorder-grid',
    controller: 'administration-content-gis-layersorder-grid',
    viewModel: {
        type: 'administration-content-gis-layersorder-grid'
    },

    forceFit: true,
    loadMask: true,
    bind: {
        store: '{layersStore}'
    },

    viewConfig: {
        plugins: [
            {
                ptype: 'gridviewdragdrop',
                dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
                // TODO: localized not work as expected
                localized: {
                    dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
                },
                containerScroll: true,
                pluginId: 'gridviewdragdrop'
            }
        ]
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.referenceclass,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.referenceclass'
            },
            dataIndex: 'owner_type',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            dataIndex: 'subtype',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
            },
            dataIndex: 'zoomMin',
            align: 'right'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
            },
            dataIndex: 'zoomMax',
            align: 'right'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
            },
            dataIndex: 'zoomDef',
            align: 'right'
        }
    ],

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.layersorder);
        this.callParent(arguments);
    }
});
