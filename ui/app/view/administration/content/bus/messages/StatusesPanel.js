Ext.define('CMDBuildUI.view.administration.content.bus.messages.StatusesPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.administration-content-bus-messages-statusespanel',
    controller: 'administration-content-bus-messages-statusespanel',
    viewModel: {
        type: 'administration-content-bus-messages-statusespanel'
    },
    layout: 'hbox',
    cls: 'tile-panel',
    defaults: {
        xtype: 'container',

        flex: 0.5,
        padding: 10,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        defaults: {
            xtype: 'panel',
            height: 100,
            bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding
        }
    },

    items: [{
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.draft + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.draft}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'draftFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.draft,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.draft'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                bind: {
                    html: '{tilehtml.queued}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'queuedFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.queued,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.queued'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.processing + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.processing}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'processingFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.processing,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.processingt'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.processed + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.processed}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'processedFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.processed,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.processed'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            bind: {
                userCls: '{tileredcls.error:pick("", "tile-red")}'
            },
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.error + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.error}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'errorFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.error,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.error'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            bind: {
                userCls: '{tileredcls.failed:pick("", "tile-red")}'
            },
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.failed + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.failed}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'failedFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.failed,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.failed'
                }
            }]
        }]
    }, {
        // first column
        flex: 1,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.busmessages.completed + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.completed}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'completedFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.busmessages.completed,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.busmessages.completed'
                }
            }]
        }]
    }],

    fetchData: function () {
        var me = this,
            vm = me.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('bus.messages.stats');
        Ext.Ajax.request({
            url: Ext.String.format("{0}/etl/messages/stats", CMDBuildUI.util.Config.baseUrl),
            method: 'GET',
            success: function (response) {
                var res = JSON.parse(response.responseText);
                if (res.success && vm && !vm.destroyed) {
                    vm.set('tileinfo.draft', res.data.draft);
                    vm.set('tilehtml.draft', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.draft, res.data.draft));
                    vm.set('tileinfo.queued', res.data.queued);
                    vm.set('tilehtml.queued', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.queued, res.data.queued));
                    vm.set('tileinfo.processing', res.data.processing);
                    vm.set('tilehtml.processing', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.processing, res.data.processing));
                    vm.set('tileinfo.processed', res.data.processed);
                    vm.set('tilehtml.processed', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.processed, res.data.processed));
                    vm.set('tileinfo.error', res.data.error);
                    vm.set('tilehtml.error', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.error, res.data.error));
                    vm.set('tileinfo.failed', res.data.failed);
                    vm.set('tilehtml.failed', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.failed, res.data.failed));
                    vm.set('tileinfo.completed', res.data.completed);
                    vm.set('tilehtml.completed', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.busmessages.completed, res.data.completed));
                }
            }
        });
    }
});