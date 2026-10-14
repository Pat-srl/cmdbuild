Ext.define('CMDBuildUI.view.administration.content.bus.messages.View', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-bus-messages-view',
    controller: 'administration-content-bus-messages-view',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    forceFit: true,
    items: [{

        xtype: 'administration-content-bus-messages-statusespanel'
    }, {
        flex: 1,
        xtype: 'administration-content-bus-messages-grid'
    }],

    tbar: [{
        xtype: 'button',
        itemId: 'refreshBtn',
        reference: 'refreshBtn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
        ui: 'administration-action',
        tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
        autoEl: {
            'data-testid': 'administration-content-bus-messages-refreshbtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        }
    }]
});