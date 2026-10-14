Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.tabitems.Attachments', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-bus-messages-viewinrow-tabitems-attachments',
    bind: {
        store: '{attachmentsStore}'
    },

    headerBorders: false,
    border: false,
    bodyBorder: false,
    rowLines: false,
    sealedColumns: false,
    sortableColumns: false,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    viewConfig: {
        markDirty: false
    },

    columnWidth: 1,
    autoEl: {
        'data-testid': 'administration-content-bus-descriptors-params-grid'
    },

    forceFit: true,
    loadMask: true,

    labelWidth: 'auto',

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.busmessages.size,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.size'
            },
            dataIndex: '_byteSize',
            align: 'left',
            renderer: function (value) {
                return Ext.String.format('{0}', Ext.util.Format.fileSize(value));
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.busmessages.contenttype,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.contenttype'
            },
            dataIndex: '_contentType',
            align: 'left'
        },
        {
            xtype: 'actioncolumn',
            minWidth: 54,
            maxWidth: 54,
            align: 'center',
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.busmessages.download;
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        var vm = this.lookupViewModel();
                        var record = grid.getStore().getAt(rowIndex);
                        CMDBuildUI.util.File.download(
                            Ext.String.format(
                                '{0}/etl/messages/{1}/attachments/{2}',
                                CMDBuildUI.util.Config.baseUrl,
                                vm.get('theMessage.messageId'),
                                record.get('name')
                            )
                        ); //, extension, hideLoader, params, config)
                    }
                }
            ]
        }
    ]
});
