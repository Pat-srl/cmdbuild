Ext.define('CMDBuildUI.model.importexports.GateDatabase', {
    extend: 'CMDBuildUI.model.importexports.Gate',

    isGate: true,
    statics: {
        sourceTypes: {
            jdbc: 'jdbc'
        },
    
        jdbcDrivers: {
            oracle: 'oracle.jdbc.driver.OracleDriver',
            postgres: 'org.postgresql.Driver',
            mysqlmaria: 'org.mariadb.jdbc.Driver',
            sqlserver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
        }
    },
    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'handlers',
        model: 'CMDBuildUI.model.importexports.GateDatabaseHandler',
        persist: true,
        critical: true,
        field: 'handlers'
    }],
    hasOne: [{
        name: 'config',
        model: 'CMDBuildUI.model.importexports.GateDatabaseConfig',
        persist: true,
        critical: true,
        field: 'config'
    }],
    fields: [{
        name: '_handler_type',
        type: 'string',
        defaultValue: 'database',
        critical: true
    }],



    save: function () {

        this.callParent(arguments);
    }
});