Ext.define('CMDBuildUI.view.importexport.ImportModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.importexport-import',

    data: {
        selectedtemplate: null,
        response: {},
        values: {
            template: null,
            file: null,
            sourcetype: "file",
            bimproject: null,
            ifccard: null
        },
        hidden: {},
        disabled: {},
        labels: {}
    },

    formulas: {
        updateData: {
            bind: {
                classmodel: '{classmodel}'
            },
            get: function () {
                var view = this.getView();

                // templates
                var templates = view.getTemplates();
                this.set("templatesdata", templates);

                // default template
                var object = view.getObject();
                if (object.get("defaultImportTemplate")) {
                    this.set("values.template", object.get("defaultImportTemplate"));
                } else if (templates.length === 1) {
                    this.set("values.template", templates[0].getId());
                }
            }
        },

        updateFieldsVisibility: {
            bind: {
                template: '{values.template}',
                sourcetype: '{values.sourcetype}',
                templates: '{templatesdata}'
            },
            get: function (data) {
                var hidefile = true,
                    hidesource = true,
                    hidebimproject = true,
                    hideifccard = true;
                if (data.template) {
                    var template = Ext.Array.findBy(data.templates, function (t) {
                        return t.get("_id") == (data.template.split(':')[1] || data.template.split(':')[0]);
                    });
                    if (template) {
                        hidefile = false;
                        switch (template.get("_handler_type")) {
                            case CMDBuildUI.model.importexports.Gate.gateType.database:
                                hidefile = true;
                                break;
                            case CMDBuildUI.model.importexports.Gate.gateType.ifc:
                                hidesource = false;
                                if (data.sourcetype === "project") {
                                    hidefile = true;
                                    hidebimproject = false;
                                } else {
                                    hideifccard = false;
                                }
                        }
                    }
                }
                this.set("hidden.file", hidefile);
                this.set("hidden.sourcetype", hidesource);
                this.set("hidden.bimproject", hidebimproject);
                this.set("hidden.ifccard", hideifccard);
            }
        },

        importBtnDisabled: {
            bind: {
                hidden: '{hidden.file}',
                template: '{values.template}',
                templates: '{templatesdata}',
                sourcetype: '{values.sourcetype}',
                bimproject: '{values.bimproject}',
                file: '{values.file}'
            },
            get: function (data) {

                var template = Ext.Array.findBy(data.templates, function (t) {
                    if (data.template) {
                        return t.get("_id") == (data.template.split(':')[1] || data.template.split(':')[0]);
                    } else {
                        return false;
                    }
                });
                var disable = true;
                if (template) {
                    switch (template.get("_handler_type")) {
                        case CMDBuildUI.model.importexports.Gate.gateType.database:
                            disable = false;
                            break;
                        case CMDBuildUI.model.importexports.Gate.gateType.ifc:
                            if (data.sourcetype === "project") {
                                disable = Ext.isEmpty(data.bimproject);
                            } else {
                                disable = Ext.isEmpty(data.file);
                            }
                            break;
                        default:
                            disable = Ext.isEmpty(data.file);
                    }
                }
                this.set("disabled.importbtn", disable);
            }
        },

        responseText: {
            bind: {
                response: '{response}'
            },
            get: function (data) {
                if (data.response && !Ext.Object.isEmpty(data.response)) {
                    return Ext.String.format(
                        "<strong>{0}</strong>: {1} - <strong>{2}</strong>: {3} - <strong>{4}</strong>: {5} - <strong>{6}</strong>: {7} - <strong>{8}</strong>: {9} - <strong>{10}</strong>: {11}",
                        CMDBuildUI.locales.Locales.importexport.response.processed,
                        data.response.processed,
                        CMDBuildUI.locales.Locales.importexport.response.created,
                        data.response.created,
                        CMDBuildUI.locales.Locales.importexport.response.modified,
                        data.response.modified,
                        CMDBuildUI.locales.Locales.importexport.response.deleted,
                        data.response.deleted,
                        CMDBuildUI.locales.Locales.importexport.response.unchanged,
                        data.response.unmodified,
                        CMDBuildUI.locales.Locales.importexport.response.errors,
                        data.response.errors.length
                    );
                }
            }
        },

        sourceTypesData: {
            get: function () {
                return [{
                    value: 'file',
                    label: CMDBuildUI.locales.Locales.attachments.file
                }, {
                    value: 'project',
                    label: CMDBuildUI.locales.Locales.importexport.ifc.project
                }];
            }
        },

        bimprojectautoload: {
            get: function () {
                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
            }

        }
    },

    stores: {
        templates: {
            model: 'CMDBuildUI.model.importexports.Template',
            proxy: 'memory',
            data: '{templatesdata}',
            autoDestroy: true
        },

        sourcetypes: {
            fields: ['value', 'label'],
            data: "{sourceTypesData}"
        },

        bimprojects: {
            proxy: {
                type: 'baseproxy',
                url: '/bim/projects'
            },
            autoLoad: '{bimprojectautoload}',
            autoDestroy: true
        },

        errors: {
            fields: [{
                type: 'int',
                name: 'recordNumber'
            }, {
                type: 'int',
                name: 'lineNumber'
            }, {
                type: 'string',
                name: 'message'
            }],
            proxy: 'memory',
            data: '{response.errors}',
            autoDestroy: true
        }
    }

});