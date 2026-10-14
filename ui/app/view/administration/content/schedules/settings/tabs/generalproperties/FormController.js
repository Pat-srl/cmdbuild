Ext.define('CMDBuildUI.view.administration.content.schedules.settings.tabs.generalproperties.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-settings-tabs-generalproperties-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnCLick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig(this._configKey).then(function (value) {
            vm.set('enabled', Ext.JSON.decode(value));            
        });
    },

    onEditBtnCLick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);        
        this.getView().up('administration-content-schedules-settings-view').fireEvent('disabletabs');
    },

    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfig(me._configKey, vm.get('enabled'), true).then(function (value) {
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs(true).then(function () {
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                    CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(function () {
                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                        me.getView().up('administration-content-schedules-settings-view').fireEvent('enabletabs');
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', Ext.util.History.getToken(), me);                            
                    });
                });
            });
        });
    },

    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig(this._configKey, null, true).then(function (value) {
            if (!vm.destroyed) {
                vm.set('enabled', Ext.JSON.decode(value));
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                me.getView().up('administration-content-schedules-settings-view').fireEvent('enabletabs');
            }
        });
    },

    privates: {
        _configKey: 'org.cmdbuild.calendar.enabled'
    }

});