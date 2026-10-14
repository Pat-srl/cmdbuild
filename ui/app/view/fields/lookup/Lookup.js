Ext.define('CMDBuildUI.view.fields.lookup.Lookup', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.lookup.LookupController',
        'CMDBuildUI.view.fields.lookup.LookupModel'
    ],

    mixins: [
        'Ext.form.field.Field'
    ],

    alias: 'widget.lookupfield',
    controller: 'fields-lookupfield',
    viewModel: {
        type: 'fields-lookupfield'
    },

    config: {
        /**
         * @cfg {String} recordLinkName
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null,

        /**
         * @cfg {Boolean} ignoreCqlFilter
         * Boolean value to ignore the ecql filter
         */
        ignoreCqlFilter: false,

        /**
         * 
         */
        lookupIdField: null
    },

    layout: 'anchor',

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    defaults: {
        xtype: 'lookupcombofield',
        anchor: '100%',
        margin: 0
    },

    initComponent: function () {
        // remove field label from bindings to prevent double label
        if (this.config.bind && this.config.bind.fieldLabel) {
            delete this.config.bind.fieldLabel;
        }

        var firstload = true;
        var me = this;

        if (this.config.bind && this.config.bind.value) {
            this.getViewModel().bind({
                bindTo: {
                    value: this.config.bind.value
                }
            }, function (data) {
                if (firstload) {
                    me.defaultValue = data.value;
                }
            });
        }
        this.callParent(arguments);
    },

    /**
     * Get lookup type.
     * @return {String}
     */
    getLookupType: function () {
        return this.metadata.lookupType;
    },

    /**
     * 
     */
    getValue: function () {
        var combo = this.lookup("combo0");
        return combo && combo.getValue() || null;
    },

    /**
     * Set value on main combo
     * @param {Object} value
     */
    setValue: function (value) {
        var combo = this.lookup("combo0");
        return combo && combo.setValue(value) || null;
    }
});