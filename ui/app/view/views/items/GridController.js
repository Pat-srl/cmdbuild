Ext.define('CMDBuildUI.view.views.items.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.views-items-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#searchtext': {
            specialkey: 'onSearchSpecialKey'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.views.items.Grid} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            vm.get("objectType"),
            vm.get("objectTypeName"),
            {
                allowFilter: view.getAllowFilter()
            }
        ).then(function (columns) {
            view.reconfigure(null, columns);
            CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().get("items").load();
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat,
            view = this.getView(),
            store = this.getViewModel().get("items"),
            queryparams = {},
            // url and format
            url = CMDBuildUI.util.api.Views.getPrintItemsUrl(this.getViewModel().get("objectTypeName"), format),
            // visibile columns
            columns = view.getVisibleColumns(),
            attributes = [],
            // apply sorters
            sorters = store.getSorters().getRange(),
            // filters
            filter = store.getAdvancedFilter();

        queryparams.extension = format;
        columns.forEach(function (c) {
            if (c.attributename) {
                attributes.push(c.attributename);
            }
        });
        queryparams.attributes = Ext.JSON.encode(attributes);

        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams), format);
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        // get value
        var searchTerm = field.getValue();
        if (searchTerm) {
            var store = field.lookupViewModel().get("items");
            if (store) {
                // add filter
                store.getAdvancedFilter().addQueryFilter(searchTerm);
                store.load();
            }
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
        var store = this.getViewModel().get("items");
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
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
     *
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onSavePreferencesBtnClick: function (tool, event) {
        var view = this.getView(),
            vm = view.lookupViewModel();
        //var grid = view.lookupReference(view.referenceGridId);
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(view, tool, vm.get("objectType"), vm.get("objectTypeName"));
    },

    /**
     * Disable print button before load
     *
     * @param {Ext.data.BufferedStore} store
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onStoreBeforeLoad: function (store, operation, eOpts) {
        this.getViewModel().set("disabledbuttons.print", true);
    },

    /**
     * Enable/disable print button on store load
     *
     * @param {Ext.data.BufferedStore} store
     * @param {Ext.data.Model[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onStoreLoad: function (store, records, successful, operation, eOpts) {
        var vm = this.getViewModel(),
            viewdata = CMDBuildUI.util.helper.ModelHelper.getViewFromName(vm.get('objectTypeName'));
        // enable or disable print button
        vm.set("disabledbuttons.print", !Ext.isEmpty(viewdata.get("_can_print")) ? !viewdata.get("_can_print") : false);
    }

});