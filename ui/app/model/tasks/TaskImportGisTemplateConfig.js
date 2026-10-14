Ext.define('CMDBuildUI.model.tasks.TaskImportGisTemplateConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'tag', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'cad',
        calculate: function () {
            return 'cad';
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
        // required field if the source = file and postImportAction = move_files, this will be the target directory of the move_files action
        name: 'gateconfig_handlers_0_targetDirectory', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_type', // fixed value
        type: 'string',
        critical: true,
        persist: true,
        defaultValue: 'filereader'
    }, {
        name: 'gateconfig_handlers_0_directory', // ”\/tmp\/source"
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'gateconfig_handlers_1_type', // fixed value
        type: 'string',
        critical: true,
        persist: true,
        defaultValue: 'gate'
    }, {
        name: 'gateconfig_handlers_1_target', // gate code
        type: 'string',
        critical: true,
        persist: true
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