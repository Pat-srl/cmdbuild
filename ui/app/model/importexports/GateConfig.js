Ext.define('CMDBuildUI.model.importexports.GateConfig', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        use: {
            cadimport: 'cadimport' // default value
        }
    },
   
    fields: [ {
        name: 'showOnClasses',
        type: 'string',
        persist: true,
        critical: true

    }]
});
