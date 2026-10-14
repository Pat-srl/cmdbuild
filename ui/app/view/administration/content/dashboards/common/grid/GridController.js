Ext.define('CMDBuildUI.view.administration.content.dashboards.common.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-grid',

    control: {
        '#': {
            itemupdated: 'onItemUpdated',
            itemcreated: 'onItemCreated',
            itemdeleted: 'onItemDeleted',
            sortchange: 'onSortChange',
            rowdblclick: 'onRowDblclick'
        }
    },

    onItemUpdated: function (record) {
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },

    onItemCreated: function (record) {
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },

    onItemDeleted: function (nextIndex) {
        var view = this.getView();
        var record;
        if (nextIndex !== null && nextIndex >= 0) {
            record = view.getStore().getAt(nextIndex);
        }
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },
    onSortChange: function () {
        var view = this.getView();
        if (view.getSelection().length) {
            var store = view.getStore();
            var index = store.findExact('_id', view.getSelection()[0].get('_id'));
            var record = store.getAt(index);
            view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('togglerow', [view, record, index]);
        }
    },

    /**
     *
     * @param {*} row
     * @param {*} record
     * @param {*} element
     * @param {*} rowIndex
     * @param {*} e
     * @param {*} eOpts
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            dataModel = eval(view.getModel()),
            container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var viewModel = {
            data: {
                grid: view.ownerGrid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    view: false,
                    edit: true,
                    add: false
                }
            }
        };
        viewModel.data[dataModel.vmObjectName] = record;
        view.setSelection(record);

        container.removeAll();
        container.add({
            xtype: dataModel.getAlias('card'),
            viewModel: viewModel
        });
    },

    /**
     * Only if grid
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model} records
     */
    onAllDashboardsStoreDatachanged: function (store, records) {
        var counter = this.getView().down('#dashboardGridCounter');
        counter.setHtml(
            Ext.String.format(
                CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords,
                null,
                null,
                store.totalCount
            )
        );
    }
});
