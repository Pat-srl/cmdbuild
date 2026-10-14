Ext.define('CMDBuildUI.model.tasks.StartWorkflowConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [ {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
    }, {
        name: 'classname',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'attributes',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'username',
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