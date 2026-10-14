Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatus', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatusController',
        'CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatusModel'
    ],
    alias: 'widget.administration-home-widgets-systemstatus-nodestatus',
    controller: 'administration-home-widgets-systemstatus-nodestatus',
    viewModel: {
        type: 'administration-home-widgets-systemstatus-nodestatus'
    },

    ui: 'administration-formpagination',
    cls: Ext.baseCSSPrefix + 'administration-home-widgets-systemstatus-nodestatus',

    collapsible: true,

    items: [{
        xtype: 'administration-home-widgets-systemstatus-nodestats',
        flex: 0.5
    }, {
        xtype: 'panel',
        flex: 0.5,
        dockedItems: [{
            scrollable: 'x',
            xtype: 'toolbar',
            dock: 'top',
            items: [{
                xtype: 'tbtext',
                html: '',
                bind: {
                    html: '<h3>{monitorTitle}</h3>'
                }
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'combobox',
                itemId: 'timeValues',
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                editable: false,
                cls: 'administration-input',
                bind: {
                    store: '{timeValues}',
                    value: '{timeChart}'
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.home.memoryload,
                itemId: 'memoryLoadButton',
                margin: 0,
                bind: {
                    ui: '{typeChart === "memoryLoad" ? "administration-action-small" : "default-toolbar-small"}'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.home.memoryload'
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.home.nodestats.systemload,
                itemId: 'systemLoadButton',
                margin: 0,
                bind: {
                    ui: '{typeChart === "systemLoad" ? "administration-action-small" : "default-toolbar-small"}'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.home.nodestats.systemload'
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.home.activesessions,
                itemId: 'activeSessionsButton',
                margin: 0,
                bind: {
                    ui: '{typeChart === "activeSessions" ? "administration-action-small" : "default-toolbar-small"}'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.home.activesessions'
                }
            }]
        }],
        items: {
            xtype: 'container',
            itemId: 'chart',
            items: []
        }
    }]

});