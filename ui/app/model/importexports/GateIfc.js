Ext.define('CMDBuildUI.model.importexports.GateIfc', {
    extend: 'CMDBuildUI.model.importexports.Gate',

    isGate: true,

    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'handlers',
        model: 'CMDBuildUI.model.importexports.GateIfcHandler',
        persist: true,
        critical: true,
        field: 'handlers'
    }],
    hasOne: [{
        name: 'config',
        model: 'CMDBuildUI.model.importexports.GateIfcConfig',
        persist: true,
        critical: true,
        field: 'config'
    }],
    

    save: function () {
      
        this.callParent(arguments);
    }
});