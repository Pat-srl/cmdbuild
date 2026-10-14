Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-accounts-card-viewinrow',
    data: {
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_email_modify}',
                defaultAccount: '{theAccount.default}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true && !data.defaultAccount);
            }
        },

        updateRemoveAccount: {
            bind: {
                defaultAccount: '{theAccount.default}'
            },
            get: function (data) {
                this.set('isDefault', data.defaultAccount);
            }
        },

        updateDisplayPassword: {
            bind: {
                password: '{theAccount.password}'
            },
            get: function (data) {
                var hiddenPassword = CMDBuildUI.util.administration.helper.RendererHelper.getDisplayPassword(data.password);
                this.set('hiddenPassword', hiddenPassword);
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