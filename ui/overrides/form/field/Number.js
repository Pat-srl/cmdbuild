Ext.define('Override.form.field.Number', {
    override: 'Ext.form.field.Number',

    /**
    * Filter insert of chars
    *
    * @private
    * @override 
    */
    filterKeys: function (e) {
        var charCode;

        /*
         * Current only FF will fire keypress events for special keys.
         * 
         * On European keyboards, the right alt key, Alt Gr, is used to type certain special
         * characters. JS detects a keypress of this as ctrlKey & altKey. As such, we check
         * that alt isn't pressed so we can still process these special characters.
         */
        if ((e.ctrlKey && !e.altKey) || e.isSpecialKey()) {
            return;
        }

        charCode = String.fromCharCode(e.getCharCode());

        if (!this.maskRe.test(charCode) || (charCode === this.decimalSeparator && e.target.value.includes(this.decimalSeparator)) ||
            (charCode === "-" && e.target.value.includes("-"))) {
            e.stopEvent();
        }
    },

    /**
     * Publish the value of this field.
     *
     * @private
     * @override 
     */
    publishValue: function () {
        var value = this.getValue();

        if (this.rendered) {
            if (value !== "-" && value !== "." && value !== "-.") {
                this.callParent(arguments);
            }
        }
    }

});