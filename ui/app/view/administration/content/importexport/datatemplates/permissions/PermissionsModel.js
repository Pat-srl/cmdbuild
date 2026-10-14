Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.permissions.PermissionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-datatemplates-permissions-permissions',
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
            modeTypeAllow: false,
            modeTypeRead: true,
            modeTypeWFPlus: true,
            modeTypeWFDefault: true,
            modeTypeWrite: true,
            actionFilter: true,
            actionResetFilter: true,
            actionActionDisabled: true,
            modeTypeNoneOther: true,
            modeTypeReadOther: true,
            modeTypeWriteOther: true,
            modeTypeWFBasic: true
        }
    },

    formulas: {
        action: {
            bind: {
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
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
                theGateTemplate: '{theGateTemplate}'
            },
            get: function (data) {
                if (data.theGateTemplate && data.theGateTemplate.crudState !== 'C') {
                    return Ext.String.format('/roles/_ALL/grants/by-target/etltemplate/{0}', data.theGateTemplate.get('code'));
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