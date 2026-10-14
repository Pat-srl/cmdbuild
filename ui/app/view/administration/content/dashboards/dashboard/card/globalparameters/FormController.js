Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.card.globalparameters.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-dashboard-card-globalparameters-form',
    requires: ['CMDBuildUI.model.dashboards.Component'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#parameterTypeCombo': {
            change: 'onParameterTypeComboChange'
        }
    },
    onBeforeRender: function (view) {
        const vm = view.getViewModel();
        const theParameter = vm.get('theParameter');
        const action = vm.get('action');
        if (
            theParameter &&
            (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ||
                action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view)
        ) {
            view.down('#parameterTypeCombo').setDisabled(true);
            const fieldset = view.down('#parametersContainer');
            fieldset.add({
                xtype: 'administration-dashboards-common-parameters-parameter',
                parameter: theParameter,
                hideGlobalParameterFields: true,
                viewModel: {
                    data: {
                        theParameter: theParameter,
                        componentId: theParameter.get('_id'),
                        dashboardName: vm.get('owner').getViewModel().get('theDashboard.name'),
                        action: action,
                        isGlobalParameter: true
                    }
                }
            });
        }
    },

    onParameterTypeComboChange: function (combo, newValue) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const action = vm.get('action');
        if (newValue && action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            const fieldset = view.down('#parametersContainer');
            const parameter = CMDBuildUI.model.dashboards.DataSourceParameter.create({
                _id: CMDBuildUI.util.Utilities.generateUUID(),
                name: '',
                type: newValue,
                defaultValue: ''
            });
            // Needed for hiddenfields formula of ParamterModel
            if (newValue === 'date' || newValue === 'datetime') {
                parameter.set('fieldType', newValue);
            }
            fieldset.removeAll();

            vm.set('theParameter', parameter);
            fieldset.add({
                xtype: 'administration-dashboards-common-parameters-parameter',
                parameter: parameter,
                hideGlobalParameterFields: true,
                viewModel: {
                    data: {
                        theParameter: parameter,
                        componentId: parameter.get('_id'),
                        dashboardName: vm.get('owner').getViewModel().get('theDashboard.name'),
                        action: action,
                        isGlobalParameter: true
                    }
                }
            });
        }
    },

    onSaveBtnClick: function () {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const owner = vm.get('owner');
        const action = vm.get('action');
        const globalParametersStore = owner.getViewModel().get('globalParametersStore');
        // The name come from arguments of functions, for global parameters we don't have a separate name field, so we set it from the description.
        const name = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(vm.get('theParameter.description'));
        vm.set('theParameter.name', name);
        if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            globalParametersStore.add(vm.get('theParameter'));
        } else if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit) {
            const parameter = vm.get('theParameter');
            const storeParameter = globalParametersStore.getById(parameter.getId());
            storeParameter.set(parameter.getData());
        }
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    },

    onCancelBtnClick: function () {
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    }
});
