Ext.define('CMDBuildUI.view.login.SsoPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-ssopanel',

    formulas: {
        updateSsoFieldset: {
            bind: '{sso.hiddendefaultlogin}',
            get: function (hiddendefaultlogin) {
                if (!hiddendefaultlogin) {
                    this.set('sso.title', CMDBuildUI.locales.Locales.login.sso.or);
                    this.set('sso.margin', '42 0 0');
                } else {
                    this.set('sso.title', CMDBuildUI.locales.Locales.login.title);
                }
            }
        }
    }
});