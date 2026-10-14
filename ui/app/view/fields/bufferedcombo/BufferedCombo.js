Ext.define('CMDBuildUI.view.fields.bufferedcombo.BufferedCombo', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.bufferedcombo.BufferedCombo'
    ],

    mixins: [
        'Ext.form.field.Field'
    ],

    alias: 'widget.bufferedcombo',
    controller: 'fields-bufferedcombo',

    layout: 'anchor',

    config: {
         /**
         * @cfg {String} modelname (required)
         * The name of the model used.
         * 
         */
        modelname: null,
        /**
         * @cfg {String} storealias (required)
         * The alias aof the model.
         */
        storealias: null,
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null,
        /**
         * @cfg {String} valueField (required)
         * The name of primary key of model
         */
        valueField: '_id',
        /**
         * @cfg {String} displayField (required)
         * The name of display field of model
         */
        displayField: 'description',
        /**
         * @cfg {Boolean} displayMode (optional) default false
         * -- true for create and bind (actions.view === true) the visibility of the displayfield
         * -- false for ignore the creation of the displayfield
         */
        displayMode: false,
        /**
         * @cfg {Boolean} inputMode (optional) default true
         * -- true for create and bind (actions.view === false) the visibility of the combo
         * -- false for ignore the creation of the combo
         */
        inputMode: true
    },

    defaults: {
        // anchor: '100%'
    },

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    initComponent: function () {
        if (this.filterRecordLinkName == null) {
            this.filterRecordLinkName = this.recordLinkName;
        }
        // create combo
        var items = [];
        if(this.getInputMode()){
            var combo = this._getComboField();
            items.push(combo);
        }
        // create displayfield
        if(this.getDisplayMode()){
            var display = this._getDisplayField();
            items.push(display);
        }
        Ext.apply(this, {
            items: items,
            name: this.name + "container"
        });
        this.callParent(arguments);
    },

    /**
     * Set value on main combo
     * @param {Object} value 
     */
    setValue: function (value) {
        var maincombo = this.getMainCombo();
        if (maincombo.getValue() != value) {
            var store = maincombo.getStore();
            if (!store.getById(value)) {
                if (store.getProxy().getUrl) {
                    var model = store.getModel();
                    model.setProxy({
                        type: 'baseproxy',
                        url: store.getProxy().getUrl()
                    });
                    model.load(value, {
                        success: function (record) {
                            maincombo.setSelection(record);
                        }
                    });
                } else {
                    maincombo.setValue(value);
                    maincombo.lookupViewModel().set("initialvalue", value);
                }
            } else {
                maincombo.setValue(value);
            }
        }
    },

    /**
     * Set value on main combo
     * @return {Object} value 
     */
    getValue: function () {
        return this.getMainCombo().getValue();
    },

    getMainCombo: function () {
        return this.lookupReference("maincombo");
    },
    /**
     * @private
     */
    privates: {
        /**
         * @private
         * @return {CMDBuildUI.view.fields.bufferedcombo.BufferedComboField}
         */
        _getComboField: function () {
            var bind = Ext.clone(this.initialConfig.bind);
            bind.hidden = '{actions.view}';
            var combo = {
                xtype: 'bufferedcombofield',
                reference: 'maincombo',
                modelname: this.getModelname(),
                storealias: this.getStorealias(),
                recordLinkName: this.getRecordLinkName(),
                bind: bind,
                displayField: this.getDisplayField(),
                valueField: this.getValueField(),
                tabIndex: this.tabIndex,
                name: this.name,
                margin: 0
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
            return combo;
        },

        /**
         * @private
         * @return {Ext.form.DisplayField}
         */
        _getDisplayField: function () {
            var bind = Ext.clone(this.initialConfig.bind);
            bind.hidden = '{!actions.view}';
            bind.value = Ext.String.format('{{0}._{1}_description}', this.getRecordLinkName(), this.name);
            var display = {
                xtype: 'displayfield',
                bind: bind,
                name: Ext.String.format('{0}_display', this.name),
                margin: 0
            };

            return display;
        }
    }


});

