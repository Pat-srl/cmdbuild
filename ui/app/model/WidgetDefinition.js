Ext.define('CMDBuildUI.model.WidgetDefinition', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        getTypes: function () {
            var widgets = [
                {
                    value: 'aiassistant',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.aiassistant, // AI Assistant
                    group: 'core'
                },
                {
                    value: 'calendar',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.calendar, // Calendar
                    group: 'core'
                },
                {
                    value: 'createModifyCard',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.createmodifycard, // Create / Modify Card'
                    group: 'core'
                },
                {
                    value: 'createReport',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.createreport, // Create Report
                    group: 'core'
                },
                {
                    value: 'linkCards',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.linkcards, // Link card
                    group: 'core'
                },
                {
                    value: 'workflow',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow, // Start workflow'
                    group: 'core'
                }
            ];

            var widgetsStore = Ext.getStore('customcomponents.Widgets');
            Ext.Array.forEach(widgetsStore.getRange(), function (item) {
                var customWidget = {
                    value: item.get('name'),
                    label: item.get('description'),
                    group: 'custom'
                };
                widgets.push(customWidget);
            });
            return widgets;
        }
    },
    fields: [
        {
            name: '_type',
            type: 'string'
        },
        {
            name: '_active',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: '_label',
            type: 'string'
        },
        {
            name: '_output',
            type: 'string'
        },
        {
            name: '_required',
            type: 'boolean'
        },
        {
            name: '_config',
            type: 'string'
        },
        {
            name: '_alwaysenabled',
            type: 'boolean'
        },
        {
            name: '_inline',
            type: 'boolean',
            defaultValue: false
        }
    ],

    proxy: {
        type: 'memory'
    },

    /**
     * @return {Function} ExtJS class definition
     * for the widget
     */
    getWidgetClass: function () {
        var widgettype = this.get('_type'),
            xtype = CMDBuildUI.util.Config.widgets[widgettype];
        if (xtype) {
            return Ext.ClassManager.getByAlias('widget.' + xtype);
        } else {
            var store = Ext.StoreManager.get('customcomponents.Widgets'),
                customwidget = store.findRecord('name', widgettype);
            if (customwidget) {
                return Ext.ClassManager.getByAlias(customwidget.get('alias'));
            }
        }
    },

    _ownerButton: null,
    _ownerPanel: null,
    getOwner: function () {
        return this._ownerButton || this._ownerPanel;
    }
});
