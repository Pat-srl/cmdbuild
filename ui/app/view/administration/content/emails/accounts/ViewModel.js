Ext.define('CMDBuildUI.view.administration.content.emails.accounts.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-accounts-view',
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
        },
        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/email/accounts/?detailed=true',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);

            }
        }
    },

    stores: {
        accounts: {
            type: 'accounts',
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            proxy: {
                url: '{storeProxyUrl}',
                type: 'baseproxy'
            },
            sorters: 'name'
        }
    }
});