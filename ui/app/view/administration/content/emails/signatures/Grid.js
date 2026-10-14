Ext.define('CMDBuildUI.view.administration.content.emails.signatures.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.signatures.GridController'
    ],

    alias: 'widget.administration-content-emails-signatures-grid',
    controller: 'administration-content-emails-signatures-grid',
    viewModel: {},

    forceFit: true,
    itemId: 'emailSignaturesGrid',
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
        },
        dataIndex: '_default',
        xtype: 'checkcolumn',
        disabled: true,
        width: '10%'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'code',
        align: 'left',
        width: '40%'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left',
        width: '40%'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        dataIndex: 'active',
        xtype: 'checkcolumn',
        disabled: true,
        width: '10%'
    }],
    bind: {
        store: '{signatures}'
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-emails-signatures-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }]
});