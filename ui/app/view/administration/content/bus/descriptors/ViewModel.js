Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bus-descriptors-view',
    data: {
        hideForm: false,
        actions: {
            add: false,
            edit: false,
            view: true
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false,
            _canDownload: true
        }
    },
    formulas: {
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_etl_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify === true);
                this.set('toolAction._canUpdate', canModify === true);
                this.set('toolAction._canDelete', canModify === true);
                this.set('toolAction._canActiveToggle', canModify === true);
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
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
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
        },
        typeLabel: {
            get: function () {
                return CMDBuildUI.locales.Locales.administration.bus.busdedescriptor;
            }
        },
        emptyNewParam: {
            bind: '{newParamsStore}',
            get: function (newParamsStore) {
                return newParamsStore.add(CMDBuildUI.model.base.KeyValue.create({
                    key: '',
                    value: ''
                }));
            }
        },
        params: {
            bind: '{theDescriptor.params}',
            get: function (params) {
                var result = [];
                for (var key in params) {
                    if (params.hasOwnProperty(key)) {
                        result.push({
                            key: key,
                            value: params[key]
                        });
                    }
                }
                return result;
            }
        }

    },
    stores: {
        newParamsStore: {
            proxy: 'memory',
            model: 'CMDBuildUI.model.base.KeyValue'
        },

        paramsStore: {
            proxy: 'memory',
            model: 'CMDBuildUI.model.base.KeyValue',
            data: '{params}'
        }
    }

});