Ext.define('CMDBuildUI.view.administration.content.emails.signatures.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-signatures-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: '',
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
                canModify: '{theSession.rolePrivileges.admin_email_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        }
    },

    stores: {
        signatures: {
            model: 'CMDBuildUI.model.emails.Signature',
            autoDestroy: true,
            sorters: ['code'],
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Emails.getSignaturesUrl(),
                extraParams: {
                    detailed: true
                }
            },
            autoLoad: true,
            pageSize: 0
        }
    }
});
