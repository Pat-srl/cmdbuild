Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-ruledefinitions-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemupdated: 'onItemUpdated',
            itemcreated: 'onItemCreated',
            itemdeleted: 'onItemDeleted',
            sortchange: 'onSortChange',
            rowdblclick: 'onRowDblclick'
        }
    },

    onBeforeRender: function (view) {
        CMDBuildUI.util.Stores.load('emails.Templates');
    },
    /**
     * @event
     * @param {CMDBuildUI.model.calendar.Trigger} record 
     */
    onItemUpdated: function (grid, record) {
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },
    /**
     * @event
     * @param {CMDBuildUI.model.calendar.Trigger} record 
     */
    onItemCreated: function (record) {
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },
    /**
     * @event
     * @param {Number} nextIndex 
     */
    onItemDeleted: function (nextIndex) {
        var view = this.getView();
        var record;
        if (nextIndex !== null && nextIndex >= 0) {
            record = view.getStore().getAt(nextIndex);
        }
        CMDBuildUI.util.administration.helper.GridHelper.gridReload(this, record);
    },
    /**
     * @event
     */
    onSortChange: function () {
        var view = this.getView();
        if (view.getSelection().length) {
            var store = view.getStore();
            var index = store.findExact("_id", view.getSelection()[0].get('_id'));
            var record = store.getAt(index);
            view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('togglerow', [view, record, index]);

        }
    },

    /**
     * 
     * @param {Ext.view.Table} row 
     * @param {CMDBuildUI.model.calendar.Trigger} record 
     * @param {HTMLElement} element 
     * @param {Number} rowIndex 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            dataModel = eval(view.getModel()),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var viewModel = {
            data: {
                grid: view.ownerGrid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    view: false,
                    edit: true,
                    add: false
                }
            },
            links: {}
        };
        viewModel.links[dataModel.vmObjectName] = {
            type: dataModel.$className,
            id: record.get('_id')
        };
        view.setSelection(record);

        container.removeAll();
        container.add({
            xtype: dataModel.getAlias('card'),
            viewModel: viewModel
        });
    }
});