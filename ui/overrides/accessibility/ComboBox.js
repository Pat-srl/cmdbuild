Ext.define('Overrides.accessibility.ComboBox', {
    override: 'Ext.form.field.ComboBox',
    afterRender: function () {
        this.callParent(arguments);
        const el = this.inputEl;
        if (!el) {
            return;
        }

        // aria-label
        if (
            !el.dom.hasAttribute('aria-label') &&
            !el.dom.hasAttribute('aria-labelledby') &&
            !el.dom.hasAttribute('title')
        ) {
            let label = 'Combobox';
            if (this.ownerCt && this.ownerCt.fieldLabel) {
                label = this.ownerCt.fieldLabel;
            }
            el.dom.setAttribute('aria-label', label);
        }

        // tabindex
        if (!el.dom.hasAttribute('tabindex')) {
            el.dom.setAttribute('tabindex', 0);
        }
    }
});
