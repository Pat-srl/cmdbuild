Ext.define('CMDBuildUI.model.ContextMenuItem', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            custom: 'custom',
            component: 'component',
            separator: 'separator'
        },
        getTypes: function () {
            return [
                {
                    value: 'component',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.component // Component
                },
                {
                    value: 'custom',
                    label: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus
                        .inputs.typeOrGuiCustom.values.custom.label // Custom
                },
                {
                    value: 'separator',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.separator // Separator
                }
            ];
        },
        visibilities: {
            all: 'all',
            many: 'many',
            one: 'one'
        },
        getVisibilities: function () {
            return [
                {
                    value: 'one',
                    label: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus
                        .inputs.applicability.values.one.label
                },
                {
                    value: 'many',
                    label: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus
                        .inputs.applicability.values.many.label
                },
                {
                    value: 'all',
                    label: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus
                        .inputs.applicability.values.all.label
                }
            ];
        }
    },

    fields: [
        {
            name: 'label',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'custom' //"custom"/"component"/"separator",
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'visibility',
            type: 'string',
            defaultValue: 'all' //"one"/"many"/"all",
        },
        {
            //only if type==component
            name: 'componentId',
            type: 'string'
        },
        {
            //only if type==custom
            name: 'script',
            type: 'string'
        },
        {
            //only if type==component
            name: 'config',
            type: 'string'
        },
        {
            name: 'separator',
            type: 'boolean',
            calculate: function (data) {
                return data.type === 'separator';
            }
        },
        {
            name: '_isComponent',
            type: 'boolean',
            defaultValue: false,
            calculate: function (data) {
                return data.type == 'component' ? true : false;
            }
        }
    ],
    proxy: {
        type: 'memory'
    }
});
