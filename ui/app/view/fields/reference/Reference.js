Ext.define('CMDBuildUI.view.fields.reference.Reference', {
    extend: 'Ext.form.FieldContainer',

    requires: ['CMDBuildUI.view.fields.reference.ReferenceController'],

    mixins: ['Ext.form.field.Field'],

    alias: 'widget.referencefield',
    controller: 'fields-referencefield',

    layout: 'anchor',

    cls: Ext.baseCSSPrefix + 'cmdbuildfilefield',

    config: {
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for
         * value binding.
         */
        recordLinkName: null,

        /**
         * @cfg {String} fiterRecordLinkName (required)
         * The name of the full record in ViewModel used for filtering.
         */
        filterRecordLinkName: null,

        /**
         * @cfg {Boolean} ignoreCqlFilter
         * Boolean value to ignore the ecql filter
         */
        ignoreCqlFilter: false,

        /**
         * @cfg {Object} customFormTargetObj
         * theTarget object used for cellediting in customForms
         */
        customFormTargetObj: null
    },

    defaults: {
        anchor: '100%'
    },

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    initComponent: function () {
        if (this.filterRecordLinkName == null) {
            this.filterRecordLinkName = this.recordLinkName;
        }

        var combo = {
            xtype: 'referencecombofield',
            reference: 'maincombo',
            metadata: this.metadata,
            bind: this.initialConfig.bind,
            tabIndex: this.tabIndex,
            recordLinkName: this.recordLinkName,
            filterRecordLinkName: this.filterRecordLinkName,
            name: this.name,
            emptyText: this.emptyText,
            margin: 0,
            ignoreCqlFilter: this.ignoreCqlFilter,
            customFormTargetObj: this.customFormTargetObj
        };

        if (this.allowBlank !== undefined) {
            combo.allowBlank = this.allowBlank;
        }
        if (this.column !== undefined) {
            combo.column = this.column;
        }
        if (this.value !== undefined) {
            combo.value = this.value;
        }
        if (this.formmode !== undefined) {
            combo.formmode = this.formmode;
        }
        if (Ext.isFunction(this.getValidation)) {
            combo.getValidation = this.getValidation;
            this.getValidation = Ext.emptyFn;
        }

        // remove field label from bindings to prevent double label
        if (this.config.bind && this.config.bind.fieldLabel) {
            delete this.config.bind.fieldLabel;
        }

        Ext.apply(this, {
            name: this.name + 'container'
        });
        this.callParent(arguments);
        this._maincombo = this.add(combo);
    },

    /**
     * Set value on main combo
     * @param {Object} value
     */
    setValue: function (value) {
        this.getMainCombo().setValue(value);
    },

    /**
     * Set value on main combo
     * @return {Object} value
     */
    getValue: function () {
        return this.getMainCombo().getValue();
    },

    getMainCombo: function () {
        return this._maincombo;
    }
});
