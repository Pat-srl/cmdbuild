Ext.define('CMDBuildUI.view.administration.content.emails.errors.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-errors-view',
    data: {
        name: 'CMDBuildUI',
        toolAction: {
            _canManageQueue: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_email_modify}'
            },
            get: function (data) {
                this.set('toolAction._canManageQueue', data.canModify === true);
            }
        }
    }

});
