
Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridController',
        'CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridModel'
    ],

    controller: 'map-tab-cards-geoattributesgrid',
    viewModel: {
        type: 'map-tab-cards-geoattributesgrid'
    },

    mixins: [
        'CMDBuildUI.view.map.Mixing'
    ],

    alias: 'widget.map-tab-cards-geoattributesgrid',

    bind: {
        store: '{geoAttributesCardStore}'
    },

    disableSelection: true,

    columns: [{
        menudDisabled: true,
        align: 'left',
        dataIndex: 'text',
        flex: 1
    }, {
        menuDisabled: true,
        xtype: 'actioncolumn',
        items: [{
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus-circle', 'solid'),
            getTip: function (v, metadata, record, rowindex, colindex, store) {
                return CMDBuildUI.locales.Locales.common.actions.add;
            },
            userCls: 'margin-left', //5 px margin
            handler: 'onAddGeoValue',
            isActionDisabled: function (tableview, rowindex, colindex, item, record) {
                const canupdate = record.get('_can_write');
                return canupdate && !record.hasValues() ? false : true;
            }
        }, {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            getTip: function (v, metadata, record, rowindex, colindex, store) {
                return CMDBuildUI.locales.Locales.common.actions.edit;
            },
            userCls: 'margin-left', //5 px margin
            handler: 'onEditGeoValue',
            isActionDisabled: function (tableview, rowindex, colindex, item, record) {
                const canupdate = record.get('_can_write');
                return canupdate && record.hasValues() ? false : true;
            }
        }, {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times-circle', 'solid'),
            getTip: function (v, metadata, record, rowindex, colindex, store) {
                return CMDBuildUI.locales.Locales.common.actions.remove;
            },
            userCls: 'margin-left', //5 px margin
            handler: 'onRemoveGeoValue',
            isActionDisabled: function (tableview, rowindex, colindex, item, record) {
                const canupdate = record.get('_can_write');
                return canupdate && record.hasValues() ? false : true;
            }
        }, {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('crosshairs', 'solid'),
            getTip: function (v, metadata, record, rowindex, colindex, store) {
                return CMDBuildUI.locales.Locales.gis.view;
            },
            userCls: 'margin-left', //5 px margin
            handler: 'onViewGeoValue',
            isActionDisabled: function (tableview, rowindex, colindex, item, record) {
                return record.hasValues() ? false : true;
            }
        }]
    }],

    buttons: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        ui: 'secondary-action',
        itemId: 'cancelButton',
        handler: 'onCancelButtonClick',
        disabled: false,
        bind: {
            hidden: '{buttonsSaveCancel.hidden}'
        }
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.save,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        ui: 'management-primary',
        itemId: 'saveButton',
        handler: 'onSaveButtonClick',
        bind: {
            disabled: '{buttonsSaveCancel.saveDisabled}',
            hidden: '{buttonsSaveCancel.hidden}'
        },
        margin: '0px 5px 0px 0px'
    }]

});
