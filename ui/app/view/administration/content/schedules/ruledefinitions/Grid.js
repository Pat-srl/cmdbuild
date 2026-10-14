Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.Grid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.GridController',
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.GridModel',
        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],
    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],
    alias: 'widget.administration-content-schedules-ruledefinitions-grid',
    controller: 'administration-content-schedules-ruledefinitions-grid',
    viewModel: {
        type: 'administration-content-schedules-ruledefinitions-grid'
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
        mode: 'MULTI',
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    autoEl: {
        'data-testid': 'administration-components-scheduler-grid'
    },
    labelWidth: "auto",
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
            me.autoEl["data-testid"] = dataModel.getAlias('grid');
            if (dataModel.viewInRowWidgetType) {
                me.plugins.push(CMDBuildUI.util.administration.helper.GridHelper.getViewInRowPlugins(dataModel));
            }
            if (dataModel.isBuffered) {
                me.plugins.push(CMDBuildUI.util.administration.helper.GridHelper.getGridFilterPlugin(dataModel));
            }
        }

        me.callParent();
        if (dataModel.store) {
            me.setSore(dataModel.store);
        }
        if (dataModel.vmStore) {
            me.setStore(me.getViewModel().get(dataModel.vmStore));
        }
        me.getStore().load();
    }
});
