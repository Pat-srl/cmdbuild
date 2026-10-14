Ext.define('CMDBuildUI.view.dependencytree.Canvas', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dependencytree.CanvasController'],

    alias: 'widget.dependencytree-canvas',
    controller: 'dependencytree-canvas',

    mixins: ['CMDBuildUI.view.dependencytree.Mixin'],

    html: '<svg style="height: 100%; font: 12px sans-serif; user-select: none; background-color: white" id="deepTree"></svg>',

    scrollable: true,

    tbar: [
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            itemId: 'initialStateBtn',
            tooltip: CMDBuildUI.locales.Locales.dependencytree.reopengraph,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.dependencytree.reopengraph'
            },
            bind: {
                hidden: '{hideRefreshButton}'
            }
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
            itemId: 'downloadBtn',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.download,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.download'
            }
        }
    ]
});
