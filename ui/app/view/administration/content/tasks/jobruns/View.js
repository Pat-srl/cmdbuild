Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.View', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-jobruns-view',
    controller: 'administration-content-tasks-jobruns-view',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    forceFit: true,
    items: [{
        xtype: 'administration-content-tasks-jobruns-statusespanel'
    }, {
        flex: 1,
        xtype: 'administration-content-tasks-jobruns-grid'
    }],
    tbar: [{
        xtype: 'button',
        itemId: 'refreshBtn',
        reference: 'refreshBtn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
        ui: 'administration-action',
        tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
        autoEl: {
            'data-testid': 'administration-content-tasks-jobruns-refreshbtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        }
    }]
});