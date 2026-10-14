Ext.define('CMDBuildUI.components.picker.patternpicker.PatternPicker', {
    extend: 'Ext.form.field.Picker',

    alias: 'widget.cmdbuild-patternpicker',
    requires: ['Ext.picker.Color'],

    editable: true,
    config: {
        picker: null,
        patterns: CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesFillPatterns()
    },

    /**
     * @override
     *
     * Create the pattern picker
     */
    createPicker: function () {
        const me = this;
        let picker;
        if (!me.picker) {
            picker = Ext.create('CMDBuildUI.components.picker.patternpicker.Pattern', {
                value: me.getValue(),
                renderTo: Ext.bodyEl,
                patterns: this.getPatterns(),
                floating: true,
                style: {
                    height: 'auto'
                },
                listeners: {
                    select: {
                        fn: me.onPatternSelect,
                        scope: me
                    }
                }
            });
            this.setPicker(picker);
        }

        return me.getPicker();
    },

    /**
     * @param {CMDBuildUI.components.picker.patternpicker.PatternPicker} colorPicker
     * @param {Object} pattern
     */
    onPatternSelect: function (patternPicker, pattern) {
        this.setValue(pattern);
        this.fireEvent('change', this, pattern);
        this.collapse();
    },

    valueToRaw: function (value) {
        if (!value) {
            return '';
        }
        return value.id || '';
    },

    rawToValue: function () {
        return this.value;
    },

    setValue: function (value) {
        this.callParent([value]);
        this.setRawValue(this.valueToRaw(value));
        return this;
    },

    /**
     * @override
     *
     * Collapses this field's picker dropdown.
     */
    collapse: function () {
        var me = this,
            openCls = me.openCls,
            aboveSfx = '-above',
            picker;

        if (me.isExpanded && !me.destroyed && !me.destroying) {
            picker = me.picker;

            // hide the picker and set isExpanded flag
            picker.hide();
            me.isExpanded = false;

            // remove the openCls
            me.bodyEl.removeCls([openCls, openCls + aboveSfx]);

            if (picker.el) {
                picker.el.removeCls(picker.baseCls + aboveSfx);
            }

            if (!me.ariaStaticRoles[me.ariaRole]) {
                me.ariaEl.dom.setAttribute('aria-expanded', false);
            }

            // remove event listeners
            me.touchListeners.destroy();
            me.scrollListeners.destroy();

            Ext.un('resize', me.alignPicker, me);
            me.fireEvent('collapse', me);

            me.onCollapse();
        }
    }
});
