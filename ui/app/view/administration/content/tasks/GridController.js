Ext.define('CMDBuildUI.view.administration.content.tasks.GridController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin'],
    alias: 'controller.administration-content-tasks-grid',

    control: {
        '#': {
            sortchange: 'onSortChange',
            afterrender: 'onAfterRender',
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'

        }
    },

    onAfterRender: function (view) {
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();
        CMDBuildUI.util.Stores.loadEmailAccountsStore();
        CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        Ext.getStore('importexports.Gates').load();
        view.getSelectionModel().excludeToggleOnColumn = 5;
    },
    onRunBtnClick: function (grid, rowIndex, colIndex, tool, event, record) {
        CMDBuildUI.util.administration.helper.AjaxHelper.runJob(record).then(function (response) {
            CMDBuildUI.util.Notifier.showMessage(
                Ext.String.format(CMDBuildUI.locales.Locales.administration.tasks.taskexecuted, record.get('name')), {
                ui: 'administration',
                icon: CMDBuildUI.util.Notifier.icons.success
            });
        });

    },
    onStartStopBtnClick: function (grid, rowIndex, colIndex, tool, event, record) {
        var formInRow = grid.grid.getPlugin('administration-forminrowwidget').view;
        var modelName = CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(record.get('type'), record.get('config').tag);
        eval(modelName).load(record.get('_id'), {
            success: function (item) {
                item.set('enabled', !item.get('enabled'));
                item.set('config', item._config.getData());
                item.save({
                    success: function (_record, operation) {
                        grid.refresh();
                        formInRow.fireEventArgs('itemupdated', [grid.grid, _record, this]);
                    }
                });
            }
        });

        event.preventDefault();
        return false;
    },
    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {

    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        this.getView().setSelection(record);
    },

    onSortChange: function () {
        var currentSelected = this.view.getSelection() && this.view.getSelection()[0];
        this.view.getPlugin('administration-forminrowwidget').removeAllExpanded();

        if (currentSelected) {
            var store = this.view.getStore();
            var index = store.findExact("_id", currentSelected.get('_id'));
            var record = store.getAt(index);
            this.view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [this.getView(), record, index]);
        }
    }

});