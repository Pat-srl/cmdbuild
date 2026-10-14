Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuthenticationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-authentication',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.setup.elements.Authentication} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.up('administration-content').getViewModel().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.systemconfig.authentication, CMDBuildUI.locales.Locales.administration.navigation.settings));
        //view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);

        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;


        tabPanelHelper.addTab(view, "loginmodules", CMDBuildUI.locales.Locales.administration.systemconfig.loginmodules, [{
            xtype: 'administration-content-setup-elements-loginmodules'
        }], 0, {
            disabled: '{disabledTabs.loginmodules}'
        });

        tabPanelHelper.addTab(view,
            "authmodules",
            CMDBuildUI.locales.Locales.administration.systemconfig.authmodules,
            [{
                xtype: 'administration-content-setup-elements-authmodules'
            }],
            1, {
                disabled: '{disabledTabs.authmodules}'
            });
        tabPanelHelper.addTab(view, "passwordpolicy", CMDBuildUI.locales.Locales.administration.navigation.passwordpolicy, [{
            xtype: 'administration-content-setup-elements-passwordpolicy'
        }], 2, {
            disabled: '{disabledTabs.passwordpolicy}'
        });

        tabPanelHelper.addTab(view, "loginsettings", CMDBuildUI.locales.Locales.administration.navigation.settings, [{
            xtype: 'administration-content-setup-elements-loginsettings'
        }], 3, {
            disabled: '{disabledTabs.loginsettings}'
        });

    },

    /**
     * @param {CMDBuildUI.view.administration.content.setup.elements.Authentication} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.systemAuthentication', this, view, newtab, oldtab, eOpts);
    }


});