Ext.define('CMDBuildUI.view.fields.biginteger.Field', {
    extend: 'Ext.form.TextField',

    alias: 'widget.biginteger',
    
    viewModel: {
       
    },

    mixins: [
        'Ext.form.field.Field'
    ],

    config: {
        /**
         * @cfg {String} value
         * The link value
         */
        value: null,

        /**
         * @cfg {String} recordLinkName
         * The name of the full record in ViewModel used for
         * value binding.
         */
        recordLinkName: null,

        /**
         * @cfg {Number} maxValue
         * 
         */
        maxValue: 9223372036854775807,

        /**
         * @cfg {Number} minValue
         * 
         */
        minValue: -9223372036854775808
    },

    
    /**
     * @property {RegExp} regex 
     * To validate min max value
     * A JavaScript RegExp object to be tested against the field value during validation.
     * If the test fails, the field will be marked invalid using
     * either **{@link #regexText}** or **{@link #invalidText}**.
     */
    regex: /\b(?:[0-9]{1,18}|[1-8][0-9]{18}|9(?:[01][0-9]{17}|2(?:[01][0-9]{16}|2(?:[0-2][0-9]{15}|3(?:[0-2][0-9]{14}|3(?:[0-6][0-9]{13}|7(?:[01][0-9]{12}|20(?:[0-2][0-9]{10}|3(?:[0-5][0-9]{9}|6(?:[0-7][0-9]{8}|8(?:[0-4][0-9]{7}|5(?:[0-3][0-9]{6}|4(?:[0-6][0-9]{5}|7(?:[0-6][0-9]{4}|7(?:[0-4][0-9]{3}|5(?:[0-7][0-9]{2}|80[0-8]))))))))))))))))\b/,

    /**
     * @property {RegExp} maskRe
     * Allowed only - and numbers
     * An input mask regular expression that will be used to filter keystrokes
     * (character being typed) that do not match.
     * Note: It does not filter characters already in the input.
     */
    maskRe: /^-?\d*$/,

    /**
     * @property {Stringg} layout
     *
     */
    layout: 'anchor',

    /**
     * @property {String} fieldType
     */
    fieldType: 'bigint',

    /**
     * Returns the trigger with the given id
     * @param {String} id
     * @return {CMDBuildUI.view.fields.displaywithtriggers.Trigger}
     */
    getTrigger: function (id) {
        return this.getTriggers()[id];
    },
   
    /**
     * @override
     * @param {String} value
     */
    setValue: function (value) {
        this.callParent(arguments);
    },

    /**
     * @param {Integer[]} newValue
     * @param {Integer[]} oldValue
     */
    updateValue: function (newValue, oldValue) {
        this.fireEvent("change", this, newValue, oldValue);

        // publish value when value update
        this.publishValue();
    },

    /**
     * Publish the value of this field.
     *
     * @private
     * @override Remove check of field validity
     */
    publishValue: function () {
        var me = this;

        if (me.rendered) {
            me.publishState('value', me.getValue());
        }
    }
});