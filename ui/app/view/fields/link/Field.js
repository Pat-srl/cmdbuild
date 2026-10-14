
Ext.define('CMDBuildUI.view.fields.link.Field', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.link.FieldModel'
    ],

    alias: 'widget.linkfield',
    viewModel: {
        type: 'fields-link-field'
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
        recordLinkName: null
    },

    layout: 'anchor',

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,


    initComponent: function () {
        if (this.getRecordLinkName()) {
            this._bindurl = Ext.String.format(
                "{0}.{1}",
                this.getRecordLinkName(),
                CMDBuildUI.util.helper.ModelHelper.getUrlFieldNameForLinkField(this.name)
            );
            this._bindlabel = Ext.String.format(
                "{0}.{1}",
                this.getRecordLinkName(),
                CMDBuildUI.util.helper.ModelHelper.getLabelFieldNameForLinkField(this.name)
            );
        }

        // add url field
        var items = [{
            xtype: 'textfield',
            emptyText: CMDBuildUI.locales.Locales.common.attributes.link.url,
            labelPad: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelPad,
            labelSeparator: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelSeparator,
            anchor: '100%',
            vtype: 'url',
            allowBlank: !Ext.isEmpty(this.allowBlank) ? this.allowBlank : true,
            bind: {
                value: '{linkfield.urlvalue}',
                validation: '{linkfield.labelvalue && !linkfield.urlvalue ? "' + CMDBuildUI.locales.Locales.common.attributes.link.errurl + '" : true}'
            },
            autoEl: {
                "data-testid": 'linkfield-url'
            }
        }];
        // add label field
        if (this.metadata.showLabel) {
            items.push({
                xtype: 'textfield',
                emptyText: CMDBuildUI.locales.Locales.common.attributes.link.label,
                labelPad: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelPad,
                labelSeparator: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelSeparator,
                anchor: '100%',
                bind: {
                    value: '{linkfield.labelvalue}'
                },
                autoEl: {
                    "data-testid": 'linkfield-label'
                }
            });
            if (this.metadata.labelRequired) {
                items[1].bind.validation = '{linkfield.urlvalue && !linkfield.labelvalue ? "' + CMDBuildUI.locales.Locales.common.attributes.link.errlabel + '" : true}';
            }
        }

        Ext.apply(this, {
            items: items
        });

        this.callParent(arguments);
    },

    /**
     * @override
     * @param {String} value
     */
    setValue: function (value) {
        var link = CMDBuildUI.util.Utilities.extractUrlAndLabelFromLink(value),
            vm = this.lookupViewModel();

        vm.set("linkfield", {
            urlvalue: link.url,
            labelvalue: this.metadata.showLabel && link.label !== link.url ? link.label : null
        });

        this.callParent(arguments);
    },

    /**
     * @param {Integer[]} newValue
     * @param {Integer[]} oldValue
     */
    updateValue: function (newValue, oldValue) {
        this.fireEvent("change", this, newValue, oldValue);

        // publish value when value update
        this.publishValue()
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
