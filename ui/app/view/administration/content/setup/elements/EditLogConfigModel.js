Ext.define('CMDBuildUI.view.administration.content.setup.elements.EditLogConfigModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-editlogconfig',

    formulas: {
        logSettingValues: {
            get: function () {
                return CMDBuildUI.model.administration.LogSetting.getLevels();
            }
        }
    },
    stores: {
        configKeysStore: {
            model: 'CMDBuildUI.model.administration.LogSetting',
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format('{0}/system/loggers', CMDBuildUI.util.Config.baseUrl),
                extraParams: {
                    includeLoggersWithoutLevel: true
                }
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },
        logSettingValuesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{logSettingValues}',
            autoDestroy: true,
            pageSize: 0
        }
    }
});
