Ext.define('Override.form.field.Text', {
    override: 'Ext.form.field.Text',

    /**
     * @override
     * 
     * @param {*} e 
     */
    onBlur: function (e) {
        var me = this;

        // if the field cannot be empty we need to trim the value otherwise
        // if the value contains only spaces it is considered valid.
        if (!me.allowBlank) {
            var value = me.getValue();
            if (value && Ext.isString(value)) {
                me.setValue(value.trim());
            }
        }

        me.callParent(e);
    }
});