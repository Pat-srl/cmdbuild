Ext.define('CMDBuildUI.view.fields.password.Password', {
    extend: 'Ext.form.field.Text',

    statics: {
        newPassword: 'new-password',
        currentPassword: 'current-password',
        username: 'username'
    },
    alias: 'widget.passwordfield',

    controller: 'fields-password-field',

    config: {
        /**
         * @cfg {Boolean} autocomplete
         * autocomplete allowed or not
         */
        autocomplete: 'new-password',

        /**
         * @cfg {String} [recordLinkName="theObject"]
         * The link name to use to pass the target to the widget.
         */
        recordLinkName: 'theObject'
    },

    validator: function (val) {
        if (this.metadata && this.metadata.showPassword === CMDBuildUI.model.Attribute.showPassword.never && this.metadata.mandatory) {
            var has_value = this.lookupViewModel().get(this.getRecordLinkName()).get("_" + this.name + "_has_value");
            if (val || has_value) {
                return true
            } else {
                return this.blankText
            }
        }
        return true;
    },

    inputType: 'password',
    triggers: {
        showPassword: {
            cls: CMDBuildUI.util.helper.IconHelper.getIconId('eye', 'regular'),
            hideTrigger: true,
            scope: this,
            handler: function (field, button, e) {
                field.inputEl.el.set({
                    type: 'text'
                });
                field.getTrigger('showPassword').setVisible(false);
                field.getTrigger('hidePassword').setVisible(true);
            }
        },
        hidePassword: {
            cls: CMDBuildUI.util.helper.IconHelper.getIconId('eye-slash', 'solid'),
            hidden: true,
            scope: this,
            handler: function (field, button, e) {
                field.inputEl.el.set({
                    type: 'password'
                });
                field.getTrigger('showPassword').setVisible(true);
                field.getTrigger('hidePassword').setVisible(false);
            }
        }
    },
    listeners: {
        afterrender: function (cmp) {
            if (cmp.getAutocomplete()) {
                cmp.inputEl.set({
                    autocomplete: this.getAutocomplete()
                });
            }
        },
        change: function (input, newValue, oldValue) {
            if (input.inputEl.el.dom.type === 'password') {
                var condition = newValue.search("•") > -1;
                input.getTrigger('showPassword').setVisible(!condition);
            }
        }
    },

    initComponent: function () {
        if (this.metadata && this.metadata.showPassword === CMDBuildUI.model.Attribute.showPassword.never && this.metadata.mandatory) {
            this.allowBlank = true;
            this.modelValidation = false;
        }
        this.callParent(arguments);
    }

});