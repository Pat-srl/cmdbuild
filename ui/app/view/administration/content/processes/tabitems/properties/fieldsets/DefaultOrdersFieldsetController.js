Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.DefaultOrdersFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',

    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],

    control: {
        '#defaultOrderGrid': {
            beforeedit: 'onBeforeEditDefaultOrderGrid',
            edit: 'editDefaultOrderGrid'
        }
    },

    /**
     *
     * @param {Ext.grid.plugin.Editing} editor
     * @param {Object} context
     * @param {Object} eOpts
     * @returns
     */
    onBeforeEditDefaultOrderGrid: function (editor, context, eOpts) {
        const vm = editor.view.lookupViewModel();
        if (vm.get('actions.view')) {
            return false;
        } else {
            context.record.set("editing", true);
        }
    },

    /**
     *
     * @param {Ext.grid.plugin.Editing} editor
     * @param {Object} context
     * @param {Object} eOpts
     */
    editDefaultOrderGrid: function (editor, context, eOpts) {
        context.record.set('direction', editor.editors.items[0].getValue());
    },

    /**
     *
     * @param {Ext.view.Table} view
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onAddNewDefaultOrderBtn: function (view, rowIndex, colIndex, item, e, record, row) {
        const mainView = this.getView();
        const attribute = mainView.down("#defaultOrderAttribute");
        const direction = mainView.down("#defaultOrderDirection");
        const orderGrid = mainView.down("#defaultOrderGrid");
        const orderStore = orderGrid.getStore();
        const newRecordStore = view.getStore();
        const required = [CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired];

        if (!direction.getValue()) {
            direction.markInvalid(required);
        }

        if (!attribute.getValue()) {
            attribute.markInvalid(required);
        }
        if (attribute.getValue() && direction.getValue()) {
            orderStore.add(CMDBuildUI.model.AttributeOrder.create({
                attribute: attribute.getValue(),
                direction: direction.getValue()
            }));
            newRecordStore.removeAll();
            newRecordStore.add(CMDBuildUI.model.AttributeOrder.create({ direction: 'ascending' }));
            orderGrid.getView().refresh();
        }
    }

});