Ext.define('CMDBuildUI.model.importexports.GateGisConfig', {
    extend: 'CMDBuildUI.model.importexports.GateConfig',
    statics: {
        use: {
            cadimport: 'cadimport' // default value
        }
    },

    fields: [{
        name: 'tag',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'cad',
        calculate: function () {
            return 'cad';
        }
    }, {
        name: 'use', // cad/script
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'cadimport'
    }]
});