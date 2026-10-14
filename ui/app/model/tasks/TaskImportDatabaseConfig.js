Ext.define('CMDBuildUI.model.tasks.TaskImportDatabaseConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],


    fields: [{
        name: 'tag', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'database',
        calculate: function () {
            return 'database';
        }
    }, {
        name: 'gate', // fixed value
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'INLINE'
    }, {
        name: 'gateconfig_handlers_0_type',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'gate'
    }, {
        name: 'gateconfig_handlers_0_target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_config_jdbcUrl',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_config_jdbcUsername',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'gateconfig_handlers_0_config_jdbcPassword',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'errorTemplate',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
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