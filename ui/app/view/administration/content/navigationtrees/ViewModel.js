Ext.define('CMDBuildUI.view.administration.content.navigationtrees.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-navigationtrees-view',

    data: {
        actions: {
            view: false,
            edit: false,
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

        dataManager: {
            bind: {
                theNavigationtree: '{theNavigationtree}'
            },
            get: function (data) {
                const theNavigationtree = data.theNavigationtree;

                if (!theNavigationtree) {
                    return;
                }

                const rawIconFont = theNavigationtree.get('icon_font');
                if (rawIconFont) {
                    theNavigationtree.set(
                        'icon_font',
                        CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                    );

                    theNavigationtree.set(
                        'icon_description',
                        CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                    );
                }

                const targetClass = theNavigationtree.get('targetClass');
                const nodes = theNavigationtree.get('nodes');

                if (!targetClass && nodes && nodes.length) {
                    theNavigationtree.set('targetClass', nodes[0].targetClass);
                }
            }
        },

        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                return data.isView && !data.isHiddenForm ? false : true;
            }
        }
    }
});
