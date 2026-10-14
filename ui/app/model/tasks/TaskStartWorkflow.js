Ext.define('CMDBuildUI.model.tasks.TaskStartWorkflow', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'
    ],
    statics: {
       
    },
    fields: [{
        name: 'config',
        critical: true,
        reference: {
            type: 'CMDBuildUI.model.tasks.StartWorkflowConfig',
            unique: true
        }
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


