Ext.define('CMDBuildUI.view.administration.content.setup.elements.MultiTenantController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-multitenant',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.multitenant);
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.getFunctionList();
    },

    onAfterRender: function (view) {
        var vm = view.up('administration-content-setup-view').getViewModel();
        vm.bind({
            bindTo: {
                multiTenantEnabled: '{multiTenantEnabled}'
            },
            single: true
        }, function (data) {
            vm.set('multitenantFieldsDisabled', data.multiTenantEnabled);

        });

    },
    privates: {

        /**
         * Get the list of function
         * @private
         */
        getFunctionList: function () {
            var me = this;
            var vm = me.getViewModel().getParent();

            var store = Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.Function'
            });

            store.load({
                callback: function (records, operation, success) {
                    if (!vm.destroyed) {
                        records.forEach(function (_function) {
                            var functionList = vm.get('functionList');
                            var exist = Ext.Array.findBy(functionList, function (obj, i) {
                                if (obj.label === _function.get('name')) {
                                    return true; // stop searching
                                }
                            });
                            if (!exist) {
                                functionList.push({
                                    label: _function.get('name'),
                                    value: _function.get('description')
                                });
                                vm.set('functionList', functionList);
                            }
                        });
                    }
                }
            });
        }
    }
});