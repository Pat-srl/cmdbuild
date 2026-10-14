Ext.define('CMDBuildUI.view.administration.content.localizations.localization.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-topbar',
    control: {
        '#importLocalizationBtn': {
            click: 'onImportLocalizationBtn'
        },
        '#exportLocalizationBtn': {
            click: 'onExportLocalizationBtn'
        }
    },

    onImportLocalizationBtn: function (button, e, eOpts) {

        var content = {
            xtype: 'administration-content-localizations-imports-view',
            scrollable: 'y'
        };
        // custom panel listeners
        var listeners = {};

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-imports-view',
            CMDBuildUI.locales.Locales.administration.localizations.import,
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );
    },

    onExportLocalizationBtn: function (button, e, eOpts) {
        var content = {
            xtype: 'administration-content-localizations-exports-view',
            scrollable: 'y'
        };
        // custom panel listeners
        var listeners = {};

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-exports-view',
            CMDBuildUI.locales.Locales.administration.localizations.export,
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '80%'
            }
        );
    }

});