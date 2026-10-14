Ext.define('CMDBuildUI.view.login.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-container',

    data: {
        hideChangePasswordBtn: false,
        sso: {
            hidden: true,
            hiddendefaultlogin: true
        },
        disabledfields: {}
    },

    formulas: {
        loginText: function () {
            return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.login.text);
        },
        currentYear: function () {
            return new Date().getFullYear();
        },
        htmlInfoBtn: function () {
            return Ext.String.format('<small>{0}  -  </small>', CMDBuildUI.locales.Locales.main.info);
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            pageSize: 0,
            autoLoad: '{language.showselector}',
            autoDestroy: true
        }
    }
});
