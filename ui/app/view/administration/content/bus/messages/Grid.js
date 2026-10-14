Ext.define('CMDBuildUI.view.administration.content.bus.messages.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bus.messages.GridController',
        'CMDBuildUI.view.administration.content.bus.messages.GridModel',
        'CMDBuildUI.view.administration.content.bus.messages.viewinrow.viewinrow.ViewInRow',
        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],
    mixins: ['CMDBuildUI.mixins.grids.Grid'],
    alias: 'widget.administration-content-bus-messages-grid',
    controller: 'administration-content-bus-messages-grid',
    viewModel: {
        type: 'administration-content-bus-messages-grid'
    },
    viewConfig: {
        markDirty: false
    },
    bind: {
        store: '{messagesStore}'
    },
    variableHeights: false,
    reserveScrollbar: true,

    plugins: ['gridfilters', {
        ptype: 'forminrowwidget',
        pluginId: 'forminrowwidget',
        id: 'forminrowwidget',
        scrollIntoViewOnExpand: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-bus-messages-viewinrow-viewinrow',
            ui: 'administration-tabandtools',
            viewModel: {

            },
            autoHeight: true,
            bind: {

            }
        }
    }],

    autoEl: {
        'data-testid': 'administration-content-bus-messages-grid'
    },

    forceFit: true,

    selModel: {
        mode: 'SINGLE'
    }
});