Ext.define('CMDBuildUI.model.tasks.TaskImportExport', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
        postImportActions: {
            delete_files: 'delete_files',
            disable_files: 'disable_files',
            move_files: 'move_files',
            do_nothing: 'do_nothing'
        },
        getPostImportActions: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.tasks.deletefiles,
                value: CMDBuildUI.model.tasks.TaskImportExport.postImportActions.delete_files
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.disablefiles,
                value: CMDBuildUI.model.tasks.TaskImportExport.postImportActions.disable_files
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.movefiles,
                value: CMDBuildUI.model.tasks.TaskImportExport.postImportActions.move_files
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.donothing,
                value: CMDBuildUI.model.tasks.TaskImportExport.postImportActions.do_nothing
            }];
        }
    },

    fields: [{
        name: 'config',
        critical: true,
        persist: true,
        reference: {
            type: 'CMDBuildUI.model.tasks.TaskImportExportConfig',
            unique: true
        },
        serialize: function (value, record) {            
            if (value._attach_file) {
                value.notificationMode = 'attach_file';
            }
            return value;
        }       
    }],

    proxy: {
        url: '/jobs/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        },
        writer: {
            type: 'json',
            allDataOptions: {
                associated: true,
                persist: true
            }

        }
    },

    copyForClone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.set('code', '');
        newTask.set('description', '');
        newTask.set('_config', this.getAssociatedData().config);
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});