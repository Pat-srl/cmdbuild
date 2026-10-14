Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-attributes-fieldsmanagement-panel',

    data: {
        theForm: null,
        activity: null,
        attributes: null,
        groups: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        showClosedTaskMessage: false
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{_can_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        titleManager: function () {
            if (this.getParent() && this.getParent().type === 'administration-detailswindow') {
                this.getParent().set('title', this.get('title'));
            }
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    if (this.getParent().type === 'administration-content-classes-view') {
                        this.getParent().configDisabledTabs();
                    } else if (this.getParent().getParent && this.getParent().getParent().type === 'joinviews-configuration-configuration') {
                        this.getParent().set('disabledTabs.properties', false);
                    }
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    if (this.getParent().type === 'administration-content-classes-view') {
                        this.getParent().toggleEnableTabs(3);
                    } else if (this.getParent().getParent && this.getParent().getParent().type === 'joinviews-configuration-configuration') {
                        this.getParent().toggleEnableTabs(1);
                    }
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },

    stores: {
        formtemplate: {
            type: 'tree',
            model: 'CMDBuildUI.model.formstructure.FormItem',
            proxy: {
                type: 'memory'
            },
            root: {
                "name": "form1",
                "type": "form",
                "children": '{groups}'
            }
        }
    }
});