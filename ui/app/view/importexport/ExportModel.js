Ext.define('CMDBuildUI.view.importexport.ExportModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.importexport-export',

    data: {
        exporturl: null,
        selectedTemplate: null,
        isDomainExport: false,
        values: {
            export: 'filtered',
            template: null
        }
    },

    formulas: {
        updateData: {
            get: function () {
                const view = this.getView();

                // templates
                const templates = view.getTemplates();
                this.set("templatesdata", templates);

                // default template
                const object = view.getObject();
                if (object.get("defaultExportTemplate")) {
                    this.set("values.template", object.get("defaultExportTemplate"));
                } else if (templates.length === 1) {
                    this.set("values.template", templates[0].getId());
                }
            }
        },

        exportTypesData: {
            get: function () {
                return [{
                    value: 'all',
                    label: CMDBuildUI.locales.Locales.importexport.exportalldata
                }, {
                    value: 'filtered',
                    label: CMDBuildUI.locales.Locales.importexport.exportfiltereddata
                }]
            }
        }
    },

    stores: {
        templates: {
            model: 'CMDBuildUI.model.importexports.Template',
            proxy: 'memory',
            data: '{templatesdata}'
        },

        exporttypes: {
            fields: [{
                type: 'string',
                name: 'label'
            }, {
                type: 'string',
                name: 'value'
            }],
            autoDestroy: true,
            data: '{exportTypesData}'
        }
    }
});
