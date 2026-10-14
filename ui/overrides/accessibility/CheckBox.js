Ext.define('Overrides.accessibility.Checkbox', {
    override: 'Ext.form.field.Checkbox',

    afterRender: function () {
        this.callParent(arguments);
        const el = this.inputEl;
        if (!el) {
            return;
        }

        // aria-label
        if (!el.dom.hasAttribute('aria-label')) {
            let labelText = this.fieldLabel || this.boxLabel || '';

            if (!labelText) {
                const labelEl = this.el.up('.x-form-fieldcontainer');
                if (labelEl) {
                    const labelTextEl = labelEl.down('.x-form-item-label-text');
                    if (labelTextEl) {
                        labelText = labelTextEl.dom.textContent.trim();
                    }
                }
            }

            if (labelText) {
                el.dom.setAttribute('aria-label', labelText);
            }
        }

        // aria-checked
        if (!el.dom.hasAttribute('aria-checked')) el.dom.setAttribute('aria-checked', false);

        // tabindex
        if (!el.dom.hasAttribute('tabindex')) el.dom.setAttribute('tabindex', 0);

        // sync aria-checked on state change
        this.on('change', function (field, newValue) {
            el.dom.setAttribute('aria-checked', newValue);
        });
    }
});
