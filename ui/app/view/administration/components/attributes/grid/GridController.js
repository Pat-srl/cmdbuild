Ext.define('CMDBuildUI.view.administration.components.attributes.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-grid-grid',
    listen: {
        global: {
            attributeupdated: 'onAttributeUpdated',
            attributecreated: 'onAttributeCreated'
        }
    },

    control: {
        '#addattribute': {
            click: 'onNewBtnClick'
        },
        '#': {
            beforerender: 'onBeforeRender',
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
     * @param {CMDBuildUI.view.administration.components.attributes.grid.Grid} view 
     */
    onBeforeRender: function (view) {
        var vm = view.getViewModel(),
            objectType = vm.get('objectType').toLowerCase(),
            gridColumns = Ext.Array.merge([], view.getGridColumns());

        // if is dmsmodel attribute we need to remove some not needed columns
        if ([CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel, CMDBuildUI.util.helper.ModelHelper.objecttypes.domain].indexOf(objectType) > -1) {
            var columns = Ext.Array.filter(gridColumns, function (column) {
                return ['showInReducedGrid', 'hideInGrid'].indexOf(column.dataIndex) > -1;
            });
            Ext.Array.forEach(columns, function (column) {
                Ext.Array.remove(gridColumns, column);
            });
        }
        view.reconfigure(view.getStore(), gridColumns);
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
        var store = view.getStore();
        var filterCollection = vm.get("allAttributes").getFilters();
        view.getView().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        dropHandlers.wait = true;
        var sorters = store.getSorters();
        if (sorters.length && !(sorters.length === 1 && sorters.first().getId() === 'index')) {
            // by default allAttributes store have one filter for hide notes and idTenant attributes 
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.administration.common.messages.attention,
                CMDBuildUI.locales.Locales.administration.attributes.strings.removegridorders,
                function (btnText) {
                    dropHandlers.cancelDrop();
                    dropHandlers.wait = false;
                    if (btnText.toLowerCase() === 'yes') {
                        store.getSorters().removeAll();
                        store.setSorters('index');
                        store.getSorters().removeAll();
                    }
                    view.getView().unmask();
                }, this);

            dropHandlers.cancelDrop();
            dropHandlers.wait = false;

        } else if (filterCollection.length > 1) {
            // by default allAttributes store have one filter for hide notes and idTenant attributes 
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
            dropHandlers.wait = false;
            view.getView().unmask();

        } else {

            var moved = data.records[0].getId();
            var reference = overModel.getId();

            var attributes = vm.get('allAttributes').getData().getIndices();
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

            Ext.Array.forEach(sortableAttributes, function (_val, _key) {
                if (moved !== _val[0]) {
                    if (dropPosition === 'before' && reference === _val[0]) {
                        jsonData.push(moved);
                    }
                    jsonData.push(_val[0]);
                    if (dropPosition === 'after' && reference === _val[0]) {
                        jsonData.push(moved);
                    }
                }
            });

            var urlObjectTypePart = Ext.util.Inflector.pluralize(vm.get('objectType')).toLowerCase();
            if (vm.get('objectType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel) {
                urlObjectTypePart = 'dms/models';
            }
            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}/{1}/{2}/attributes/order',
                    CMDBuildUI.util.Config.baseUrl,
                    urlObjectTypePart,
                    vm.get('objectTypeName')
                ),
                method: 'POST',
                jsonData: jsonData,
                success: function (response) {
                    var res = Ext.JSON.decode(response.responseText);
                    if (res.success) {
                        dropHandlers.processDrop();
                        if (store.source) {
                            store.source.load();
                        } else {
                            store.load();
                        }
                    } else {
                        dropHandlers.cancelDrop();
                    }
                    dropHandlers.wait = false;
                    view.getView().unmask();
                },
                failure: function (response) {
                    dropHandlers.cancelDrop();
                    dropHandlers.wait = false;
                    view.getView().unmask();
                }
            });
        }


    },

    /**
     * @event change
     * Fires when the value of a field is changed. The value of a field is 
     * checked for changes when the field's setValue method 
     * is called and when any of the events listed in 
     * checkChangeEvents are fired.
     * @param {Ext.form.field.Field} field
     * @param {Boolean} newValue The new value
     * @param {Boolean} oldValue The original value
     */
    onIncludeInheritedChange: function (field, newValue, oldValue) {
        var vm = this.getViewModel();
        // check if grid have selected row
        var grid = this.getView();
        var selected = grid.getSelection()[0];
        if (selected) {
            var store = grid.getStore();
            var index = store.findExact("id", selected.getId());
            var storeItem = store.getById(selected.getId());
            var formInRowPlugin = grid.getPlugin('administration-forminrowwidget').view;
            // TODO: this is a workaround, find best method
            formInRowPlugin.fireEventArgs('togglerow', [grid, storeItem, index]);
        }
        // get attributes filter
        vm.bind({
            bindTo: '{allAttributes}',
            single: true
        }, function (allAttributes) {
            if (allAttributes) {
                var filterCollection = allAttributes.getFilters();
                if (newValue === true) {
                    // show all attributes
                    filterCollection.removeByKey('inheritedFilter');
                } else {
                    // remove all inherited attributes
                    filterCollection.add({
                        id: 'inheritedFilter',
                        property: 'inherited',
                        value: newValue
                    });
                }
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
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: {
                data: {
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributes: this.getView().getStore().getRange(),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    grid: Ext.copy(view)

                }
            }
        });
    },

    /**
     * 
     * @param {CMDBuildUI.model.Attribute} record 
     */
    onAttributeUpdated: function (view, record) {
        var _view = this.getView();
        var plugin = _view.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs('itemupdated', [_view, record, this]);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.Attribute} record 
     */
    onAttributeCreated: function (view, record) {
        var _view = this.getView();
        var store = _view.getStore();
        var index = store.findExact("_id", record.getId());
        _view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [_view, record, index]);
    }


});