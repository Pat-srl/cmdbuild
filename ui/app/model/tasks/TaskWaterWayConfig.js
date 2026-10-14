Ext.define('CMDBuildUI.model.tasks.TaskWaterWayConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'tag', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'trigger',
        calculate: function () {
            return 'trigger';
        }
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
    }, {
        name: 'busdescriptor',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'target',
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