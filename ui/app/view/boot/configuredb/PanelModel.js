Ext.define('CMDBuildUI.view.boot.configuredb.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.boot-configuredb-panel',

    data: {
        values: {
            configtype: null,
            dbname: null,
            dbhost: 'localhost',
            dbport: 5432,
            dbusername: null,
            dbpassword: null,
            dbadminusername: null,
            dbadminpassword: null,
            dbupload: null
        },
        hiddenfields: {
            filefield: true
        }
    },

    formulas: {
        updateFileVisibility: {
            bind: '{values.configtype}',
            get: function(configtype) {
                var hide = true;
                if (configtype === "upload") {
                    hide = false;
                }
                this.set("hiddenfields.filefield", hide);
            }
        },
        testConnectionDisabled: {
            bind: {
                configtype: '{values.configtype}',
                dbname: '{values.dbname}',
                dbhost: '{values.dbhost}',
                dbport: '{values.dbport}',
                dbusername: '{values.dbusername}'
            },
            get: function(values) {
                return Ext.isEmpty(values.configtype) ||
                    Ext.isEmpty(values.dbname) || 
                    Ext.isEmpty(values.dbhost) || 
                    Ext.isEmpty(values.dbport) || 
                    Ext.isEmpty(values.dbusername);
            }
        },
        filefieldValidation: {
            bind: {
                dbupload: '{values.dbupload}',
                configtype: '{values.configtype}'
            }, 
            get: function(params) {
                if (params.configtype === "upload" && Ext.isEmpty(params.dbupload)) {
                    return "Required";
                }
                return null;
            }
        }
    },

    stores: {
        types: {
            proxy: {
                type: 'memory'
            },
            data : [{
                value: 'empty',
                description: 'Empty'
            }, {
                value: 'demo',
                description: 'Demo'
            }, {
                value: 'existing',
                description: 'Existing'
            }, {
                value: 'upload',
                description: 'Upload backup'
            }]
        }
    }
});
