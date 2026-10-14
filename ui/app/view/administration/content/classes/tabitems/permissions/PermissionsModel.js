Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.permissions.PermissionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-permissions-permissions',
    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        },

        toolbarHiddenButtons: {
            edit: true
        },

        toolPermissionAction: {
            _canUpdate: false
        },
        hiddenColumns: {
            modeTypeNone: false,
            modeTypeAllow: true,
            modeTypeRead: false,
            modeTypeWFPlus: true,
            modeTypeWFBasic: true,
            modeTypeWFDefault: true,
            modeTypeWrite: false,
            actionFilter: false,
            actionResetFilter: false,
            actionActionDisabled: false,
            modeTypeNoneOther: true,
            modeTypeReadOther: true,
            modeTypeWriteOther: true
        }
    },

    formulas: {
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
        },

        toolsManager: {
            bind: {
                rolePrivileges: '{theSession.rolePrivileges}'
            },
            get: function (data) {
                this.set('toolPermissionAction._canUpdate', data.rolePrivileges.admin_roles_modify === true);
            }
        },

        proxyurl: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    return Ext.String.format('/roles/_ALL/grants/by-target/{0}/{1}', data.objectType, data.objectTypeName);
                }
            }
        }
    },
    stores: {
        grantsChainedStore: {
            proxy: {
                type: 'baseproxy',
                url: '{proxyurl}'
            },
            model: 'CMDBuildUI.model.users.Grant',
            sorters: ['_object_description'],
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        }
    }
});