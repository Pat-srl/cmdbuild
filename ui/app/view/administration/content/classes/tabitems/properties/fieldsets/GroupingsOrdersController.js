Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.GroupingsOrdersFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-groupingsordersfieldset',
    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],
    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#groupingsAttributesGrid': {
            edit: function (editor, context, eOpts) {
                context.record.set('description', editor.editor.items.items[0].getValue());
                context.record.set('defaultDisplayMode', editor.editor.items.items[1].getValue());
            },
            beforeedit: function (editor, context, eOpts) {
                var vm = editor.view.lookupViewModel();
                if (vm.get('actions.view')) {
                    return false;
                }
            }
        }
    },

    onAfterRender: function (view) {
        var fieldsetElement = view.down('fieldset').getEl();
        fieldsetElement.dom.dataset.testid = 'administration-grouping-fieldset';
    },
    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddGroupClick: function (grid, rowIndex, colIndex, button, event, record, rowEl) {
        var view = grid.up('administration-content-classes-view');
        var vm = view.getViewModel();
        if (!record.get('description')) {
            return false;
        }
        var newStore = vm.getStore('attributeGroupsStoreNew');
        var store = vm.getStore('attributeGroupsStore');
        if (!record.get('name')) {
            record.set('name', grid.lookupViewModel().get('objectTypeName') + ' ' + record.get('description'));
        }
        record.set('index', record.get('index') || (store.data.max('index') || 0) + 1);
        newStore.remove(record);
        store.add(record);
        vm.set('attributeGroupingCount', store.data.length);
        newStore.add({});
        view.down('#groupingsAttributesGrid').view.grid.getView().refresh();
    }
});