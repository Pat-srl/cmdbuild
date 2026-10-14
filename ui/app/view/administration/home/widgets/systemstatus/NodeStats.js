
Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStats',{
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatsModel'
    ],

    alias: 'widget.administration-home-widgets-systemstatus-nodestats',
    viewModel: {
        type: 'administration-home-widgets-systemstatus-nodestats'
    },

    cls: Ext.baseCSSPrefix + 'admin-home-nodestats',

    bind: {
        html: '{dynHtml}'
    }
});
