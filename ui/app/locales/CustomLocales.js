Ext.define(
    'CMDBuildUI.locales.CustomLocales',
    {
        singleton: true
    },
    function () {
        Ext.Loader.loadScript({
            url: `${CMDBuildUI.util.Config.baseUrl.replace('services/rest/v4', 'ui')}/app/locales/CustomLocales.js`
        });
    }
);
