Ext.define('CMDBuildUI.view.administration.content.users.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-users-view',
    data: {
        totalUserCount: 0,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        toolAction: {
            _canAdd: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_users_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
            }
        },
        actionManager: {
            bind: '{action}',
            get: function (action) {
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        getToolbarButtons: {
            bind: '{theUser.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theUser.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        }
    },

    stores: {
        allUsers: {
            type: 'users',
            autoLoad: true,
            autoDestroy: true,
            listeners: {
                datachanged: 'onAllUsersStoreDatachanged'
            }
        },
        allGroups: {
            model: 'CMDBuildUI.model.users.Group',
            pageSize: 0,
            sorters: ['description'],
            autoLoad: false,
            autoDestroy: true,
            proxy: {
                url: '/roles',
                type: 'baseproxy'
            }
        },
        languages: {
            source: 'localizations.Languages',

            sorters: ['description'],
            autoLoad: true,
            pageSize: 0,
            autoDestroy: true
        }
    }
});