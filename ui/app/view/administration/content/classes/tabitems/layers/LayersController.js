Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.layers.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-layers-layers',
    control: {

        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onEditBtnClick: function (button, event, eOpts) {
        this.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);

        var vm = button.up('administration-content-classes-view').getViewModel();
        vm.toggleEnableTabs(5);

        this.getViewModel().get('layersStore').reload();

    },

    onSaveBtnClick: function (button, event, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = button.lookupViewModel();
        var currentClassName = vm.get('objectTypeName');
        var layers = this.getView().getStore().getData().getRange();
        var visibles = {};
        Ext.Array.forEach(layers, function (element, index) {
            if (currentClassName in element.get('visibility')) {
                visibles[element.get('_id')] = element.get('visibility')[currentClassName];
            }
        });
        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request({
            url: Ext.String.format('{0}/classes/{1}/geoattributes/visibility', CMDBuildUI.util.Config.baseUrl, currentClassName),
            method: 'POST',
            jsonData: visibles,
            success: function (transport) {
                me.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                var vmParent = vm.getParent();
                vmParent.configDisabledTabs();
            },
            callback: function (reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
                me.getViewModel().get('layersStore').reload();
            }
        });
    },
    onCancelBtnClick: function (button, event, eOpts) {
        this.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var vm = button.lookupViewModel().getParent();
        vm.configDisabledTabs();
        this.getViewModel().get('layersStore').reload();
    },

    privates: {
        setFormMode: function (mode) {
            var vm = this.getViewModel();
            vm.set('actions.edit', mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            vm.set('actions.view', mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    }

});