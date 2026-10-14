Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabitems-values-grid-grid',
    listen: {
        global: {
            lookupvalueupdated: 'onLookupValueUpdated',
            lookupvaluecreated: 'onAttributeCreated'
        }
    },

    control: {
        '#addlookupvalue': {
            click: 'onNewBtnClick'
        },
        '#': {
            sortchange: 'onSortChange'
        },
        tableview: {
            deselect: 'onDeselect',
            select: 'onSelect',
            beforedrop: 'onBeforeDrop'
        }
    },
    /**
     * 
     * @param {Ext.grid.header.Container} ct 
     * @param {Ext.grid.column.Column} column 
     * @param {String} direction 
     * @param {Object} eopts 
     */
    onSortChange: function (ct, column, direction, eopts) {
        var grid = ct.grid;
        var selected = grid.getSelection()[0];
        if (selected) {
            var store = grid.getStore();
            var index = store.findExact("id", selected.getId());
            var storeItem = store.getById(selected.getId());
            var formInRowPlugin = grid.getPlugin('administration-forminrowwidget').view;
            // TODO: this is a workaround, find best method
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
        }

    },

    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        // Defer the handling
        var vm = this.getViewModel();
        var view = this.getView();

        var filterCollection = vm.get("allValues").getFilters();
        view.getView().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        dropHandlers.wait = true;
        // by default allAttributes store have one filter for hide notes and idTenant attributes 
        if (filterCollection.length > 0) {
            var w = Ext.create('Ext.window.Toast', {
                ui: 'administration',
                width: 250,
                title: CMDBuildUI.locales.Locales.administration.common.messages.attention,
                html: CMDBuildUI.locales.Locales.administration.common.messages.cannotsortitems,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-circle', 'solid'),
                align: 'br'
            });
            w.show();
            dropHandlers.cancelDrop();
            view.getView().unmask();

        } else {

            var moved = data.records[0].getId();
            var reference = overModel.getId();

            var attributes = vm.get('allValues').getData().getIndices();
            var sortableAttributes = [];
            for (var key in attributes) {
                if (attributes.hasOwnProperty(key)) {
                    sortableAttributes.push([key, attributes[key]]); // each item is an array in format [key, value]
                }
            }

            // sort items by value
            sortableAttributes.sort(function (a, b) {
                return a[1] - b[1]; // compare numbers
            });

            var jsonData = [];
            Ext.Array.forEach(sortableAttributes, function (val, key) {
                if (moved !== val[0]) {
                    if (dropPosition === 'before' && reference === val[0]) {
                        jsonData.push(moved);
                    }
                    jsonData.push(val[0]);
                    if (dropPosition === 'after' && reference === val[0]) {
                        jsonData.push(moved);
                    }
                }
            });

            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}order',
                    CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(vm.get('objectTypeName'))
                ),
                method: 'POST',
                jsonData: jsonData,
                success: function (response) {
                    var res = JSON.parse(response.responseText);

                    if (res.success) {
                        view.getView().grid.getStore().load();
                        dropHandlers.processDrop();
                    } else {
                        dropHandlers.cancelDrop();
                    }
                    view.getView().unmask();
                },
                error: function (response) {
                    dropHandlers.cancelDrop();
                    view.getView().unmask();
                }
            });
        }

    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} e 
     * @param {Object} eOpts 
     */
    onSortMenuItemClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel(),
            grid = button.up('grid'),
            store = grid.getStore(),
            data = store.getRange(),
            sorting = button.sorting;

        grid.getView().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);

        var sortedData = data.sort(function (a, b) {
            if ((a && a.get(sorting.attribute)) && (b && b.get(sorting.attribute))) {
                var nameA = a.get(sorting.attribute).toUpperCase(); // ignore upper and lowercase
                var nameB = b.get(sorting.attribute).toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return sorting.direction === 'ASC' ? -1 : 1;
                }
                if (nameA > nameB) {
                    return sorting.direction === 'ASC' ? 1 : -1;
                }
                return 0;
            }
        });
        var ids = Ext.Array.pluck(sortedData, 'id');

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/lookup_types/{1}/values/order',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('objectTypeName')
            ),
            method: 'POST',
            jsonData: ids,
            callback: function (response) {
                store.load();
                grid.getView().unmask();
            }
        });

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

    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {

        var grid = this.getView();
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        var lookupTypeName = vm.getParent().getParent().get('objectTypeName');
        var proxy = CMDBuildUI.model.dms.DMSCategory.getProxy();
        proxy.setUrl(CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(lookupTypeName));
        container.add({
            xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-card',
            viewModel: {
                links: {
                    theValue: {
                        type: 'CMDBuildUI.model.dms.DMSCategory',
                        create: true
                    }
                },
                data: {
                    actions: {
                        edit: false,
                        add: true,
                        view: false
                    },
                    lookupTypeName: lookupTypeName,
                    values: grid.getStore().getRange(),
                    title: Ext.String.format(
                        '{0} - {1}',
                        lookupTypeName,
                        CMDBuildUI.locales.Locales.administration.tasks.value
                    ),
                    grid: grid
                }
            }
        });
    },

    onLookupValueUpdated: function (record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view, record, this, true]);
    },

    onAttributeCreated: function (record) {
        var view = this.getView();
        var store = view.getStore();
        var index = store.findExact("_id", record.getId());
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [view, record, index]);
    }
});