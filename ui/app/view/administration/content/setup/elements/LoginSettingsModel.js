Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettingsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-loginsettings',

    data: {
        actions: {
            view: true,
            edit: false
        },
        attributeOptions: []
    },

    formulas: {
        action: {
            get: function () {
                return this.get('actions.view')
                    ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                    : CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            }
        },

        ssoautoLogin: {
            bind: ' {theSetup.org__DOT__cmdbuild__DOT__auth__DOT__sso__DOT__redirect__DOT__enabled}',
            get: function (enabled) {
                return enabled.trim() !== 'true';
            },
            set: function (value) {
                this.set('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__sso__DOT__redirect__DOT__enabled', !value);
            }
        },

        modeConfig: function () {
            return [
                {
                    value: 'DISABLED',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.disabled
                },
                {
                    value: 'CMDBUILD_CLASS',
                    label: CMDBuildUI.locales.Locales.administration.localizations.class
                },
                {
                    value: 'DB_FUNCTION',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.databasefunction
                }
            ];
        },

        operatorClass: function () {
            const classes = Ext.getStore('classes.Classes').getRange();
            const options = [];
            classes.forEach(function (cls) {
                options.push({
                    value: cls.get('_id'),
                    label: cls.get('description')
                });
            });
            return options;
        },

        matchingUserProperty: function () {
            return [
                {
                    value: 'username',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.username
                },
                {
                    value: 'email',
                    label: CMDBuildUI.locales.Locales.administration.navigation.email
                }
            ];
        }
    },

    stores: {
        /**
         * Store for modes combo
         */
        modeConfigs: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{modeConfig}',
            autoDestroy: true
        },

        /**
         * Store for Operator class combo
         */
        operatorClasses: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{operatorClass}',
            autoDestroy: true
        },

        /**
         * Store for Matching attribute combo
         */
        matchingAttributes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{attributeOptions}',
            autoDestroy: true
        },

        /**
         * Store for Matching user property combo
         */
        matchingUserProperties: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{matchingUserProperty}',
            autoDestroy: true
        }
    }
});
