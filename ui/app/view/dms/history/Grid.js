Ext.define('CMDBuildUI.view.dms.history.Grid', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.dms.history.GridController', 'CMDBuildUI.view.dms.history.GridModel'],

    alias: 'widget.dms-history-grid',
    controller: 'dms-history-grid',
    viewModel: {
        type: 'dms-history-grid'
    },

    forceFit: true,
    loadMask: true,
    reserveScrollbar: true,

    columns: [
        {
            text: CMDBuildUI.locales.Locales.attachments.creationdate,
            dataIndex: 'created',
            align: 'left',
            hidden: false,
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return Ext.util.Format.date(value, CMDBuildUI.locales.Locales.common.dates.datetime);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.creationdate'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.modificationdate,
            dataIndex: 'modified',
            align: 'left',
            hidden: false,
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return Ext.util.Format.date(value, CMDBuildUI.locales.Locales.common.dates.datetime);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.modificationdate'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.author,
            dataIndex: 'author',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.author'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.authordescription,
            dataIndex: '_author_description',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.authordescription'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.version,
            dataIndex: 'version',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.version'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.filename,
            dataIndex: 'name',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.filename'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.description,
            dataIndex: 'description',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.description'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.category,
            dataIndex: 'category',
            align: 'left',
            hidden: true,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.category'
            }
        },
        {
            xtype: 'actioncolumn',
            minWidth: 30,
            maxWidth: 30,
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.attachments.download;
                    },
                    handler: 'onActionDownload'
                }
            ]
        }
    ],

    bind: {
        store: '{attachmentshistory}'
    }
});
