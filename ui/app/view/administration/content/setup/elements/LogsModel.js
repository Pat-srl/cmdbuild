Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-logs',

    data: {
        actions: {
            edit: false,
            view: true
        },
        disabledTabs: {
            log: false,
            retention: false,
            audit: false,
            governancedata: false
        }
    },

    formulas: {
        configManager: function () {
            var me = this;
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (configs) {
                if (!me.destroyed) {
                    configs.forEach(function (key) {
                        me.set(Ext.String.format('theSetup.{0}', key._key), key.hasValue ? key.value : key.default);
                    });
                }
            });
        },

        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                if (data.isView) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            }
        }
    }
});
