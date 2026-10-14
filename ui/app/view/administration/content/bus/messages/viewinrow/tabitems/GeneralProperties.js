Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.tabitems.GeneralProperties', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-bus-messages-viewinrow-tabitems-generalproperties',
    ui: 'administration-formpagination',
    layout: 'column',
    items: [
        {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.busmessages.messageid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.busmessages.messageid'
                    },
                    bind: {
                        value: '{theMessage.messageId}'
                    }
                },
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.busmessages.nodeid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.busmessages.nodeid'
                    },
                    bind: {
                        value: '{theMessage.nodeId}'
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.busmessages.queue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.busmessages.queue'
                    },
                    bind: {
                        value: '{theMessage.queue}'
                    }
                },
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.busmessages.timestamp,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.busmessages.timestamp'
                    },
                    bind: {
                        value: '{theMessage.timestamp}'
                    },
                    renderer: function (value) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.status,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
                    },
                    bind: {
                        value: '{theMessage._status_description}'
                    }
                }
            ]
        }
    ]
});
