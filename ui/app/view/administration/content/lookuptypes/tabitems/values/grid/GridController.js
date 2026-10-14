Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-grid-grid',

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
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy',
            sortchange: 'onSortChange'
        },
        'tableview': {
            beforedrop: 'onBeforeDrop',
            rowdblclick: 'onRowDblClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.Grid} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        // It is necessary to set itemId to its initial value, otherwise the interface will crash
        view.itemId = 'lookupValuesGrid';
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const gridItemId = vm.get('gridItemId');
        if (gridItemId) {
            view.itemId = gridItemId;
            view.down('localsearchfield').setGridItemId('#' + gridItemId);
        }
    },

    /**
     *
     * @param {Ext.view.Table} row
     * @param {Ext.data.Model} record
     * @param {HTMLElement} element
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (row, record, element, rowIndex, e, eOpts) {
        const vm = this.getViewModel();
        const theLookupType = vm.get('theLookupType');
        const toolAction = vm.get('toolAction');
        const parentLookupsStore = vm.get('parentLookupsStore');
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card',
            viewModel: {
                data: {
                    theValue: record,
                    actions: {
                        add: false,
                        edit: true,
                        view: false
                    },
                    theLookupType: theLookupType,
                    toolAction: toolAction,
                    parentLookupsStore: parentLookupsStore
                }
            }
        });
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onLoadLookupValues: function (store, records, successful, operation, eOpts) {
        Ext.Array.forEach(records, function (item, index, allitems) {
            const icon_font = item.get('icon_font') ? item.get('icon_font').split(':') : [];
            item.set(
                'icon_font',
                CMDBuildUI.util.helper.IconHelper.getIconId(icon_font[0], icon_font.length > 1 ? icon_font[1] : '')
            );
            item.set('icon_description', CMDBuildUI.util.helper.IconHelper.getIconDescription(icon_font[0]));
        });
    },

    /**
     *
     * @param {Ext.grid.header.Container} ct
     * @param {Ext.grid.column.Column} column
     * @param {String} direction
     * @param {Object} eopts
     */
    onSortChange: function (ct, column, direction, eopts) {
        const grid = ct.grid;
        const selected = grid.getSelection()[0];
        if (selected) {
            const store = grid.getStore();
            const index = store.findExact('id', selected.getId());
            const storeItem = store.getById(selected.getId());
            const formInRowPlugin = grid.getPlugin('administration-forminrowwidget').view;
            // TODO: this is a workaround, find best method
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
        }
    },

    /**
     *
     * @param {HTMLElement} node
     * @param {Object} data
     * @param {Ext.data.Model} overModel
     * @param {String} dropPosition
     * @param {Object} dropHandlers
     * @param {Object} eOpts
     */
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers, eOpts) {
        // Defer the handling
        const vm = this.getViewModel();
        const view = this.getView();
        const tableView = view.getView();
        const allValues = vm.get('allValues');

        tableView.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        dropHandlers.wait = true;

        // by default allAttributes store have one filter for hide notes and idTenant attributes
        if (allValues.getFilters().length > 0) {
            const w = Ext.create('Ext.window.Toast', {
                ui: 'administration',
                width: 250,
                title: CMDBuildUI.locales.Locales.administration.common.messages.attention,
                html: CMDBuildUI.locales.Locales.administration.common.messages.cannotsortitems,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-circle', 'solid'),
                align: 'br'
            });
            w.show();
            dropHandlers.cancelDrop();
            tableView.unmask();
        } else {
            const moved = data.records[0].getId();
            const reference = overModel.getId();
            const attributes = vm.get('allValues').getData().getIndices();
            const sortableAttributes = [];

            Ext.Array.forEach(Ext.Object.getKeys(attributes), function (k) {
                if (attributes.hasOwnProperty(k)) {
                    sortableAttributes.push([k, attributes[k]]); // each item is an array in format [key, value]
                }
            });

            // sort items by value
            sortableAttributes.sort(function (a, b) {
                return a[1] - b[1]; // compare numbers
            });

            const jsonData = [];
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
                    '{0}/lookup_types/{1}/values/order',
                    CMDBuildUI.util.Config.baseUrl,
                    CMDBuildUI.util.Utilities.stringToHex(vm.get('theLookupType.name'))
                ),
                method: 'POST',
                jsonData: jsonData,
                success: function (response) {
                    if (Ext.JSON.decode(response.responseText).success) {
                        tableView.grid.getStore().load();
                        dropHandlers.processDrop();
                    } else {
                        dropHandlers.cancelDrop();
                    }
                    tableView.unmask();
                },
                error: function (response) {
                    dropHandlers.cancelDrop();
                    tableView.unmask();
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
        const grid = button.up('grid');
        const store = grid.getStore();
        const sorting = button.sorting;
        const gridView = grid.getView();

        gridView.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);

        const sortedData = store.getRange().sort(function (a, b) {
            if (a && a.get(sorting.attribute) && b && b.get(sorting.attribute)) {
                const nameA = a.get(sorting.attribute).toUpperCase(); // ignore upper and lowercase
                const nameB = b.get(sorting.attribute).toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return sorting.direction === 'ASC' ? -1 : 1;
                }
                if (nameA > nameB) {
                    return sorting.direction === 'ASC' ? 1 : -1;
                }
                return 0;
            }
        });
        const ids = Ext.Array.pluck(sortedData, 'id');

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/lookup_types/{1}/values/order',
                CMDBuildUI.util.Config.baseUrl,
                CMDBuildUI.util.Utilities.stringToHex(this.getViewModel().get('theLookupType.name'))
            ),
            method: 'POST',
            jsonData: ids,
            callback: function () {
                store.load();
                gridView.unmask();
            }
        });
    },

    /**
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        const vm = this.getViewModel();
        const theLookupType = vm.get('theLookupType');
        const toolAction = vm.get('toolAction');
        const parentLookupsStore = vm.get('parentLookupsStore');
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card',
            viewModel: {
                links: {
                    theValue: {
                        type: 'CMDBuildUI.model.lookups.Lookup',
                        create: true
                    }
                },
                data: {
                    actions: {
                        edit: false,
                        add: true,
                        view: false
                    },
                    theLookupType: theLookupType,
                    toolAction: toolAction,
                    parentLookupsStore: parentLookupsStore
                }
            }
        });
    },

    /**
     *
     * @param {Ext.data.Model} record
     */
    onLookupValueUpdated: function (record) {
        const view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view, record, this, true]);
    },

    /**
     *
     * @param {Ext.data.Model} record
     */
    onAttributeCreated: function (record) {
        const view = this.getView();
        const index = view.getStore().findExact('_id', record.getId());
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [view, record, index]);
    }
});
