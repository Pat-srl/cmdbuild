Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.StatusesPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.administration-content-tasks-jobruns-statusespanel',
    controller: 'administration-content-tasks-jobruns-statusespanel',
    viewModel: {
        type: 'administration-content-tasks-jobruns-statusespanel'
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
        flex: 1 / 3,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.jobruns.running + '</p><p class="tile-counter">0</p>',
                bind: {
                    html: '{tilehtml.running}'
                }
            }],
            bbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'tool',
                cls: Ext.baseCSSPrefix + 'tool-gray',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                itemId: 'runningFilterTool',
                tooltip: CMDBuildUI.locales.Locales.administration.jobruns.running,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.jobruns.running'
                }
            }]
        }]
    }, {
        flex: 1 / 3,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.jobruns.completed + '</p><p class="tile-counter">0</p>',
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
                tooltip: CMDBuildUI.locales.Locales.administration.jobruns.completed,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.jobruns.completed'
                }
            }]
        }]
    }, {
        flex: 1 / 3,
        items: [{
            margin: '0 0 15px 0',
            cls: 'tile',
            bind: {
                userCls: '{tileredcls.failed:pick("", "tile-red")}'
            },
            items: [{
                xtype: 'component',
                height: 30,
                html: '<p class="tile-lable">' + CMDBuildUI.locales.Locales.administration.jobruns.failed + '</p><p class="tile-counter">0</p>',
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
                tooltip: CMDBuildUI.locales.Locales.administration.jobruns.failed,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.jobruns.failed'
                }
            }]
        }]
    }, {
        flex: 4 / 3,
        items: []
    }],

    fetchData: function () {
        var me = this,
            vm = me.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('job.runs.stats');
        Ext.Ajax.request({
            url: Ext.String.format("{0}/jobs/_ANY/runs/stats", CMDBuildUI.util.Config.baseUrl),
            method: 'GET',
            success: function (response) {
                var res = JSON.parse(response.responseText);
                if (res.success && vm && !vm.destroyed) {
                    vm.set('tileinfo.running', res.data.error);
                    vm.set('tilehtml.running', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.jobruns.running, res.data.running));
                    vm.set('tileinfo.completed', res.data.completed);
                    vm.set('tilehtml.completed', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.jobruns.completed, res.data.completed));
                    vm.set('tileinfo.failed', res.data.failed);
                    vm.set('tilehtml.failed', Ext.String.format('<p class="tile-lable">{0}</p><p class="tile-counter">{1}</p>', CMDBuildUI.locales.Locales.administration.jobruns.failed, res.data.failed));
                }
            }
        });
    }
});