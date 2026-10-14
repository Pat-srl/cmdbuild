Ext.define('CMDBuildUI.view.graph.canvas.topMenu.TopMenu', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.canvas.topMenu.TopMenuController',
        'CMDBuildUI.view.graph.canvas.topMenu.TopMenuModel'
    ],

    controller: 'graph-canvas-topmenu-topmenu',
    viewModel: {
        type: 'graph-canvas-topmenu-topmenu'
    },

    alias: 'widget.graph-canvas-topmenu-topmenu',

    items: [
        {
            xtype: 'button',
            itemId: 'refresh',
            ui: 'management-neutral-action-small',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.relationGraph.refresh,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relationGraph.refresh'
            }
        },
        {
            xtype: 'button',
            itemId: 'reopengraph',
            ui: 'management-neutral-action-small',
            cls: 'cmdbuildicon-fix', // needed for fix custom icon overflow
            iconCls: 'cmdbuildicon-relgraph',
            tooltip: CMDBuildUI.locales.Locales.relationGraph.reopengraph,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relationGraph.reopengraph'
            },
            bind: {
                disabled: '{disableReopenGraph}'
            }
        },
        {
            xtype: 'combobox',
            itemId: 'chooseNavTree',
            valueField: 'value',
            displayField: 'label',
            queryMode: 'local',
            forceSelection: true,
            width: '40%',
            bind: {
                store: '{navTreeStore}',
                disabled: '{disableChooseNavTree}'
            },
            triggers: {
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: function (combo, trigger, eOpts) {
                        combo.setValue();
                    }
                }
            },
            emptyText: CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.relationGraph.choosenaviagationtree'
            }
        }
    ]
});
