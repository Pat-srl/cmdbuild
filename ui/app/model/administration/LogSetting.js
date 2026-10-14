Ext.define('CMDBuildUI.model.administration.LogSetting', {
    extend: 'Ext.data.Model',
    statics: {
        getLevels: function(){
            return [{
                value: 'DEFAULT',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.default
            },{
                value: 'TRACE',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.trace
            }, {
                value: 'DEBUG',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.debug
            }, {
                value: 'INFO',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.info
            }, {
                value: 'WARN',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.warn
            }, {
                value: 'ERROR',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.error
            }];
        }
    },
    fields: [
        { name: 'category', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'level', type: 'string' }
    ]
});
