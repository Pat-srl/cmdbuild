Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuthenticationModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-authentication',
    data: {
        disabledTabs: {
            authmodules: false,
            loginmodules: false,
            passwordpolicy: false,
            loginsettings: false
        }
    },

    formulas: {
        activeTab: function () {
            return this.get('activeTabs.systemAuthentication');
        },
        configManager: function () {
            var me = this;

            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    if (!me.destroyed) {
                        configs.forEach(function (key) {
                            me.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key.default);
                        });
                    }
                }
            );
        }
    }

});