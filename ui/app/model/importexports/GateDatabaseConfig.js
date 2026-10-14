Ext.define('CMDBuildUI.model.importexports.GateDatabaseConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'tag',
        type: 'string',
        persist: true,
        critical: true,
        calculate: function () {            
            return 'database';
        }
    }, {
        name: 'sourceType', // jdbc
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'jdbc'
    }, {
        name: 'jdbcDriverClassName',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'jdbcUrl',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'jdbcUsername',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'jdbcPassword',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'jdbcTestQuery',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'errorTemplate',
        type: 'string',
        persist: true,
        critical: true
    }]
});