Ext.define('CMDBuildUI.view.administration.content.emails.queue.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.queue.GridController',
        'CMDBuildUI.view.administration.content.emails.queue.GridModel'
    ],
    alias: 'widget.administration-content-emails-queue-grid',
    controller: 'administration-content-emails-queue-grid',
    viewModel: {
        type: 'administration-content-emails-queue-grid'
    },
    scrollable: true,
    disableSelection: true,
    viewConfig: {
        preserveScrollOnRefresh: true,
        preserveScrollOnReload: true
    },
    tbar: [{
        xtype: 'button',
        ui: 'administration-action-small',
        text: CMDBuildUI.locales.Locales.administration.tasks.tooltips.stop,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.tasks.tooltips.stop'
        },
        bind: {
            hidden: '{!queueEnabled}',
            disabled: '{!toolAction._canManageQueue}'

        },
        listeners: {
            click: 'onActiveStop'
        }
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.tasks.tooltips.start,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.tasks.tooltips.start'
        },
        ui: 'administration-action-small',
        bind: {
            hidden: '{queueEnabled}',
            disabled: '{!toolAction._canManageQueue}'
        },
        listeners: {
            click: 'onActiveStart'
        }
    }],
    bind: {
        store: '{gridDataStore}'
    },
    columns: [{
        dataIndex: 'status',
        align: 'center',
        minWidth: 30,
        maxWidth: 30,
        sortable: false,
        menuDisabled: true,
        renderer: function (value) {
            switch (value) {
                case 'outgoing':
                    return '<span class="' + CMDBuildUI.util.helper.IconHelper.getIconId('hourglass-half', 'solid') + '" data-qtip="' + CMDBuildUI.locales.Locales.administration.emails.outgoing + '" style="color:#FFCC00"></span>';
                case 'error':
                    return '<span class="' + CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-triangle', 'solid') + '" data-qtip="' + CMDBuildUI.locales.Locales.administration.common.messages.error + '" style="color:#800000"></span>';
                case 'sent':
                    return '<span class="' + CMDBuildUI.util.helper.IconHelper.getIconId('square', 'solid') + '" data-qtip="' + CMDBuildUI.locales.Locales.administration.emails.sent + '" style="color:#008000"></span>';
                default:
                    break;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.from,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.from'
        },
        dataIndex: 'from',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.to,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.to'
        },
        dataIndex: 'to',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.subject,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.subject'
        },
        dataIndex: 'subject',
        align: 'left',
        flex: 2,
        renderer: function (data) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTextField(data);
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.date,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.date'
        },
        dataIndex: 'date',
        align: 'left',
        minWidth: 150,
        maxWidth: 150,
        renderer: Ext.util.Format.dateRenderer('d/m/Y H:i:s')
    }, {
        xtype: 'actioncolumn',
        minWidth: 30,
        maxWidth: 30,
        align: 'center',
        items: [{
            bind: {
                disabled: '{!toolAction._canManageQueue}'
            },
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('paper-plane', 'solid'),
            getTip: function () {
                return CMDBuildUI.locales.Locales.administration.emails.send;
            },
            handler: 'onItemSendClick'
        }]
    }]
});