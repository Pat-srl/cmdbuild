Ext.define('Override.form.Basic', {
    override: 'Ext.form.Basic',

    /**
     * @override
     * 
     * Check whether the validity of the entire form has changed since it was last checked, and
     * if so fire the {@link #validitychange validitychange} event. This is automatically invoked
     * when an individual field's validity changes.
     */
    checkValidity: function () {
        var me = this,
            valid;

        if (me.destroyed) {
            return;
        }

        // Use me.isValid instead of !me.hasInvalidField which 
        // does not trigger properly vialiditychange event
        valid = me.isValid();

        if (valid !== me.wasValid) {
            me.onValidityChange(valid);
            me.fireEvent('validitychange', me, valid);
            me.wasValid = valid;
        }
    }
});