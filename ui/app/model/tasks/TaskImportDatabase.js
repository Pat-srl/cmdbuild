Ext.define('CMDBuildUI.model.tasks.TaskImportDatabase', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'        
    ],
    

    fields: [{
        name: 'config',

        reference: {
            type: 'CMDBuildUI.model.tasks.TaskImportDatabaseConfig',
            unique: true
        }
    }],

    proxy: {
        url: '/jobs/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        },
        writer: {
            type: 'json'
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