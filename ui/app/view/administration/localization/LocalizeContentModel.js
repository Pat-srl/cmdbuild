Ext.define('CMDBuildUI.view.administration.localization.LocalizeContentModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-localization-localizecontent',
    data: {
        action: null,
        actions: {
            edit: false,
            add: false,
            view: false
        },
        languages: [],
        theTranslation: false
    },
    formulas: {
        actionManager: {
            bind: '{action}',
            get: function (action) {
                if (action) {
                    this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                    this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                    this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                }
            }
        }
    },
    stores: {
        languagesStore: {
            model: 'CMDBuildUI.model.Language',
            proxy: {
                type: 'baseproxy',
                url: '/languages',
                extraParams: {
                    active: true
                }
            },
            sorters: 'description',
            autoLoad: false,
            autoDestroy: true
        },
        localizationStore: {
            model: 'CMDBuildUI.model.Translation',
            proxy:{
                type: 'baseproxy',
                url: '/translations'
            },
            autoload: false,
            autoDestroy: true
        }
    }
});
