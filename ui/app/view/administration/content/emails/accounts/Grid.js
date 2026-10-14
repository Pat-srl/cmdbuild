Ext.define('CMDBuildUI.view.administration.content.emails.accounts.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.GridController'
    ],

    alias: 'widget.administration-content-emails-accounts-grid',
    controller: 'administration-content-emails-accounts-grid',
    viewModel: {},

    forceFit: true,
    itemId: 'emailAccountsGrid',
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
        },
        dataIndex: 'default',
        xtype: 'checkcolumn',
        disabled: true,
        width: 100
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'name',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.username,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.username'
        },
        dataIndex: 'username',
        flex: 1,
        hidden: true
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.address,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.address'
        },
        dataIndex: 'address',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.smtpserver,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.smtpserver'
        },
        dataIndex: 'smtp_server',
        flex: 1,
        hidden: true
    }, {
        text: CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail'
        },
        dataIndex: 'maxAttachmentSizeForEmail',
        flex: 1,
        hidden: true,
        renderer: function (value, cell, record, rowIndex, colIndex, store, grid) {
            return value && value !== "0" ? value : CMDBuildUI.locales.Locales.administration.common.labels.default;
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.imapserver,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.imapserver'
        },
        dataIndex: 'imap_server',
        flex: 1,
        hidden: true
    }],
    bind: {
        store: '{accounts}'
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-emails-accounts-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }]
});