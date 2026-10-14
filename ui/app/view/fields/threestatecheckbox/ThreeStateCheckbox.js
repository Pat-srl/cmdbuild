Ext.define('CMDBuildUI.view.fields.threestatecheckbox.ThreeStateCheckbox', {
    extend: 'Ext.form.field.Checkbox',
    alias: 'widget.threestatecheckboxfield',

    config: {
        /**
         * @cfg {Boolean} disableLabelClick
         *
         * If true, the component will deny the edit value when the label is clicked
         */
        disableLabelClick: false
    },

    focusCls: '',

    /**
     * @private
     */
    fieldBodyCls: Ext.baseCSSPrefix + 'form-tscb-wrap',

    /**
     * @cfg {String} [checkedCls='x-form-tscb-checked']
     * The CSS class(es) added to the component's main element when it is in the checked state.
     * You can add your own class (checkedCls='myClass x-form-cb-checked') or replace the default
     * class altogether (checkedCls='myClass').
     */
    checkedCls: Ext.baseCSSPrefix + 'form-tscb-checked',

    /**
     * @cfg {String} [checkedCls='x-form-tscb-unchecked']
     * The CSS class(es) added to the component's main element when it is in the checked state.
     * You can add your own class (checkedCls='myClass x-form-cb-checked') or replace the default
     * class altogether (checkedCls='myClass').
     */
    uncheckedCls: Ext.baseCSSPrefix + 'form-tscb-unchecked',

    /**
     * @cfg {String} [boxLabelCls='x-form-tscb-label']
     * The CSS class to be applied to the {@link #boxLabel} element
     */
    boxLabelCls: Ext.baseCSSPrefix + 'form-tscb-label',

    afterLabelCls: Ext.baseCSSPrefix + 'form-tscb-after',

    wrapInnerCls: Ext.baseCSSPrefix + 'form-tscb-wrap-inner',

    noBoxLabelCls: Ext.baseCSSPrefix + 'form-tscb-no-box-label',

    inputCls: Ext.baseCSSPrefix + 'form-tscb',
    _checkboxCls: Ext.baseCSSPrefix + 'form-tscb-input',

    changeEventName: 'click',

    initValue: function () {
        var me = this,
            checked = me.checked;

        /**
         * @property {Object} originalValue
         * The original value of the field as configured in the {@link #checked} configuration, or as loaded by the last
         * form load operation if the form's {@link Ext.form.Basic#trackResetOnLoad trackResetOnLoad} setting is `true`.
         */
        me.originalValue = me.initialValue = me.lastValue = checked;

        // Set the initial checked state
        me.setValue(checked);
    },

    initEvents: function () {
        var me = this;

        me.callParent();

        if (me.getDisableLabelClick()) {
            me.labelEl.on('click', function (event) {
                event.preventDefault();
                return false;
            });
        }

        if (Ext.isAndroid) {
            me.el.on('click', function () {
                me.updateValueFromDom();
            });
        }
    },

    getSubTplData: function (fieldData) {
        var me = this;
        var data = me.callParent([fieldData]);
        inputElAttr = data.inputElAriaAttributes;

        if (inputElAttr) {
            inputElAttr['aria-checked'] = '';
        }

        return data;
    },

    /**
     * @private
     */
    updateValueFromDom: function () {
        var me = this,
            inputEl = me.inputEl && me.inputEl.dom;

        if (inputEl) {
            var val = inputEl.getAttribute('aria-checked');
            var newval;
            if (val === true || val === 'true') {
                newval = false;
            } else if (val === false || val === 'false') {
                newval = true;
            } else {
                newval = true;
            }
            me.checked = me.rawValue = me.value = newval;

            me.checkChange();
        }
    },

    isChecked: function (rawValue, inputValue) {
        var ret = false;

        if (rawValue === true || rawValue === 'true') {
            ret = true;
        } else if (rawValue === null || rawValue === 'null' || rawValue === undefined || rawValue === 'undefined') {
            ret = null;
        } else {
            if (
                inputValue !== 'on' &&
                (inputValue || inputValue === 0) &&
                (Ext.isString(rawValue) || Ext.isNumber(rawValue))
            ) {
                ret = rawValue == inputValue;
            } else {
                ret = rawValue === '1' || rawValue === 1 || this.onRe.test(rawValue);
            }
        }
        return ret;
    },

    /**
     * Returns the checked state of the checkbox.
     * @return {Boolean} True if checked, else false
     */
    getRawValue: function () {
        var inputEl = this.inputEl && this.inputEl.dom;
        var value = this.checked;
        if (inputEl) {
            var threestatevalue = inputEl.getAttribute('aria-checked');
            if (threestatevalue === true || threestatevalue === 'true') {
                value = true;
            } else if (threestatevalue === false || threestatevalue === 'false') {
                value = false;
            } else {
                value = null;
            }
        }
        return value;
    },

    /**
     * Returns the checked state of the checkbox.
     * @return {Boolean} True if checked, else false
     */
    getValue: function () {
        return this.checked;
    },

    /**
     * Sets the checked state of the checkbox.
     *
     * @param {Boolean/String/Number} value The following values will check the checkbox:
     * - `true, 'true'.
     * - '1', 1, or 'on'`, when there is no {@link #inputValue}.
     * - Value that matches the {@link #inputValue}.
     * Any other value will un-check the checkbox.
     * @return {Boolean} the new checked state of the checkbox
     */
    setRawValue: function (value) {
        var me = this,
            inputEl = me.inputEl && me.inputEl.dom,
            checked = me.isChecked(value, me.inputValue);

        if (inputEl) {
            // Setting checked property will fire unwanted propertychange event in IE8.
            me.duringSetRawValue = true;
            me.setAriaValue(inputEl, checked);
            // inputEl.checked = checked;
            me.duringSetRawValue = false;

            me.updateCheckedCls(checked);
        }

        me.checked = me.rawValue = checked;

        if (!me.duringSetValue) {
            me.lastValue = checked;
        }

        return checked;
    },

    setValue: function (value) {
        return this.callParent(arguments);
    },

    /**
     * @private
     */
    updateCheckedCls: function (checked) {
        var me = this;

        switch (checked) {
            case null:
                me.removeCls(me.checkedCls);
                me.removeCls(me.uncheckedCls);
                break;
            case true:
                me.addCls(me.checkedCls);
                me.removeCls(me.uncheckedCls);
                break;
            case false:
                me.removeCls(me.checkedCls);
                me.addCls(me.uncheckedCls);
                break;
        }
    },

    /**
     * @private
     * Called when the checkbox's checked state changes. Invokes the {@link #handler} callback
     * function if specified.
     */
    onChange: function (newVal, oldVal) {
        var me = this,
            inputEl = me.inputEl && me.inputEl.dom;

        if (inputEl) {
            me.setAriaValue(inputEl, newVal);
        }

        this.callParent([newVal, oldVal]);
    },

    getErrors: function (value) {
        var me = this;
        if (arguments.length === 0) {
            value = me.checked;
        }
        var errors = this.callParent([value]);
        if (this.allowBlank === false && Ext.isEmpty(value) && Ext.isEmpty(errors)) {
            errors.push(CMDBuildUI.locales.Locales.errors.fieldrequired);
        }
        return errors;
    },

    privates: {
        setAriaValue: function (input, value) {
            let ariaValue;
            switch (value) {
                case true:
                case 'true':
                    ariaValue = 'true';
                    input.checked = true;
                    break;
                case false:
                case 'false':
                    ariaValue = 'false';
                    input.checked = false;
                    break;
                default:
                    ariaValue = 'false';
                    input.checked = false;
            }
            input.setAttribute('aria-checked', ariaValue);
        }
    }
});
