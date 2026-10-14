Ext.define('CMDBuildUI.view.joinviews.configuration.items.DataSortingController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-datasorting',

    control: {
        '#': {
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange'
        },
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
     */
    onBeforeEditDefaultOrderGrid: function (editor, context, eOpts) {
        context.record.set("editing", true);
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
                property: attribute.getValue(),
                direction: direction.getValue()
            }));
            newRecordStore.removeAll();
            newRecordStore.add(CMDBuildUI.model.AttributeOrder.create({
                direction: 'ASC'
            }));
            orderGrid.getView().refresh();
        }
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    moveUp: function (grid, rowIndex, colIndex, item, e, record, row) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }

        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    moveDown: function (grid, rowIndex, colIndex, item, e, record, row) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }
        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    deleteRow: function (grid, rowIndex, colIndex, item, event, record, row) {
        var store = grid.getStore();
        store.remove(record);
        grid.grid.reconfigure(store);
        grid.refresh();
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input
     * @param {String} newValue
     * @param {String} oldValue
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        this.getView().down("#defaultOrderGrid").getView().refresh();
    },

    /**
     * @event
     * @param {Ext.data.Model} record
     * @param {Object} context
     * @param {Object} eOpts
     */
    onDomainChange: function (record, context, eOpts) {
        this.getView().down("#defaultOrderGrid").getView().refresh();
    }

});