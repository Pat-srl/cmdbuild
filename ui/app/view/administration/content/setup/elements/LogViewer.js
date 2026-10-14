Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogViewer', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LogViewerController',
        'CMDBuildUI.view.administration.content.setup.elements.LogViewerModel'
    ],

    alias: 'widget.administration-content-setup-elements-logviewer',
    controller: 'administration-content-setup-elements-logviewer',
    viewModel: {
        type: 'administration-content-setup-elements-logviewer'
    },
    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: [{
                // it will set the correct heigth
                xtype: 'button',
                itemId: 'spacer',
                style: {
                    "visibility": "hidden"
                }
            }, {
                xtype: 'tbfill' // it will move the others tools to right
            }, {
                xtype: 'tool',
                itemId: 'pauseBtn',
                hidden: true,
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pause', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.systemconfig.pause,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.systemconfig.pause'
                },
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-pauseBtn', 'logger')
                },
                bind: {
                    hidden: '{!loggeractive}'
                }
            }, {
                xtype: 'tool',
                itemId: 'startBtn',
                hidden: true,
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('play', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.systemconfig.start,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.systemconfig.start'
                },
                disabled: true,
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-startBtn', 'logger')
                },
                bind: {
                    disabled: '{!theSession.rolePrivileges.admin_sysconfig_view || showWebsocketsDisabledWarning}',
                    hidden: '{loggeractive}'
                }
            }, {
                xtype: 'tool',
                itemId: 'enableAutoScrollBtn',
                hidden: true,
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('lock', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.systemconfig.turnautoscrollon,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.systemconfig.turnautoscrollon'
                },
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-enableScrollBtn', 'logger')
                },
                bind: {
                    hidden: '{autoscrollenabled}'
                }
            }, {
                xtype: 'tool',
                itemId: 'disableAutoScrollBtn',
                hidden: true,
                cls: 'administration-tool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('unlock', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.administration.systemconfig.turnautoscrolloff,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.systemconfig.turnautoscrolloff'
                },
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-disableScrollBtn', 'logger')
                },
                bind: {
                    hidden: '{!autoscrollenabled}'
                }
            }]
        }, {
            hidden: true,
            bind: {
                hidden: '{!showWebsocketsDisabledWarning}'
            },
            margin: 10,
            xtype: 'container',
            height: 40,
            ui: 'messagewarning',
            items: [{
                flex: 1,
                ui: 'custom',
                xtype: 'panel',
                html: CMDBuildUI.locales.Locales.administration.systemconfig.logwsdisbaledwarning,
                localized: {
                    html: 'CMDBuildUI.locales.Locales.administration.systemconfig.logwsdisbaledwarning'
                }
            }]
        }]
    }],
    layout: 'fit',
    items: [{
        xtype: 'dataview',
        scrollable: 'y',
        padding: '5',
        tpl: new Ext.XTemplate(
            '<tpl for=".">',
            '<p class="log-message x-selectable">',
            '{[this.getMessage(values)]}',
            '</p>',
            '</tpl>',
            {
                scope: this,
                getMessage: function (value) {
                    return value.line || value._event;
                }
            }
        ),
        itemSelector: 'p.log-message',
        emptyText: CMDBuildUI.locales.Locales.administration.systemconfig.nologmessage,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.systemconfig.nologmessage'
        },
        bind: {
            store: '{messagesStore}'
        }
    }]
});