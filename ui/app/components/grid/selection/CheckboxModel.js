Ext.define('CMDBuildUI.components.grid.selection.CheckboxModel', {
    alias: 'selection.cmdbuildcheckboxmodel',
    extend: 'Ext.selection.CheckboxModel',

    /**
     * @override
     * Retrieve a configuration to be used in a HeaderContainer.
     * This is called when injectCheckbox is not `false`.
     *
     * @returns
     */
    getHeaderConfig: function () {
        var config = this.callParent(arguments);
        config.renderer = function (value, cell, record, rowIndex, colIndex, store, view) {
            if (store.buffered && cell.column.xtype === "checkcolumn") {
                view.getHeaderAtIndex(colIndex).setHeaderCheckbox(false);
            }
            var config = this.defaultRenderer(value, cell);
            view.fireEvent('selectioncheckrendered', value, cell, record, rowIndex, colIndex, store, this, view);
            return config;
        };
        return config;
    }
});