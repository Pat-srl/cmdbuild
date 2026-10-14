Ext.define('CMDBuildUI.view.administration.content.localizations.exports.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-exports-view',
    control: {

        '#cancelBtn': {
            click: 'onCancelButtonClick'
        },
        '#exportBtn': {
            click: 'onExportButtonClick'
        }
    },
    onLaguagesStoreLoad: function (store) {      
        this.getViewModel().set('selection', store.getRange());        
    },
    onCancelButtonClick: function (button, e, eopts) {
        this.getView().up().close();
    },

    onExportButtonClick: function (button, e, eopts) {
        var sectionCombobox = this.lookup('sectionCombobox').getValue();
        var activelanguagesgrid = this.lookup('activelanguagesgrid');
        var formatCombobox = this.lookup('formatCombobox').getValue();
        var separatorCombobox = this.lookup('separatorCombobox').getValue();
        var activeOnly = this.lookup('activeOnly').getValue();
        var activeLanguages = activelanguagesgrid.getSelection();
        var languages = [];
        activeLanguages.forEach(function (activeLanguage) {
            var code = activeLanguage.get('code');
            languages.push(code);
        });

        var encodedRequest = encodeURI(Ext.String.format('{0}/translations/export?section={1}&lang={2}&format={3}&separator={4}&activeonly={5}&includeRecordsWithoutTranslation=true',
            CMDBuildUI.util.Config.baseUrl,
            sectionCombobox,
            languages,
            formatCombobox,
            separatorCombobox,
            activeOnly
        ));

        CMDBuildUI.util.File.download(encodedRequest, formatCombobox);

        this.getView().up().close();
    }
});