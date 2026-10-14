Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabs.PermissionsTabBaseModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-common-permissionstab',
    data: {
        targetType: 'dashboard', // default value, can be overridden by config

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
                entityId: '{theDashboard._id}'
            },
            get: function (data) {
                if (this.get('theDashboard').crudState !== 'C') {
                    return Ext.String.format(
                        '/roles/_ALL/grants/by-target/{0}/{1}',
                        this.get('targetType'),
                        data.entityId
                    );
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
