Ext.define('Overrides.grid.CellEditor', {
    override: 'Ext.grid.CellEditor',

    /**
     * @override
     *
     */
    completeEdit: function (remainVisible) {
        var me = this,
            context = me.context;
        if (me.editing) {
            context.value = me.field.getValue();
            if (!me.editingPlugin.skipValidationOnFocusOut && me.editingPlugin.validateEdit(context) === false) {
                if (context.cancel) {
                    context.value = me.originalValue;
                    me.editingPlugin.cancelEdit();
                }
                return !!context.cancel;
            }
        }
        me.callSuper([remainVisible]);
    }
});
