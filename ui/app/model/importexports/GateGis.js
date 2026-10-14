Ext.define('CMDBuildUI.model.importexports.GateGis', {
    extend: 'CMDBuildUI.model.importexports.Gate',

    isGate: true,

    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'handlers',
        model: 'CMDBuildUI.model.importexports.GateGisHandler',
        persist: true,
        critical: true,
        field: 'handlers'
    }],
    hasOne: [{
        name: 'config',
        model: 'CMDBuildUI.model.importexports.GateGisConfig',
        persist: true,
        critical: true,
        field: 'config'
    }],


    save: function () {
        var associated = this.getAssociatedData();
        if (associated.handlers) {
            Ext.Array.forEach(associated.handlers, function (handler, index) {
                var inclueOrExclude = associated.handlers[index]._shape_import_include_or_exclude;
                if (inclueOrExclude) {
                    if (inclueOrExclude === CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.include) {
                        associated.handlers[index].shape_import_source_layers_exclude = '';
                    } else if (inclueOrExclude === CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.exclude) {
                        associated.handlers[index].shape_import_source_layers_include = '';
                    } else {
                        associated.handlers[index].shape_import_source_layers_include = '';
                        associated.handlers[index].shape_import_source_layers_exclude = '';
                    }
                }
                delete associated.handlers[index]._id;
                delete associated.handlers[index]._shape_import_include_or_exclude;
                delete associated.handlers[index]._shape_import_target_attr_description;

            });
            this.set('handlers', associated.handlers);
        }
        this.callParent(arguments);
    }
});