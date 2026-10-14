Ext.define('CMDBuildUI.view.administration.content.webhooks.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.webhooks.GridController',
        'CMDBuildUI.view.administration.content.webhooks.GridModel'
    ],

    alias: 'widget.administration-content-webhooks-grid',
    controller: 'administration-content-webhooks-grid',
    viewModel: {
        type: 'administration-content-webhooks-grid'
    },

    bind: {
        store: '{gridStore}',
        selection: '{selected}'
    },
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        selectRowOnExpand: true,
        widget: {
            xtype: 'administration-content-webhooks-viewinrow',
            ui: 'administration-tabandtools',
            controller: 'administration-content-webhooks-viewinrow',
            layout: 'fit',
            paddingBottom: 10,
            heigth: '100%',
            bind: {
                theWebhook: '{selected}'
            },
            viewModel: {
                type: 'administration-content-webhooks-card'
            }
        }
    }],


    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.code,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
        },
        dataIndex: 'code',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.bus.classprocess,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.classprocess'
        },
        dataIndex: '_target_description',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.bus.event,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.event'
        },
        dataIndex: 'event',
        flex: 1,
        renderer: function (value, row, record) {
            return CMDBuildUI.util.administration.helper.ModelHelper.getWebhookEvents(record.get('_target_type')).find(function (e) { return e.value === value; }).label;
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.bus.method,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.method'
        },
        dataIndex: 'method',
        flex: 1,
        renderer: function (value) {
            return CMDBuildUI.util.administration.helper.ModelHelper.getWebhookMethods().find(function (e) { return e.value === value; }).label;
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.systemconfig.url,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
        },
        dataIndex: 'url',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.bus.enabled,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.enabled'
        },
        dataIndex: 'active',
        xtype: 'checkcolumn',
        disabled: true,
        width: 100
    }]
});
