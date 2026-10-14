Ext.define('CMDBuildUI.view.administration.content.emails.templates.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.GridController',
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.ViewInRow'
    ],

    mixins: ['CMDBuildUI.mixins.grids.Grid'],
    alias: 'widget.administration-content-emails-templates-grid',
    controller: 'administration-content-emails-templates-grid',
    viewModel: {
        type: 'administration-content-emails-templates-grid'
    },
    bufferedRenderer: false,
    forceFit: true,
    loadMask: true,
    reserveScrollbar: true,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    itemId: 'emailTemplatesGrid',
    labelWidth: "auto",
    columns: [],
    bind: {
        store: '{templates}',
        selection: '{selected}'
    },
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-emails-templates-card-viewinrow',
            ui: 'administration-tabandtools',
            layout: 'fit',
            paddingBottom: 10,
            heigth: '100%',
            viewModel: {}
        }
    }]
});