Ext.define('Overrides.grid.column.Check', {
    override: 'Ext.grid.column.Check',

    sortable: true,

    /**
     * @override
     * 
     * @param {*} headerCheckbox 
     */
    updateHeaderCheckbox: function (headerCheckbox) {
        var me = this,
            cls = Ext.baseCSSPrefix + 'column-header-checkbox';

        if (headerCheckbox) {
            me.addCls(cls);

            // Allow sort if configured
            // me.sortable = false;

            if (me.useAriaElements) {
                me.updateHeaderAriaDescription(me.areAllChecked());
            }
        }
        else {
            me.removeCls(cls);

            if (me.useAriaElements && me.ariaEl.dom) {
                me.ariaEl.dom.removeAttribute('aria-describedby');
            }
        }

        // Keep the header checkbox up to date
        me.updateHeaderState();
    }

});