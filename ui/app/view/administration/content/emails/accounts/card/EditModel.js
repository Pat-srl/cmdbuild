Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-accounts-card-edit',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        maxAttachmentSizeForEmailManager: {
            bind: '{theAccount}',
            get: function (theAccount) {
                if (theAccount && theAccount.get('maxAttachmentSizeForEmail') === '0') {
                    theAccount.set('maxAttachmentSizeForEmail', null);
                }
            }
        },

        authTypesData: {
            get: function () {
                return [{
                    value: 'default',
                    label: CMDBuildUI.locales.Locales.administration.common.labels.default
                }, {
                    value: 'google_oauth2',
                    label: CMDBuildUI.locales.Locales.administration.emails.gmailoauth
                }, {
                    value: 'ms_oauth',
                    label: CMDBuildUI.locales.Locales.administration.emails.msouauth
                }];
            }
        }
    },

    stores: {
        authTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{authTypesData}',
            autoDestroy: true
        }
    }

});