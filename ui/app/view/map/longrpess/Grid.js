
Ext.define('CMDBuildUI.view.map.longpress.Grid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.map-longpress-grid',

    requires: [
        'CMDBuildUI.view.map.longpress.GridController',
        'CMDBuildUI.view.map.longpress.GridModel'
    ],

    statics: {
        longpressPopupId: 'longpressPopup'
    },

    controller: 'map-longpress-grid',
    viewModel: {
        type: 'map-longpress-grid'
    },

    bind: {
        store: '{gridStore}'
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.gis.type,
        localized: {
            text: 'CMDBuildUI.locales.Locales.gis.type'
        },
        align: 'left',
        dataIndex: '_type',
        flex: 1,
        renderer: function (value, metadata, record, rowIndex, colIndex, store, view) {
            return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
        }
    }, {
        text: CMDBuildUI.locales.Locales.gis.code,
        localized: {
            text: 'CMDBuildUI.locales.Locales.gis.code'
        },
        dataIndex: 'Code',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.gis.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.gis.description'
        },
        dataIndex: 'Description',
        align: 'left',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-circle-right', 'solid'),
        align: 'center',
        flex: 0.2,
        handler: 'onActionColumnClick',
        hideable: false,
        menuDisabled: true
    }]
});
