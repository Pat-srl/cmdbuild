Ext.define('CMDBuildUI.view.joinviews.items.involvedcards.Grid', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.joinviews.items.involvedcards.GridController'],

    alias: 'widget.joinviews-items-involvedcards-grid',
    controller: 'joinviews-items-involvedcards-grid',

    forceFit: true,

    bind: {
        store: '{involvedCards}'
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.relations.type,
            dataIndex: 'typeNameAlias',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.type'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.code,
            dataIndex: 'code',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.code'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.description,
            dataIndex: 'description',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.description'
            }
        },
        {
            xtype: 'actioncolumn',
            minWidth: 60,
            maxWidth: 60,
            hideable: false,
            items: [
                {
                    iconCls:
                        'relations-grid-action ' +
                        CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                        grid.fireEvent('actionopencard', record);
                    },
                    getTip: function (v, metadata, record, rowindex, colindex, store) {
                        return CMDBuildUI.locales.Locales.relations.opencard;
                    }
                }
            ]
        }
    ]
});
