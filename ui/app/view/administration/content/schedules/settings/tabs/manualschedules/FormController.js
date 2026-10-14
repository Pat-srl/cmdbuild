Ext.define('CMDBuildUI.view.administration.content.schedules.settings.tabs.manualschedules.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-settings-tabs-manualschedules-form',

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
        },
        '#classesgrid': {
            afterrender: 'onClassGridAfterRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig(this._configKey).then(function (value) {
            vm.set('selectableclasses', value);
        });
    },
    onClassGridAfterRender: function(grid){
        var vm = grid.lookupViewModel();
        vm.bind({
            bindTo: {
                classesStore: '{classes}',
                selected: '{selectableclasses}'
            }
        },
        function (data) {
            if (data.classesStore) {
                var selectedArray = data.selected.split(',');
                var selection = [];
                if (selectedArray.length) {
                    Ext.Array.forEach(selectedArray, function (selected) {
                        var klass = data.classesStore.findRecord('name', selected);

                        if (klass) {
                            selection.push(klass);
                        }
                    });
                    grid.setSelection(selection);
                }
                grid.getSelectionModel().setLocked(true);
                return selection;
            }
        });
    },

    onEditBtnCLick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        button.up('form').down('#classesgrid').getSelectionModel().setLocked(false);
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.getView().up('administration-content-schedules-settings-view').fireEvent('disabletabs');
    },

    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var records = button.up('form').down('#classesgrid').getSelection() || [];
        var selectables = [];
        Ext.Array.forEach(records, function (record) {
            selectables.push(record.get('name'));
        });
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfig(me._configKey, selectables.join(','), true).then(function (value) {
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs(true).then(function () {
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                    CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(function () {
                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', Ext.util.History.getToken(), me);
                        me.getView().up('administration-content-schedules-settings-view').fireEvent('enabletabs');
                    });
                });
            });
        });
    },

    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('administration-content-schedules-settings-view').fireEvent('enabletabs');
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', Ext.util.History.getToken(), this);
    },
    privates: {
        _configKey: 'org.cmdbuild.scheduler.selectableclasses'
    }

});