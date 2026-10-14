Ext.define('CMDBuildUI.model.localizations.Localization', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            class: 'class',
            process: 'class',
            domain: 'domain',
            view: 'view',
            lookup: 'lookup',
            dashboard: 'dashboard',
            report: 'report',
            menu: 'menuitem'
        },
        attributeTypes: {
            class: 'attributeclass',
            process: 'attributeclass',
            domain: 'attributedomain'
        }
    },

    fields: [{
            name: 'code',
            type: 'string'
        }, {
            name: 'lang',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        }
    ]
});