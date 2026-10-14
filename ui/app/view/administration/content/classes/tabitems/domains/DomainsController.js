Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-domains-domains',
    requires: ['CMDBuildUI.model.domains.Domain'],

    control: {
        '#': {
            afterrender: 'onAfterRender',
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick',
            drop: 'onDrop'
        },
        '#adddomain': {
            click: 'onAddDomainClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        }
    },

    // create domains rows
    onAfterRender: function (view) {
        var objectType = view.getViewModel().get('objectTypeName');
        if (!Ext.isEmpty(objectType)) {
            view.setStore(Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.domains.Domain',
                alias: 'store.classdomain-store',
                proxy: {
                    type: 'baseproxy',
                    url: Ext.String.format('/classes/{0}/domains', objectType),
                    extraParams: {
                        ext: true,
                        detailed: true
                    }
                },
                pageSize: 0,
                autoLoad: true,
                autoDestroy: true
            }).load());
        }

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
        this.view.setSelection(record);
        Ext.GlobalEvents.fireEventArgs('selecteddomain', [record]);
    },

    onDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        // Defer the handling
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var view = this.getView();
        var domains = view.getStore().getRange();
        var domainsOrder = [];
        Ext.Array.forEach(domains, function (item) {
            domainsOrder.push(item.get('name'));
        });
        var theObject = vm.get('theObject');
        Ext.apply(theObject.data, theObject.getAssociatedData());
        theObject.set('domainOrder', domainsOrder);

        theObject.save({
            callback: function () {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });

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
        var filterCollection = grid.getStore().getFilters();
        var filter = function (item) {
            return item.get('source') === vm.get('objectTypeName') || item.get('destination') === vm.get('objectTypeName');
        };
        if (newValue === true && !oldValue) {
            // show all domains
            filterCollection.removeAll();
            grid.view.getPlugin('gridviewdragdrop').enable();
        } else {
            // remove all inherited domains
            grid.view.getPlugin('gridviewdragdrop').disable();
            filterCollection.add(filter);
        }
    },
    onAddDomainClick: function (button, event, eOpts) {

        var view = this.getView();

        var grid = view.up().down('administration-content-classes-tabitems-domains-domains');
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        var theDomain = CMDBuildUI.model.domains.Domain.create();
        container.add({
            xtype: 'administration-content-domains-tabitems-properties-properties',
            viewModel: {
                links: {
                    theDomain: {
                        type: 'CMDBuildUI.model.domains.Domain',
                        create: true
                    }
                },

                data: {
                    theDomain: theDomain,
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.localizations.domain,
                        theDomain.get('name')),
                    grid: grid,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    objectTypeName: theDomain.get('name')
                }
            }
        });
    },
    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.get('searchdomain.value');
        var store = this.getView().getStore();
        store.clearFilter();
        if (searchTerm) {
            var regex = RegExp(searchTerm, 'i');
            store.filter(function (record) {
                var data = record.getData();
                var result = false;
                Ext.Object.each(data, function (property, value) {
                    result = result || regex.test(String(value));
                });

                return result;
            });
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

        // clear store filter
        var store = this.getView().getStore();
        store.clearFilter();
        // reset input
        field.reset();
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },



    // TODO: These functions are unused for now.
    onEditBtnClick: function (button, event, eOpts) {
        // TODO: disable plugin forminrowwidget

        this.getView().getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);


        container.removeAll();
        var theDomain = record;
        container.add({
            xtype: 'administration-content-domains-tabitems-properties-properties',
            viewModel: {
                data: {
                    theDomain: theDomain,
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.localizations.domain,
                        theDomain.get('name')),
                    grid: row.ownerGrid,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    objectTypeName: theDomain.get('name')
                }
            }
        });
    }
});