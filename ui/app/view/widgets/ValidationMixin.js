Ext.define('CMDBuildUI.view.widgets.ValidationMixin', {
    mixinId: 'cmdbuildwidgets-validationmixin',

    mixins: ['Ext.mixin.Bindable'],

    /**
     * @property {Boolean} isFormField
     * Flag denoting that this component is a Field. Always true.
     */
    isFormField: true,

    config: {
        /**
         * @cfg {Boolean} mandatory
         * True if the widget must be evaluate.
         */
        required: null,

        /**
         * @cfg {Object} [value]
         * A value to initialize the widget.
         */
        value: null
    },

    /**
     * @cfg {String} activeError
     * If specified, then the component will be displayed with this value as its active error when first rendered. Use
     * {@link #setActiveError} or {@link #unsetActiveError} to change it after component creation.
     */
    activeError: null,

    /**
     * @cfg {String[]} activeErrors
     * The list of active errors.
     */
    activeErrors: [],

    /**
     * @cfg {String/String[]/Ext.XTemplate} activeErrorsTpl
     * The template used to format the Array of error messages passed to {@link #setActiveErrors} into a single HTML
     * string. if the {@link #msgTarget} is title, it defaults to a list separated by new lines. Otherwise, it
     * renders each message as an item in an unordered list.
     */
    activeErrorsTpl: [
        '<tpl if="errors && errors.length">',
        '<tpl for="errors"><tpl if="xindex &gt; 1">\n</tpl>{.}</tpl>',
        '</tpl>'
    ],

    publishes: [
        'value'
    ],

    /**
     * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the
     * {@link #processRawValue processed raw value} of the widget. **Note**: {@link #disabled} buttons are
     * always treated as valid.
     *
     * @return {Boolean} True if the value is valid, else false
     */
    isValid: function () {
        return this.isDisabled() || this.validateValue(this.getValue());
    },

    /**
     * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
     * {@link #markInvalid} and false is returned, otherwise true is returned.
     *
     * @param {Object} value The value to validate
     * @return {Boolean} True if all validations passed, false if one or more failed
     */
    validateValue: function (value) {
        var errors = this.getErrors(value),
            isValid = Ext.isEmpty(errors);

        if (isValid) {
            this.clearInvalid();
        } else {
            this.markInvalid(errors);
        }
        this.checkValidityChange(isValid);
        return isValid;
    },

    /**
     *
     * @param {String} error
     */
    addError: function (error) {
        if (this._errs === undefined) {
            this._errs = [];
        }
        if (!Ext.Array.contains(this._errs, error)) {
            this._errs.push(error);
            this.validate();
        }
    },

    /**
     *
     * @param {String} error
     */
    removeError: function (error) {
        if (this._errs === undefined) {
            this._errs = [];
        }
        this._errs = Ext.Array.remove(this._errs, error);
        this.validate();
    },

    /**
     * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
     * @return {String[]} Array of any validation errors
     */
    getErrors: function (value) {
        var errs = [];
        if (
            this.getRequired() && (
                (!Ext.isObject(this.getValue() || value) && Ext.isEmpty(this.getValue() || value)) || // use Ext.isEmpty if value is not an Object
                (Ext.isObject(this.getValue() || value) && Ext.Object.isEmpty(this.getValue() || value)) // use Ext.Object.isEmpty if value is an Object
            )
        ) {
            errs.push(CMDBuildUI.locales.Locales.widgets.required);
        }
        return Ext.Array.merge(errs, this._errs || []);
    },

    /**
     * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the field's current
     * value, and fires the {@link #validitychange} event if the field's validity has changed since the last validation.
     * **Note**: {@link #disabled} fields are always treated as valid.
     *
     * Custom implementations of this method are allowed to have side-effects such as triggering error message display.
     * To validate without side-effects, use {@link #isValid}.
     *
     * @return {Boolean} True if the value is valid, else false
     */
    validate: function () {
        return this.checkValidityChange(this.isValid());
    },

    checkValidityChange: function (isValid) {
        var me = this;

        if (isValid !== me.wasValid) {
            me.wasValid = isValid;
            me.fireEvent('validitychange', me, isValid);
        }
        return isValid;
    },

    /**
     *
     */
    isDirty: function () {
        return false;
    },

    /**
     * Get the dom element on which add the error.
     *
     * @returns {HTMLElement}
     */
    getErrorDomElement: Ext.emptyFn,

    privates: {
        /**
         * Clear any invalid styles/messages for this widget button.
         *
         * **Note**: this method does not cause the Field's {@link #validate} or {@link #isValid} methods to return `true`
         * if the value does not _pass_ validation. So simply clearing a field's errors will not necessarily allow
         * submission of forms submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
         * @private
         */
        clearInvalid: function () {
            var me = this;
            if (!!me.activeError) {
                delete me.activeError;
                delete me.activeErrors;
            }
            // remove class only if the object is not destroyed
            if (!me.isDestroyed) {
                this.removeClsWithUI("error");
                var el = this.getErrorDomElement();
                if (el) {
                    el.removeAttribute('data-errorqtip');
                }
            }
        },

        /**
         * @method
         * Display one or more error messages associated with this widget.
         *
         * **Note**: this method does not cause the Field's {@link #validate} or
         * {@link #isValid} methods to return `false` if the value does _pass_ validation.
         * So simply marking a Field as invalid will not prevent submission of forms
         * submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
         *
         * @param {String/String[]} errors The validation message(s) to display.
         */
        markInvalid: function (errors) {
            var me = this;
            errors = Ext.Array.from(errors);
            var tpl = this.lookupTpl("activeErrorsTpl");

            this.activeErrors = errors;
            var activeError = me.activeError = tpl.apply({
                fieldLabel: me.fieldLabel,
                errors: errors,
                listCls: Ext.baseCSSPrefix + 'list-plain'
            });

            // add class only if the object is not destroyed
            if (!me.isDestroyed) {
                this.addClsWithUI("error");
                var el = this.getErrorDomElement();
                if (el) {
                    el.setAttribute('data-errorqtip', activeError);
                }
            }
        }
    }

});