Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenu', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController',
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuModel'
    ],

    alias: 'widget.graph-canvas-bottommenu-canvasmenu',
    controller: 'graph-canvas-bottommenu-canvasmenu',
    viewModel: {
        type: 'graph-canvas-bottommenu-canvasmenu'
    },

    items: [
        {
            xtype: 'tbtext',
            html: CMDBuildUI.locales.Locales.relationGraph.level,
            localized: {
                html: 'CMDBuildUI.locales.Locales.relationGraph.level'
            }
        },
        {
            xtype: 'slider',
            id: 'sliderLevel',
            width: 200,
            increment: 1,
            minValue: 1,
            maxValue: 10,
            listeners: {
                change: function (slider, newValue, thumb, eOpts) {
                    slider.lookupViewModel().set('pointerExternalCanvas', false);
                }
            }
        },
        {
            xtype: 'tbtext',
            id: 'sliderValue'
        },
        {
            xtype: 'tbseparator'
        },
        {
            xtype: 'button',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('comment', 'solid') + ' enableTooltip',
            cls: 'management-tool',
            itemId: 'enableTooltip',
            enableToggle: true,
            bind: {
                disabled: '{enableAllTooltip.pressed}'
            }
        },
        {
            xtype: 'tbseparator'
        },
        {
            xtype: 'button',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('comments', 'solid'),
            cls: 'management-tool',
            itemId: 'enableAllTooltip',
            enableToggle: true
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'container',
            layout: 'hbox',
            defaults: {
                labelWidth: 'auto',
                cls: Ext.baseCSSPrefix + 'process-action-field',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding
            },
            items: [
                {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.relationGraph.nodes,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.relationGraph.nodes'
                    },
                    bind: {
                        value: '{nodesNumber}'
                    }
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.relationGraph.edges,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.relationGraph.edges'
                    },
                    bind: {
                        value: '{edgesNumber}'
                    }
                }
            ]
        }
    ]
});
