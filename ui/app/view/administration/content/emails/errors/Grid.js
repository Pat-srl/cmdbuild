Ext.define('CMDBuildUI.view.administration.content.emails.errors.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.errors.GridController',
        'CMDBuildUI.view.administration.content.emails.errors.GridModel'
    ],
    alias: 'widget.administration-content-emails-errors-grid',
    controller: 'administration-content-emails-errors-grid',
    viewModel: {
        type: 'administration-content-emails-errors-grid'
    },
    scrollable: true,
    disableSelection: true,
    viewConfig: {
        preserveScrollOnRefresh: true,
        preserveScrollOnReload: true
    },
    tbar: [{
        xtype: 'button',
        itemId: 'refreshBtn',
        ui: 'administration-action',
        text: CMDBuildUI.locales.Locales.common.actions.refresh,
        autoEl: {
            'data-testid': 'administration-content-bus-messages-refreshbtn'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        },
        bind: {
            disabled: '{!toolAction._canManageQueue}'

        }
    }],
    bind: {
        store: '{gridDataStore}'
    },
    columns: [{
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
        minWidth: 120,
        maxWidth: 120,
        menuDisabled: true,
        sortable: false,
        border: false,
        resizable: false,
        hideable: false,
        align: 'center',
        items: [
            '->',
            {
                bind: {
                    disabled: '{!toolAction._canManageQueue}'
                },
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                getTip: function () {
                    return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                },
                handler: 'onItemEditClick'
            },
            '->',
            {
                bind: {
                    disabled: '{!toolAction._canManageQueue}'
                },
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('paper-plane', 'solid'),
                getTip: function () {
                    return CMDBuildUI.locales.Locales.administration.emails.send;
                },
                handler: 'onItemSendClick'
            },
            '->',
            {
                bind: {
                    disabled: '{!toolAction._canManageQueue}'
                },
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                getTip: function () {
                    return CMDBuildUI.locales.Locales.administration.common.actions.delete;
                },
                handler: 'onItemDeleteClick'
            },
            '->']
    }]
});