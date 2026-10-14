Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.domains.DomainsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-domains-domains',
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
        }
    },
    onAfterRender: function (view) {
        var objectType = view.getViewModel().get('objectTypeName');
        if (!Ext.isEmpty(objectType)) {
            view.setStore(Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.domains.Domain',
                alias: 'store.classdomain-store',
                proxy: {
                    type: 'baseproxy',
                    url: Ext.String.format('/processes/{0}/domains', objectType),
                    extraParams: {
                        ext: true,
                        detailed: true
                    }
                },
                autoLoad: true,
                autoDestroy: true
            }).load());
        }

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
        var theProcess = vm.get('theProcess');
        Ext.apply(theProcess.data, theProcess.getAssociatedData());
        theProcess.set('domainOrder', domainsOrder);

        theProcess.save({
            callback: function () {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
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
        this.view.setSelection(record);
    },

    onAddDomainClick: function (button, event, eOpts) {

        var view = this.getView();

        var grid = view.up().down('administration-content-processes-tabitems-domains-domains');
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
    }
});