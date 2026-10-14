Ext.define('CMDBuildUI.view.main.header.tenants.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-tenants-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        tableview: {
            selectioncheckrendered: 'onSelectionCheckRendered',
            beforeselect: 'onBeforeChange',
            beforedeselect: 'onBeforeChange',
            selectionchange: 'onSelectionChange'
        },
        '#checkedonly': {
            toggle: 'onToggleButton'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.main.header.tenants.Grid} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel(),
            checkbox = view.down("#checkboxIgnore");

        if (!vm.get("tenants.ignoreTenants")) {
            checkbox.fireEvent("change", checkbox);
        }

        if (!vm.get("tenants.isMultitenant")) {
            view.getSelectionModel().setSelectionMode("SINGLE");
        }
    },

    /**
     * 
     * @param {String|Numeric} value
     * @param {Object} cell
     * @param {Ext.data.Model} record
     * @param {Numeric} rowIndex
     * @param {Numeric} colIndex
     * @param {Ext.data.Store} store
     * @param {Ext.grid.column.Check} check
     * @param {Ext.view.Table}
     */
    onSelectionCheckRendered: function (value, cell, record, rowIndex, colIndex, store, check, tableview) {
        var vm = this.getViewModel();

        if (vm.get("tenants.ignoreTenants")) {
            cell.tdCls = CMDBuildUI.view.main.header.tenants.Grid.disabledcls;
            record.set("disabled", true);
        }
    },

    /**
     * 
     * @param {Ext.selection.RowModel} selModel 
     * @param {Ext.data.Model} record 
     * @param {Number} index 
     * @param {Object} eOpts 
     * @returns
     */
    onBeforeChange: function (selModel, record, index, eOpts) {
        if (record.get("disabled")) {
            return false;
        }
    },

    /**
     * 
     * @param {Ext.grid.Panel} selModel
     * @param {Ext.data.Model[]} records
     * @param {Object} eOpts
     */
    onSelectionChange: function (selModel, records, eOpts) {
        this.getViewModel().set("fields.buttonSaveDisabled", records.length === 0);
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var searchTerm = field.getValue();

        if (searchTerm) {
            var store = this.getView().getStore();
            store.clearFilter();
            store.filterBy(function (record) {
                return record.get("description").toLowerCase().indexOf(searchTerm.toLowerCase()) != -1;
            })
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var store = this.getView().getStore();
        store.clearFilter();
        field.reset();
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleButton: function (button, selected, eOpts) {
        var view = this.getView(),
            store = view.getStore();
        if (selected) {
            store.filter({
                property: 'id',
                operator: 'in',
                value: Ext.Array.pluck(view.getSelection(), 'id')
            });
        } else {
            store.clearFilter();
        }
    }

});