Ext.define('CMDBuildUI.view.administration.content.emails.templates.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-view',
    data: {
        actions: {
            add: false,
            edit: false,
            view: true
        },
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

        titleManager: {
            bind: {
                type: '{templateType}'
            },
            get: function (data) {
                var title = Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.importexport.texts.notifications, CMDBuildUI.locales.Locales.administration.emails.templates);
                if (data.type) {
                    if (data.type !== 'all') {
                        title = Ext.String.format('{0} - {1} - {2}', CMDBuildUI.locales.Locales.administration.importexport.texts.notifications, CMDBuildUI.locales.Locales.administration.emails.templates, CMDBuildUI.locales.Locales.administration.emails[data.type]);
                    }
                }
                this.getParent().set('title', title);
            }
        }
    }
});