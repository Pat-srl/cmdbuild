
Ext.define('CMDBuildUI.view.fields.file.Field', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.file.FieldController',
        'CMDBuildUI.view.fields.file.FieldModel'
    ],

    alias: 'widget.cmdbuildfilefield',
    controller: 'fields-file-field',
    viewModel: {
        type: 'fields-file-field'
    },

    mixins: [
        'CMDBuildUI.view.fields.ValidationMixin'
    ],

    config: {
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for
         * value binding.
         */
        recordLinkName: null
    },

    cls: Ext.baseCSSPrefix + 'cmdbuildfilefield',

    /**
     * @cfg {String} invalidCls
     * The CSS class to use when marking the component invalid.
     */
    invalidCls: Ext.baseCSSPrefix + 'form-invalid',

    layout: 'anchor',

    items: [{
        xtype: 'container',
        layout: 'hbox',
        hidden: true,
        bind: {
            hidden: '{filedata.hidden}'
        },
        items: [{
            xtype: 'component',
            bind: {
                html: '{filedata.status}'
            }
        }, {
            xtype: 'component',
            flex: 1,
            bind: {
                html: '{filedata.name}'
            }
        }, {
            xtype: 'tool',
            bind: {
                hidden: '{!filedata.loaded}'
            },
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
            itemId: 'cleartool',
            tooltip: CMDBuildUI.locales.Locales.common.actions.clear,
            localized: {
                tooltip: "CMDBuildUI.locales.common.actions.clear"
            }
        }]
    }, {
        xtype: 'filefield',
        anchor: '100%',
        disabledCls: null
    }, {
        xtype: 'formpaginationfieldset',
        title: CMDBuildUI.locales.Locales.attachments.metadata,
        bind: {
            hidden: '{filedata.fieldsethidden || !filedata.loaded}',
            disabled: '{!filedata.loaded}'
        },
        localized: {
            title: 'CMDBuildUI.locales.Locales.attachments.metadata'
        },
        collapsible: true
    }],

    /**
     * Set the field value
     *
     * @param {String} value document id
     * @param {String} filename file name
     * @param {Numeric} size file size
     */
    setFieldValue: function (value, filename, size) {
        var obj = this.getOwnerRecord();
        if (obj) {
            obj.set(this.name, value);
            obj.set("_" + this.name + "_name", filename);
            obj.set("_" + this.name + "_Size", size);
        }
    },

    /**
     * Checks whether the value of the field has changed since the last time it was checked.
     * If the value has changed, it:
     *
     * 1. Fires the {@link #change change event},
     * 2. Performs validation if the {@link #validateOnChange} config is enabled, firing the
     *    {@link #validitychange validitychange event} if the validity has changed, and
     * 3. Checks the {@link #isDirty dirty state} of the field and fires the {@link #dirtychange dirtychange event}
     *    if it has changed.
     */
    checkChange: function () {
        this.mixins.field.checkChange.call(this);
        if (!this.suspendCheckChange && !this.destroying && !this.destroyed) {
            this.publishValue();
        }
    },

    /**
     * Update accept property of file input.
     *
     * @param {String} extensions Allowed extensions.
     */
    updateAllowedExtensions: function (extensions) {
        if (extensions) {
            this.down("filefield").fileInputEl.dom.accept = extensions;
        }
    },

    /**
     * Returns whether or not the value of the field is valid
     *
     * @return {Boolean} True if the value is valid, otherwise false
     */
    validate: function () {
        var vm = this.getViewModel(),
            fileName = vm.get("filedata.name");
        if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled)) {
            this.markInvalid(CMDBuildUI.locales.Locales.attachments.errordms);
            this.down("filefield").disable();
        } else if (vm.get("filedata.status") === this.statuses.alreadypresent(fileName)) {
            this.markInvalid(Ext.String.format(CMDBuildUI.locales.Locales.attachments.filealreadyinlist, fileName));
        } else if (!this.allowBlank && !fileName) {
            this.markInvalid(CMDBuildUI.locales.Locales.errors.fieldrequired);
        } else {
            this.clearInvalid();
            return true;
        }
        return false;
    },

    privates: {
        /**
         * Returns the owner record.
         *
         * @returns {Ext.data.Model}
         */
        getOwnerRecord: function () {
            var object = this._ownerRecord;
            if (!object && this.getRecordLinkName()) {
                object = this.lookupViewModel().get(this.getRecordLinkName());
            }
            return object;
        },

        statuses: {
            ready: function () {
                return Ext.String.format(
                    '<span class="status-icon status-ready" data-qtip="{0}" aria-label="{0}"><i class="{1}"></i> </span>',
                    CMDBuildUI.locales.Locales.attachments.statuses.ready,
                    CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid'));
            },
            loaded: function () {
                return Ext.String.format(
                    '<span class="status-icon status-loaded" data-qtip="{0}" aria-label="{0}"><i class="{1}"></i> </span>',
                    CMDBuildUI.locales.Locales.attachments.statuses.loaded,
                    CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular'));
            },
            loading: function () {
                return Ext.String.format(
                    '<span class="status-icon status-done"><i class="{0} fa-spin"></i> </span>',
                    CMDBuildUI.util.helper.IconHelper.getIconId('spinner', 'solid'));
            },
            alreadypresent: function (name) {
                return Ext.String.format(
                    '<span class="status-icon status-alreadypresent" data-qtip="{0}" aria-label="{0}"><i class="{1}"></i> </span>',
                    Ext.String.format(CMDBuildUI.locales.Locales.attachments.filealreadyinlist, name),
                    CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'));
            }
        }
    }
});
