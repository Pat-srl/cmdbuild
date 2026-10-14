Ext.define('CMDBuildUI.view.joinviews.configuration.items.FieldsetsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-fieldsets',
    control: {
        '#groupingsAttributesGrid': {
            beforeedit: 'onBeforeEdit',
            edit: 'onEdit'
        }
    },

    /**
     * 
     * @param {*} editor 
     * @param {*} context 
     * @param {*} eOpts 
     */
    onBeforeEdit: function (editor, context, eOpts) {
        var vm = editor.view.lookupViewModel();
        if (vm.get('actions.view')) {
            return false;
        }
    },

    /**
     * 
     * @param {*} editor 
     * @param {*} context 
     * @param {*} eOpts 
     */
    onEdit: function (editor, context, eOpts) {
        context.record.set('description', editor.editor.items.items[0].getValue());
        context.record.set('defaultDisplayMode', editor.editor.items.items[1].getValue());
        // fire event for refresh attributes grid 
        editor.getCmp().up('joinviews-configuration-main').fireEventArgs('attributegruopchanged', [context.record]);
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddGroupClick: function (grid, rowIndex, colIndex, button, event, record, rowEl) {
        var view = grid.up('fieldset');
        var vm = view.lookupViewModel();
        if (!record.get('description')) {
            return false;
        }
        var newStore = vm.get('attributeGroupsStoreNew');
        var store = vm.get('theView.attributeGroups');
        if (!record.get('name')) {
            record.set('name',record.get('description'));
        }
        record.set('index', record.get('index') || (store.data.max('index') || 0) + 1);
        newStore.remove(record);
        store.add(record);
        vm.set('attributeGroupingCount', store.data.length);
        newStore.add({});
        view.down('#groupingsAttributesGrid').view.grid.getView().refresh();
    },

    moveUp: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }

        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },

    moveDown: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }
        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },

    deleteRow: function (grid, rowIndex, colIndex, item, event, record, row) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        store.remove(record);
        grid.grid.reconfigure(store);
        grid.refresh();
        Ext.resumeLayouts();
        grid.up('joinviews-configuration-main').fireEventArgs('attributegruopremoved', [record]);
    }
});