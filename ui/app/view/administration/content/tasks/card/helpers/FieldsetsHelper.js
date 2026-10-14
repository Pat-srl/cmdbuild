Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper', {
    mixinId: 'administration-task-formmixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.ImportExportMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.EmailServiceMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.SatrtWorkflowMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.ConnectorMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.GisTemplateMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.SendEmailMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.IfcTemplateMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.WaterWayMixin'
    ],
    requires: ['CMDBuildUI.util.administration.helper.FormHelper'],

    getGeneralPropertyPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            /**
             * Name: stringa con regole di validazione standard per i Name. Obbligatorio. Immutabile.
             * Description: stringa traducibile. Obbligatorio.
             * Type: combo con i valori Import, Export. Obbligatorio. Immutabile.
             * Template: combo con l’elenco dei template. Obbligatorio.
             **/
            case CMDBuildUI.model.tasks.Task.types.import_export:
            case CMDBuildUI.model.tasks.Task.types.import_file:
            case CMDBuildUI.model.tasks.Task.types.export_file:
                items = this.importexport.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.workflow:
                items = this.startworkflow.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.importgis:
                if (data[theVmObject]._config.get('tag') === 'cad') {
                    items = this.gistemplate.getGeneralPropertyPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'database') {
                    items = this.connector.getGeneralPropertyPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'ifc') {
                    items = this.ifc.getGeneralPropertyPanel(theVmObject, step, data, this);
                }
                break;
            case CMDBuildUI.model.tasks.Task.types.sendemail:
                items = this.sendemail.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.waterway:
                items = this.waterway.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralPropertyPanelDisabled}'
            },
            items: items
        };

    },

    getSettingsPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.import_export:
            case CMDBuildUI.model.tasks.Task.types.import_file:
            case CMDBuildUI.model.tasks.Task.types.export_file:
                items = this.importexport.getSettingsPanel(theVmObject, step, data, this);

                break;
            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getSettingsPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.importgis:
                if (data[theVmObject]._config.get('tag') === 'cad') {
                    items = this.gistemplate.getSettingsPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'database') {
                    items = this.connector.getSettingsPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'ifc') {
                    items = this.ifc.getSettingsPanel(theVmObject, step, data, this);
                }
                break;
            case CMDBuildUI.model.tasks.Task.types.sendemail:
                items = this.sendemail.getSettingsPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.waterway:
                items = this.waterway.getSettingsPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }


        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.settings,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.settings'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isSettingPanelDisabled}'
            },
            items: items
        };
    },
    getCronPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.import_export:
            case CMDBuildUI.model.tasks.Task.types.import_file:
            case CMDBuildUI.model.tasks.Task.types.export_file:
                items = this.importexport.getCronPanel(theVmObject, step, data, this);
                break;

            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getCronPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.workflow:
                items = this.startworkflow.getCronPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.importgis:
                if (data[theVmObject]._config.get('tag') === 'cad') {
                    items = this.gistemplate.getCronPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'database') {
                    items = this.connector.getCronPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'ifc') {
                    items = this.ifc.getCronPanel(theVmObject, step, data, this);
                }
                break;
            case CMDBuildUI.model.tasks.Task.types.sendemail:
                items = this.sendemail.getCronPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.waterway:
                items = this.waterway.getCronPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.cron,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.cron'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },

    getNotificationPanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.import_export:
            case CMDBuildUI.model.tasks.Task.types.export_file:
            case CMDBuildUI.model.tasks.Task.types.import_file:
                items = this.importexport.getNotificationPanel(theVmObject, step, data, this);
                break;
            case CMDBuildUI.model.tasks.Task.types.importgis:
                if (data[theVmObject]._config.get('tag') === 'cad') {
                    items = this.gistemplate.getNotificationPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'database') {
                    items = this.connector.getNotificationPanel(theVmObject, step, data, this);
                } else if (data[theVmObject]._config.get('tag') === 'ifc') {
                    items = this.ifc.getNotificationPanel(theVmObject, step, data, this);
                }
                break;
            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getNotificationPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.notifications,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.notifications'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getParsePanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getParsePanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.parsing,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.parsing'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getProcessPanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.emailService:
                items = this.emailservice.getProcessPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.process,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.process'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getSourceDataPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.import_database:
                items = this.connector.getSourceDataPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.sourcedata,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.sourcedata'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getImportExportTemplatesGridPanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case CMDBuildUI.model.tasks.Task.types.import_database:
                items = this.connector.getImportExportTemplatesGridPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }

        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: false,
            title: '',
            localized: {
                // title: 'CMDBuildUI.locales.Locales.administration.tasks.sourcedata'
            },
            layout: {
                type: 'column'
            },
            items: items
        };
    }
});