Ext.define('CMDBuildUI.view.administration.content.customcomponents.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-customcomponents-view',

    data: {
        componentTypeName: null,
        theTranslation: false,
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
        },
        hasMobile: false,
        hasDefault: false,
        mobileRemoved: false,
        defaultRemoved: false
    },
    formulas: {
        hideMobileFields: {
            get: function () {
                return !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled);
            }
        },
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_uicomponents_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify === true);
                this.set('toolAction._canUpdate', canModify === true);
                this.set('toolAction._canDelete', canModify === true);
                this.set('toolAction._canActiveToggle', canModify === true);
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
        componentManager: {
            bind: '{theCustomcomponent}',
            get: function (theCustomcomponent) {
                if (!theCustomcomponent.phantom) {
                    var devices = theCustomcomponent.get('devices');
                    this.set('hasDefault', devices.indexOf(CMDBuildUI.model.menu.Menu.device['default']) > -1);
                    this.set('hasMobile', devices.indexOf(CMDBuildUI.model.menu.Menu.device.mobile) > -1);
                }
            }
        },
        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                if (data.isView && !data.isHiddenForm) {
                    return false;
                }
                return true;
            }
        },
        targetDevices: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getCustomUITargetDevices();
            }
        }
    },
    stores: {
        targetDevicesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            sorters: ['label'],
            data: '{targetDevices}',
            proxy: {
                type: 'memory'
            }
        }
    }
});