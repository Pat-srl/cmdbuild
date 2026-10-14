Ext.define('CMDBuildUI.mixins.model.ImportExportTemplates', {
    mixinId: 'model-importexporttemplates-mixin',

    /**
     * @cfg {Function} getAttributesUrl
     * A function that returns the url of the attributes.
     */
    getImportExportTemplatesUrl: Ext.emptyFn,

    /**
     * @cfg {Function} getAttributesUrl
     * A function that returns the url of the attributes.
     */
    getImportExportGatesUrl: Ext.emptyFn,

    /**
     * Load import/export templates
     *
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the import/export templates store and a boolean field.
     */
    getImportExportTemplates: function (force) {
        var deferred = new Ext.Deferred();
        if (this.importExportTemplates) {
            var templates = this.importExportTemplates();
            if (!templates.isLoaded() || force) {
                templates.getProxy().setUrl(this.getImportExportTemplatesUrl());
                // load store
                templates.load({
                    params: {
                        include_related_domains: true,
                        filter: Ext.JSON.encode({
                            "attribute": {
                                "simple": {
                                    "attribute": "fileFormat",
                                    "operator": "in",
                                    "value": [
                                        CMDBuildUI.model.importexports.Template.fileTypes.csv,
                                        CMDBuildUI.model.importexports.Template.fileTypes.xlsx,
                                        CMDBuildUI.model.importexports.Template.fileTypes.xls
                                    ]
                                }
                            }
                        })
                    },
                    callback: function (records, operation, success) {
                        if (success) {
                            deferred.resolve(templates, true);
                        }
                    }
                });
            } else {
                // return promise
                deferred.resolve(templates, false);
            }
        } else {
            deferred.resolve([], true);
        }
        return deferred.promise;
    },

    /**
     * Load GIS import/export templates
     *
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the gis templates store and a boolean field.
     */
    getImportExportGates: function (force) {
        var deferred = new Ext.Deferred();
        if (this.importExportGISTemplates) {
            var templates = this.importExportGISTemplates();
            if (!templates.isLoaded() || force) {
                var handler_types = ['cad', 'database'];
                if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled)) {
                    handler_types.push('ifc');
                }
                templates.getProxy().setUrl(this.getImportExportGatesUrl());
                // load store
                templates.load({
                    params: {
                        _has_single_handler: true,
                        filter: Ext.JSON.encode({
                            "attribute": {
                                "simple": {
                                    "attribute": "_handler_type",
                                    "operator": "in",
                                    "value": handler_types
                                }
                            }
                        }),
                        include_etl_templates: true
                    },
                    callback: function (records, operation, success) {
                        if (success) {
                            deferred.resolve(templates, true);
                        }
                    }
                });
            } else {
                // return promise
                deferred.resolve(templates, false);
            }
        } else {
            deferred.resolve([], true);
        }
        return deferred.promise;
    },

    /**
     * Load all import/export templates (data and GIS)
     *
     * @return {Ext.Deferred} The promise has as paramenters an object with import and export templates.
     */
    getAllTemplatesForImportExport: function () {
        var me = this,
            deferred = new Ext.Deferred();

        Ext.Promise.all([
            me.getImportExportTemplates(),
            me.getImportExportGates()
        ]).then(function (stores) {
            var tpls = {
                import: [],
                export: []
            },
                etl = stores[0],
                gis = stores[1];

            if (etl && etl.getRange) {
                etl.getRange().forEach(function (tpl) {
                    if (Ext.Array.contains([
                        CMDBuildUI.model.importexports.Template.fileTypes.csv,
                        CMDBuildUI.model.importexports.Template.fileTypes.xlsx,
                        CMDBuildUI.model.importexports.Template.fileTypes.xls,
                        CMDBuildUI.model.importexports.Template.fileTypes.ifc
                    ], tpl.get("fileFormat"))) {
                        switch (tpl.get("type")) {
                            case CMDBuildUI.model.importexports.Template.types.import:
                                tpls.import.push(tpl);
                                break;
                            case CMDBuildUI.model.importexports.Template.types.export:
                                tpls.export.push(tpl);
                                break;
                            case CMDBuildUI.model.importexports.Template.types.importexport:
                                tpls.import.push(tpl);
                                tpls.export.push(tpl);
                                break;
                        }
                    }
                });
            }
            if (gis && gis.getRange) {
                gis.getRange().forEach(function (tpl) {
                    tpls.import.push(tpl);
                });
            }

            deferred.resolve(tpls);
        });
        return deferred.promise;
    }
});