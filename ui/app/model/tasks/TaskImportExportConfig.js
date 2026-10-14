Ext.define('CMDBuildUI.model.tasks.TaskImportExportConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'fileName', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'template', // import/export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'directory', // import/export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'cronExpression', // import/export
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
    }, {
        // for values see statics.sourceTypes
        name: 'source', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // ".*[.]csv" string|REGEX
        name: 'filePattern', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // required if source === file
        // values delete_files, disable_files, move_files, do_nothing
        name: 'postImportAction', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // required field if the source = file and postImportAction = move_files, this will be the target directory of the move_files action
        name: 'targetDirectory', // import
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
        defaultValue: 'never',
        depends: '_attach_file',
        convert: function (value, record) {            
            if (value === 'attach_file') {
                record.data._attach_file = true;
                return 'always';
            }
            return value;
        }

    }, {
        name: '_attach_file', // export
        type: 'boolean',
        persist: false,
        critical: false
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