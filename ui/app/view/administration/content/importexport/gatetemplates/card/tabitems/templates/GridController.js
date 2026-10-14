Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.GridController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardMixin'],
    alias: 'controller.administration-content-importexport-gatetemplates-tabitems-templates-grid',
    control: {
        '#': {
            afterrender: 'onAfterRender',
            sortchange: 'onSortChange',
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick',
            removetemplate: 'onRemoveTemplate'
        },
        tableview: {
            beforedrop: 'onBeforeDrop'
        }
    },

    onAfterRender: function (view) {
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();
        CMDBuildUI.util.Stores.loadEmailAccountsStore();
        CMDBuildUI.util.Stores.loadEmailTemplatesStore();

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
        this.getViewModel().set('selected', record);
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
    },

    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        // Defer the handling
        var vm = this.getViewModel();
        var view = this.getView();
        var store = view.getStore();
        var filterCollection = store.getFilters();
        view.getView().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        dropHandlers.wait = true;
        if (store.getSorters().length && store.getSorters().first().getProperty('field') !== 'index') {
            // by default allGateTemplates store have one filter on index

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
            return;
        } else

        if (filterCollection.length > 1) {
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
            return;
        }


        var moved = data.records[0].getId();
        var reference = overModel.getId();

        var templatesIndices = vm.get('allGateTemplates').getData().getIndices();
        var sortableTemplates = [];
        for (var key in templatesIndices) {
            if (templatesIndices.hasOwnProperty(key)) {
                sortableTemplates.push([key, templatesIndices[key]]); // each item is an array in format [key, value]
            }
        }

        // sort items by value
        sortableTemplates.sort(function (a, b) {
            return a[1] - b[1]; // compare numbers
        });

        var sortedIds = [];
        var sortedNames = [];
        Ext.Array.forEach(sortableTemplates, function (val, templateKey) {
            if (moved !== val[0]) {
                if (dropPosition === 'before' && reference === val[0]) {
                    sortedIds.push(moved);
                }
                sortedIds.push(val[0]);
                if (dropPosition === 'after' && reference === val[0]) {
                    sortedIds.push(moved);
                }
            }
        });

        Ext.Array.forEach(sortedIds, function (id) {
            var record = store.findRecord('_id', id);
            sortedNames.push(record.get('code'));
        });
        var handler = vm.get('theGate').handlers().first();
        handler.setTemplates(sortedNames);
        delete handler.data._id;
        delete handler.data._shape_import_include_or_exclude;
        delete handler.data._shape_import_target_attr_description;
        delete handler.data._shape_import_key_attr_description;
        vm.get('theGate').save({
            success: function (record, operation) {
                dropHandlers.processDrop();
                dropHandlers.wait = false;
                view.getView().unmask();
                store.setSorters('index');
            },
            failure: function () {
                dropHandlers.cancelDrop();
                dropHandlers.wait = false;
                view.getView().unmask();
            }
        });

    },


    onRemoveTemplate: function (record, grid) {
        var vm = this.getView().lookupViewModel();
        var theGate = vm.get('theGate');
        var handler = theGate.handlers().first();
        handler.removeTemplate(record.get('code'));
    }


});