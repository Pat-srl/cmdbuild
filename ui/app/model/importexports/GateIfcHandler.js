Ext.define('CMDBuildUI.model.importexports.GateIfcHandler', {
    extend: 'CMDBuildUI.model.importexports.GateHandler',
    statics: {
        gatetemplatesStore: null
    },

    fields: [{
        name: 'type', // cad/script/database/ifc
        type: 'string',
        persist: true,
        critical: true,       
        defaultValue: 'ifc'
    }]   
});