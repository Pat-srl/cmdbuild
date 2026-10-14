Ext.define('CMDBuildUI.model.FormTrigger', {
    extend: 'Ext.data.Model',
    
    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],

    fields: [{
        name: "script",
        type: "string"
    }, {
        name: "beforeView",
        type: "boolean",
        defaultValue: false
    }, {
        name: "beforeInsert",
        type: "boolean",
        defaultValue: false
    }, {
        name: "beforeEdit",
        type: "boolean",
        defaultValue: false
    }, {
        name: "beforeClone",
        type: "boolean",
        defaultValue: false
    }, {
        name: "afterInsert",
        type: "boolean",
        defaultValue: false
    }, {
        name: 'afterInsertExecute',
        type: 'boolean',
        defaultValue: false
    }, {
        name: "afterEdit",
        type: "boolean",
        defaultValue: false
    }, {
        name: 'afterEditExecute',
        type: 'boolean',
        defaultValue: false
    }, {
        name: "afterClone",
        type: "boolean",
        defaultValue: false
    }, {
        name: "afterDelete",
        type: "boolean",
        defaultValue: false
    }, {
        name: "active",
        type: "boolean",
        defaultValue: true
    }, {
        name: "selectedTrigger",
        type: 'string', //persist: false,
        calculate: function (data) {

            var triggers = [];
            for (var property in data) {
                if (data[property] === true && property !== 'active') {
                    var label = property.replace(/([A-Z]+)/g, " $1").replace(/([A-Z][a-z])/g, " $1");
                    triggers.push(Ext.String.capitalize(label.toLowerCase()));
                }
            }
            var join = triggers.join(', ');
            return join;
        }
    }],

    validators: ['trimpresence'],
    belongsTo: 'CMDBuildUI.model.classes.Class',

    getSelectedTriggers: function () {
        var triggers = [];
        var data = this.getData();
        for (var property in data) {
            if (data[property] === true && property !== 'active') {                               
                triggers.push(CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values[property].label);
            }
        }
        var join = triggers.join(', ');
        return join;
    },
    proxy: {
        type: 'memory'
    }
});