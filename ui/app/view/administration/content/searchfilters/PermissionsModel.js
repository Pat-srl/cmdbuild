Ext.define('CMDBuildUI.view.administration.content.searchfilters.PermissionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-searchfilters-permissions',
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
            modeTypeWFBasic: true,
            modeTypeWFPlus: true,
            modeTypeWFDefault: true,
            modeTypeWrite: true,
            actionFilter: true,
            actionResetFilter: true,
            actionActionDisabled: true,
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
                objectTypeName: '{theViewFilter.name}'
            },
            get: function (data) {
                if (this.get('theViewFilter').crudState !== 'C') {
                    return Ext.String.format('/roles/_ALL/grants/by-target/filter/{0}', data.objectTypeName);
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