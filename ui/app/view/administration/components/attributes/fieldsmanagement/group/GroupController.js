Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.GroupController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-fieldsmanagement-group-group',

    control: {
        '#': {
            drop: 'onDrop',
            autogenerate: 'onAutogenerate'
        },
        '#addrowBtn': {
            click: 'onAddrowBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onAddrowBtnClick: function (button) {
        var grid = this.getView().down('components-grid-reorder-grid');
        var rows = grid.getStore();
        rows.add({
            "columns": [{

                "fields": [

                ]
            }, {
                "fields": [

                ]
            }]
        });

        this.getView().updateGroupAndRefresh(true);
    },

    moveUp: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex - 1, current);
        view.refresh();
        this.getView().updateGroupAndRefresh(true);
    },

    moveDown: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex + 1, current);
        this.getView().updateGroupAndRefresh(true);
    },

    deleteRow: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        // get all columns in row       
        var columns = store.getAt(rowIndex).get('columns');
        Ext.Array.forEach(columns, function (column) {
            // get all fields in column
            if (column.fields.length) {
                Ext.Array.forEach(column.fields, function (field) {
                    // find attribute record
                    var fieldset = view.up('fieldset');
                    var vm = fieldset.lookupViewModel();
                    var allGroupAttributesStore = vm.get('attributesStore');
                    var attribute = allGroupAttributesStore.findRecord('name', field.attribute);
                    attribute.set('descriptionWithName', attribute.getDescriptionWithName());
                    // add attribute to free attributes store
                    fieldset
                        .down('#freeattributes')
                        .down('administration-components-attributes-fieldsmanagement-group-form-column')
                        .getStore()
                        .add(attribute);
                });
            }
        });
        // add fields to all free attributes
        store.removeAt(rowIndex);
        this.getView().updateGroupAndRefresh(true);
    },

    addColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {
            var rowRec = view.up('administration-components-attributes-fieldsmanagement-group-group').getGroup().get('rows')[rowIndex] || record;
            var columns = rowRec.columns || rowRec.get('columns');
            if (columns.length < 4) {
                columns.push({
                    fields: []
                });
                record.set('width', undefined);
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                this.getView().updateGroupAndRefresh(true);
            }
        }
    },

    removeColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {
            var rowRec = view.up('administration-components-attributes-fieldsmanagement-group-group').getGroup().get('rows')[rowIndex] || record;
            var columns = rowRec.columns || rowRec.get('columns');
            if (columns.length > 1) {
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                var wasRemoved = false;
                for (var i = 0; i < columns.length; i++) {
                    var isEmpty = !columns[i].fields.length;
                    if (isEmpty) {
                        Ext.Array.removeAt(columns, i);
                        wasRemoved = true;
                        break;
                    }
                }
                if (!wasRemoved) {
                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.administration.attributes.texts.unabletoremovenotemptycolum);
                    return;
                }
                record.set('width', undefined);
                this.getView().updateGroupAndRefresh(true);
            }
        }
    },

    onDrop: function (node, data, overModel, dropPosition, eOpts) {
        data.view.refresh();
        this.view.ownerGrid.refreshIndex(data.view.getStore());
    },

    columnsSizeBtnClick: function (button) {
        var me = button.up().ctrl;
        var values = button.value;
        var record = button.up().record;
        Ext.Array.forEach(record.get('columns'), function (item, index) {
            item.width = values[index];
        });

        me.getView().updateGroupAndRefresh(true);
    },

    /**
     * 
     * @param {Booleafn} removeLoader 
     */
    onAutogenerate: function (removeLoader) {

        var view = this.getView();
        var fieldset = view.up('fieldset');


        var fieldsetVm = fieldset.lookupViewModel();
        var allGroupAttributesStore = fieldsetVm.get('attributesStore');
        var attributeStore = fieldset
            .down('#freeattributes')
            .down('administration-components-attributes-fieldsmanagement-group-form-column')
            .getStore();
        var grid = view.down('components-grid-reorder-grid');
        var rows = grid.getStore();
        var rowsNeeded = Math.ceil(attributeStore.count() / 2);

        var removeFreeAttribute = function (attributeName) {
            var attribute = allGroupAttributesStore.findRecord('name', attributeName);
            attribute.set('descriptionWithName', attribute.getDescriptionWithName());
            attributeStore.remove(attribute);
        };
        for (var i = 0; i <= rowsNeeded; i++) {

            var attr1 = attributeStore.getAt(0);
            var attr2 = attributeStore.getAt(1);
            var fields1 = [];
            var fields2 = [];
            if (attr1) {
                fields1 = [{
                    attribute: attr1.get('name'),
                    descriptionWithName: attr1.get('descriptionWithName')
                }];
                removeFreeAttribute(attr1.get('name'));
            }
            if (attr2) {
                fields2 = [{
                    attribute: attr2.get('name'),
                    descriptionWithName: attr2.get('descriptionWithName')
                }];
                removeFreeAttribute(attr2.get('name'));
            }
            if (fields1.length) {
                rows.add({
                    "columns": [{
                        "fields": fields1
                    }, {
                        "fields": fields2
                    }]
                });
            }
        }
        view.updateGroupAndRefresh(removeLoader);
    }
});