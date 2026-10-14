Ext.define('CMDBuildUI.model.tasks.TaskReadEmail', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'
    ],
    statics: {
        getFilterTypes: function(){
            return [{
                label: CMDBuildUI.locales.Locales.administration.common.strings.none,
                value: 'none'
            },{
                label: CMDBuildUI.locales.Locales.administration.tasks.regex,
                value: 'regex'
            }, {
                label: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                value: 'function'
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.isreply,
                value: 'isreply'
            }, {
                label: CMDBuildUI.locales.Locales.administration.tasks.isnotreply,
                value: 'isnotreply'
            }];
        }
    },
    fields: [{
        name: 'config',
        critical: true,        
        reference: {
            type: 'CMDBuildUI.model.tasks.ReadEmailConfig',
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


