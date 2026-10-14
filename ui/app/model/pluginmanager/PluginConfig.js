Ext.define('CMDBuildUI.model.pluginmanager.PluginConfig', {
    extend: 'CMDBuildUI.model.base.Base',
    
    fields: [
        {
            name: 'key',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'value',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'access',
            type: 'string',
            persist: true,
            critical: false
        }
    ],

    idProperty: 'key'
});