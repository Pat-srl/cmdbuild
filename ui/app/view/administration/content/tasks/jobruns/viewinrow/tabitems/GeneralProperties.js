Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.tabitems.GeneralProperties', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-jobruns-viewinrow-tabitems-generalproperties',
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
                    fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.jobcode,
                    style: 'word-break: break-all;',
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.jobcode'
                    },
                    bind: {
                        value: '{theJobrun.jobCode}'
                    }
                },
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.nodeid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.nodeid'
                    },
                    bind: {
                        value: '{theJobrun.nodeId}'
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
                        value: '{theJobrun._status_description}'
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
                    fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.timestamp,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.timestamp'
                    },
                    bind: {
                        value: '{theJobrun.timestamp}'
                    },
                    renderer: function (value) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                    }
                },
                {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.elapsedmillis,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.elapsedmillis'
                    },
                    bind: {
                        value: '{theJobrun.elapsedMillis}'
                    },
                    renderer: function (value) {
                        return Ext.String.format('{0} ms', value);
                    }
                }
            ]
        }
    ]
});
