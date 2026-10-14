Ext.define('CMDBuildUI.view.administration.components.geoattributes.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.geoattributes.GridController',
        'CMDBuildUI.view.administration.components.geoattributes.GridModel'
    ],

    alias: 'widget.administration-components-geoattributes-grid',
    controller: 'administration-components-geoattributes-grid',
    viewModel: {
        type: 'administration-components-geoattributes-grid'
    },

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    forceFit: true,
    loadMask: true,
    itemId: 'geoAttributesGrid',
    autoEl: {
        'data-testid': 'administration-components-geoattributes-grid'
    },

    bind: {
        store: '{geoattributesStore}'
    },

    plugins: [
        {
            ptype: 'administration-forminrowwidget',
            pluginId: 'administration-forminrowwidget',
            selectRowOnExpand: true,
            expandOnDblClick: true,
            widget: {
                xtype: 'administration-components-geoattributes-card-viewinrow',
                autoEl: {
                    'data-testid': 'administration-components-geoattributes-card-viewinrow'
                },
                ui: 'administration-tabandtools',
                controller: 'administration-components-geoattributes-card-viewinrow',
                layout: 'fit',
                paddingBottom: 10,
                heigth: '100%'
            }
        }
    ],

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute'
            },
            itemId: 'addattribute',
            ui: 'administration-primary-outline-small',
            bind: {
                disabled: '{!toolAction._canUpdate}',
                hidden: '{newButtonHidden}'
            }
        },
        {
            xtype: 'localsearchfield',
            gridItemId: '#geoAttributesGrid'
        },
        {
            // move all buttons on right side
            xtype: 'tbfill'
        }
    ],

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
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            dataIndex: 'type',
            align: 'left',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return CMDBuildUI.util.administration.helper.RendererHelper.getGeoatributeTypeAndSubype(record);
            },
            sorter: '_type_description'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
            },
            dataIndex: 'zoomMin',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
            },
            dataIndex: 'zoomMax',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
            },
            dataIndex: 'zoomDef',
            align: 'left'
        },
        {
            // TODO: currently not supported
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            align: 'center',
            disabled: true
        }
    ]
});
