Ext.define('CMDBuildUI.view.administration.content.dashboards.common.grid.Grid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.grid.GridController',
        'CMDBuildUI.view.administration.content.dashboards.common.grid.GridModel',
        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],
    mixins: ['CMDBuildUI.mixins.grids.Grid'],
    alias: 'widget.administration-content-dashboards-grid',
    controller: 'administration-content-dashboards-grid',
    viewModel: {
        type: 'administration-content-dashboards-grid'
    },
    config: {
        selected: null,
        model: 'CMDBuildUI.model.calendar.Trigger',
        formInRowPlugin: null
    },
    plugins: [],
    bind: {
        selection: '{selected}'
    },
    reserveScrollbar: true,
    forceFit: true,
    loadMask: true,
    selModel: {
        mode: 'multi',
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: 'auto',
    initComponent: function () {
        var me = this;
        var dataModel = eval(me.getModel());
        if (dataModel) {
            me.setFormInRowPlugin(dataModel.viewInRowWidgetType);
            // set columns from model
            me.plugins = [];
            me.columns = CMDBuildUI.util.administration.helper.GridHelper.getColumns(dataModel);

            // set testid
            if (!me.autoEl) {
                me.autoEl = {};
            }
            me.autoEl['data-testid'] = dataModel.getAlias('grid');
            if (dataModel.viewInRowWidgetType) {
                me.plugins.push(CMDBuildUI.util.administration.helper.GridHelper.getViewInRowPlugins(dataModel));
            }
            if (dataModel.isBuffered) {
                me.plugins.push(CMDBuildUI.util.administration.helper.GridHelper.getGridFilterPlugin(dataModel));
            }
        }

        me.callParent(arguments);
        if (dataModel.store) {
            me.setStore(dataModel.store);
        }
        if (dataModel.vmStore) {
            me.setStore(me.getViewModel().get(dataModel.vmStore));
        }
        me.getStore().load();
    }
});
