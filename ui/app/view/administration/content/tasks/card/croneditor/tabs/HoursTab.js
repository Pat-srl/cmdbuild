Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.HoursTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-card-croneditor-tabs-hourstab',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.HoursTabModel',
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    viewModel: {
        type: 'administration-content-tasks-card-croneditor-tabs-hourstab'
    },
    controller: 'administration-content-tasks-card-croneditor-tabs-hourstab',
    config: {
        theTask: null
    },

    bind: {
        theTask: '{theTask}'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,

    ui: 'administration-formpagination',

    items: [{
        xtype: 'radiogroup',
        columns: 1,
        vertical: true,
        layout: 'fit',
        bind: {
            value: {
                hoursValueType: '{hoursValueType}'
            }
        },
        items: [{
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.everyhours,
            localized: {
                boxLabel: "CMDBuildUI.locales.Locales.administration.tasks.everyhours"
            },
            name: 'hoursValueType',
            inputValue: 'every'
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.periodically,
            localized: {
                boxLabel: "CMDBuildUI.locales.Locales.administration.tasks.periodically"
            },
            name: 'hoursValueType',
            inputValue: 'periodic'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getHoursPeriodicFields(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.specifichour,
            localized: {
                boxLabel: "CMDBuildUI.locales.Locales.administration.tasks.specifichour"
            },
            name: 'hoursValueType',
            inputValue: 'specific'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getHoursCheckBoxes(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.range,
            localized: {
                boxLabel: "CMDBuildUI.locales.Locales.administration.tasks.range"
            },
            name: 'hoursValueType',
            inputValue: 'between'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getHoursBetweenFields()],
        listeners: {
            change: function (input, value) {
                var vm = this.lookupViewModel();
                vm.set('hoursValueType', value.hoursValueType);
            }
        }
    }]
});