Ext.define('CMDBuildUI.view.boot.patches.Panel', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.boot.patches.PanelController', 'CMDBuildUI.view.boot.patches.PanelModel'],

    xtype: 'boot-patchespanel',
    alias: 'widget.boot-patches-panel',
    controller: 'boot-patches-panel',
    viewModel: {
        type: 'boot-patches-panel'
    },

    forceFit: true,
    title: CMDBuildUI.locales.Locales.patches.patches,
    localized: {
        title: 'CMDBuildUI.locales.Locales.patches.patches'
    },
    sortableColumns: false,
    enableColumnHide: false,
    loadMask: true,
    columns: [
        {
            text: CMDBuildUI.locales.Locales.patches.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.patches.name'
            },
            dataIndex: 'name',
            cellWrap: true,
            flex: 1,
            margin: '0 0 0 8'
        },
        {
            text: CMDBuildUI.locales.Locales.patches.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.patches.description'
            },
            dataIndex: 'description',
            cellWrap: true,
            flex: 2
        }
    ],
    bind: {
        store: '{patches}'
    },

    buttons: [
        {
            text: CMDBuildUI.locales.Locales.patches.apply,
            localized: {
                text: 'CMDBuildUI.locales.Locales.patches.apply'
            },
            itemId: 'btnApply',
            ui: 'management-primary',
            autoEl: {
                'data-testid': 'patcches-btnapply'
            },
            margin: '7 13'
        }
    ]
});
