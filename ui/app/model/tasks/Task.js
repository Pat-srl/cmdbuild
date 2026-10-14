Ext.define('CMDBuildUI.model.tasks.Task', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],
    statics: {
        types: {
            import_export: 'import_export',
            import_file: 'import_file',
            export_file: 'export_file',
            emailService: 'emailService',
            workflow: 'workflow',
            import_database: 'import_database',
            importgis: 'etl',
            sendemail: 'sendemail',
            waterway: 'trigger'
        },
        typeGroups: {
            import_export: 'import_export',
            emailService: 'emailService',
            workflow: 'workflow',
            import_database: 'import_database',
            importgis: 'etl',
            sendemail: 'sendemail',
            waterway: 'trigger'
        },
        cronDeafultValues: {
            everyhour: '0 * ? * *',
            everyday: '0 4 ? * *',
            everymonth: '0 4 1 * ?',
            everyyear: '0 4 1 JAN ?',
            advanced: 'advanced'
        },
        getTypes: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.tasks.texts.reademails,
                value: CMDBuildUI.model.tasks.Task.types.emailService,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.emailService,
                groupLabel: CMDBuildUI.locales.Locales.administration.tasks.texts.reademails
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.sendemail,
                value: CMDBuildUI.model.tasks.Task.types.sendemail,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.sendemail,
                groupLabel: CMDBuildUI.locales.Locales.administration.tasks.sendemail
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.import,
                value: CMDBuildUI.model.tasks.Task.types.import_file,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.import_export,
                groupLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.export,
                value: CMDBuildUI.model.tasks.Task.types.export_file,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.import_export,
                groupLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.importdatabase,
                value: 'etl',
                subType: 'database',
                group: CMDBuildUI.model.tasks.Task.typeGroups.import_database,
                groupLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importdatabase
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.importgis,
                value: 'etl',
                subType: 'cad',
                group: CMDBuildUI.model.tasks.Task.typeGroups.importgis,
                groupLabel: CMDBuildUI.locales.Locales.administration.tasks.importgis
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportifcgatetemplate,
                value: 'etl',
                subType: 'ifc',
                group: 'ifc',
                groupLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportifcgatetemplate
            }, {
                label: CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow,
                value: CMDBuildUI.model.tasks.Task.types.workflow,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.workflow,
                groupLabel: CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow
            }, {
                label: CMDBuildUI.locales.Locales.administration.navigation.servicebus,
                value: CMDBuildUI.model.tasks.Task.types.waterway,
                subType: null,
                group: CMDBuildUI.model.tasks.Task.typeGroups.waterway,
                groupLabel: CMDBuildUI.locales.Locales.administration.navigation.servicebus
            }];
        },
        getCronSettings: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.tasks.everyhour,
                value: CMDBuildUI.model.tasks.Task.cronDeafultValues.everyhour
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.everyday,
                value: CMDBuildUI.model.tasks.Task.cronDeafultValues.everyday
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.everymonth,
                value: CMDBuildUI.model.tasks.Task.cronDeafultValues.everymonth
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.everyyear,
                value: CMDBuildUI.model.tasks.Task.cronDeafultValues.everyyear
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.advanced,
                value: CMDBuildUI.model.tasks.Task.cronDeafultValues.advanced
            }];
        }
    },
    fields: [{
        name: 'code',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'type',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'enabled',
        type: 'boolean',
        persist: true,
        critical: true
    }],

    proxy: {
        url: '/jobs/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    },

    clone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.set('code', '');
        newTask.set('description', '');
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});