Ext.define('CMDBuildUI.model.tasks.TaskSendEmailConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
        attachreportformat: {
            pdf: 'pdf',
            odt: 'odt'
        }
    },
    fields: [{
        name: 'email_template',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'email_account',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'email_template_context',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'attach_report_enabled',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: false
    }, {
        name: 'attach_report_code',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'attach_report_format',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'attach_report_params',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'advanced'
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