Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-signatures-card-card',
    data: {
        actions: {
            add: false,
            edit: false,
            view: true
        }            
    },

    formulas: {
        panelTitle: {
            bind: {
                action: '{action}',
                signatureDescription: '{theSignature.description}'
            },
            get: function (data) {                
                switch (data.action) {
                    case CMDBuildUI.util.administration.helper.FormHelper.formActions.add:
                        return CMDBuildUI.locales.Locales.administration.emails.newsignature;
                    case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                        return Ext.String.format(CMDBuildUI.locales.Locales.administration.emails.editsignature, data.signatureDescription);
                    case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:
                        return Ext.String.format(CMDBuildUI.locales.Locales.administration.emails.viewsignature, data.signatureDescription);
                }
            }
        },
        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    }
});