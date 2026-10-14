Ext.define('CMDBuildUI.view.fields.ValidationMixin', {
    mixinId: 'cmdbuildfields-validationmixin',

    mixins: {
        field: 'Ext.form.field.Field'
    },

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    /**
     * @property {Boolean} isFormField
     */
    isFormField: true,

    /**
     * @cfg {Boolean} [allowBlank=true]
     * Specify false to validate that the value's length must be > 0. If `true`, then a blank value is **always** taken to be valid regardless of any {@link #vtype}
     * validation that may be applied.
     *
     * If {@link #vtype} validation must still be applied to blank values, configure {@link #validateBlank} as `true`;
     */
    allowBlank: true,

    getErrors: function (value) {
        value = arguments.length ? value : this.getValue();
        var errors = this.mixins.field.getErrors.call(this, value);
        if (!this.allowBlank && Ext.isEmpty(value)) {
            errors.push(CMDBuildUI.locales.Locales.errors.fieldrequired);
        }
        return errors;
    },

    isValid: function () {
        var me = this,
            disabled = me.disabled,
            validate = me.forceValidation || !disabled;
        return validate ? me.validateValue(me.getValue()) : disabled;
    },

    /**
     * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
     * {@link #markInvalid} and false is returned, otherwise true is returned.
     *
     * Previously, subclasses were invited to provide an implementation of this to process validations - from 3.2
     * onwards {@link #getErrors} should be overridden instead.
     *
     * @param {Object} value The value to validate
     * @return {Boolean} True if all validations passed, false if one or more failed
     */
    validateValue: function (value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);
        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }
        return isValid;
    },

    /**
     * @inheritdoc Ext.form.field.Field#markInvalid
     */
    markInvalid: function (errors) {
        // Save the message and fire the 'invalid' event
        var me = this,
            oldMsg = me.getActiveError(),
            active;
        me.setActiveErrors(Ext.Array.from(errors));
        active = me.getActiveError();
        if (oldMsg !== active) {
            // me.setError(active);
            if (!me.ariaStaticRoles[me.ariaRole] && me.inputEl) {
                me.inputEl.dom.setAttribute('aria-invalid', true);
            }
        }
    },

    /**
     * Clear any invalid styles/messages for this field.
     *
     * **Note**: this method does not cause the Field's {@link #validate} or {@link #isValid} methods to return `true`
     * if the value does not _pass_ validation. So simply clearing a field's errors will not necessarily allow
     * submission of forms submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
     */
    clearInvalid: function () {
        // Clear the message and fire the 'valid' event
        var me = this,
            hadError = me.hasActiveError();
        delete me.hadErrorOnDisable;
        me.unsetActiveError();
        if (hadError) {
            me.setError('');
            if (!me.ariaStaticRoles[me.ariaRole] && me.inputEl) {
                me.inputEl.dom.setAttribute('aria-invalid', false);
            }
        }
    },

    /**
     * Set the current error state
     * @private
     * @param {String} error The error message to set
     */
    setError: function (error) {
        var me = this,
            msgTarget = me.msgTarget,
            prop;
        if (me.rendered) {
            if (msgTarget === 'title' || msgTarget === 'qtip') {
                prop = msgTarget === 'qtip' ? 'data-errorqtip' : 'title';
                me.getActionEl().dom.setAttribute(prop, error || '');
            } else {
                me.updateLayout();
            }
        }
    }
});