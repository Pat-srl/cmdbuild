Ext.define('CMDBuildUI.model.tasks.TaskImportIfcTemplateConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'tag', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'ifc',
        calculate: function () {
            return 'ifc';
        }
    }, {
        name: 'gate', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'INLINE'
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
    }, {
        name: 'gateconfig_handlers_0_type',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'filereader' // or urlreader
    }, {
        name: 'gateconfig_handlers_0_directory',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_url',
        type: 'string',
        persist: true,
        critical: true,
        calculate: function (data) {
            if (!Ext.isEmpty(data.gateconfig_handlers_0_bimprojectId)) {
                return Ext.String.format('bimserver:project/{0}/ifc', data.gateconfig_handlers_0_bimprojectId);
            }
            return '';
        },
        serialize: function (value, record) {
            if (!Ext.isEmpty(record.get('gateconfig_handlers_0_bimprojectId'))) {
                return Ext.String.format('bimserver:project/{0}/ifc', record.get('gateconfig_handlers_0_bimprojectId'));
            }
            return value;
        }
    }, {
        name: 'gateconfig_handlers_0_bimprojectId',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // ".*[.]csv" string|REGEX
        name: 'gateconfig_handlers_0_filePattern', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // values delete_files, disable_files, move_files, do_nothing
        name: 'gateconfig_handlers_0_postImportAction', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_targetDirectory',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_1_type',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'gate'
    }, {
        name: 'gateconfig_handlers_1_target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'emailAccount', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'emailTemplate', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        // on_errors, always, never
        name: 'notificationMode', // export
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'never'
    }, {
        name: 'notificationTemplate',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'attachImportReport',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_1_config_bimserver_project_master_card_mode',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_1_config_bimserver_project_master_card_id',
        type: 'string',
        persist: true,
        critical: true
    }],


    proxy: {
        type: 'memory'
    },

    clone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});