Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.tabitems.History', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-bus-messages-viewinrow-tabitems-history',
    bind: {
        store: '{historyStore}'
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
            text: CMDBuildUI.locales.Locales.administration.busmessages.messageid,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.messageid'
            },
            dataIndex: 'messageId',
            filter: {
                type: 'string',
                dataIndex: 'messageId'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.busmessages.nodeid,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.nodeid'
            },
            dataIndex: 'nodeId',
            allowFilter: true
        },
        {
            text: CMDBuildUI.locales.Locales.administration.busmessages.queue,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.queue'
            },
            dataIndex: 'queue',
            allowFilter: true
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.status,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
            },
            dataIndex: '_status_description',
            allowFilter: true,
            renderer: {}
        },
        {
            text: CMDBuildUI.locales.Locales.administration.busmessages.timestamp,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.busmessages.timestamp'
            },
            dataIndex: 'timestamp',
            allowFilter: true,
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            }
        }
    ]
});
