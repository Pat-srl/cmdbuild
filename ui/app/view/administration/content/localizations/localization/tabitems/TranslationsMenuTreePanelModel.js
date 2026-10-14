Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-localization-tabitems-translationsmenutreepanel',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        localizationModel: {
            bind: {
                languages: '{translationsstore.languagesList}'
            },
            get: function (data) {

                if (data.languages) {
                    var languagesFields = [];
                    var activeLangs = data.languages.split(',');
                    Ext.Array.forEach(activeLangs, function (item) {
                        var newField = {
                            name: item,
                            mapping: 'values.' + item,
                            persist: false,
                            type: "string"
                        };
                        languagesFields.push(newField);                       
                    });
         
                    var model =  Ext.define(null, {
                            extend: 'CMDBuildUI.model.localizations.LocalizationByCode',
                            fields: languagesFields
                        });
                    
                    return model;
                }
            }
        }
    },

    stores: {
        // menus: {
        //     type: 'store',
        //     autoLoad: true,
        //     autoDestroy: true,
        //     pageSize: 0,
        //     proxy: {
        //         url: Ext.String.format(
        //             '{0}/menu/',
        //             CMDBuildUI.util.Config.baseUrl
        //         ),
        //         type: 'baseproxy'
        //     },
        //     listeners: {
        //         load: 'onStoreLoad'
        //     }
        // },
        completeTranslationsStore: {
            type: 'store',
            proxy: {
                type: 'baseproxy'
            },
            autoDestroy: true
        },

        localizationsStore: {
            model: '{localizationModel}',
            proxy: {
                type: 'baseproxy',
                url: '/translations/by-code',
                extraParams: {
                    languages: '{translationsstore.languagesList}',
                    includeRecordsWithoutTranslation: true,
                    section: 'menu'
                }
            },
            sorters: ['_element', 'type', 'default'],
            listeners: {
                load: 'onStoreLoad'
            },
            autoLoad: '{translationsstore.autoload}',
            autoDestroy: true,
            pageSize: 0
        }

    }
});