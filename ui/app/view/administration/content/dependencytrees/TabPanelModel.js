Ext.define('CMDBuildUI.view.administration.content.dependencytrees.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dependencytrees-tabpanel',

    data: {
        activeTab: 0,
        disabledTabs: {
            properties: false,
            permissions: false
        },
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: false,
            edit: true,
            add: false
        },
        hideForm: false,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_navtrees_modify}'
            },
            get: function (data) {
                const canModify = data.canModify;
                this.set('toolAction._canAdd', canModify);
                this.set('toolAction._canUpdate', canModify);
                this.set('toolAction._canDelete', canModify);
                this.set('toolAction._canActiveToggle', canModify);
            }
        },

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

        dependencyTreeLabel: {
            bind: '{theDependencyTree}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.dependencytrees.description;
            }
        },

        panelTitle: {
            bind: {
                theReport: '{theDependencyTree}',
                description: '{theDependencyTree.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    CMDBuildUI.locales.Locales.administration.navigation.dependencytrees,
                    data.description ? ' - ' : '',
                    data.description
                );
                this.getParent().set('title', title);
            }
        }
    }
});
