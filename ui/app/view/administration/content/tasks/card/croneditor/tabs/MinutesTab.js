Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MinutesTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-card-croneditor-tabs-minutestab',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MinutesTabModel',
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    viewModel: {
        type: 'administration-content-tasks-card-croneditor-tabs-minutestab'
    },
    controller: 'administration-content-tasks-card-croneditor-tabs-minutestab',
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
                minutesValueType: '{minutesValueType}'
            }
        },
        items: [{
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.everyminutes,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.everyminutes'
            },
            name: 'minutesValueType',
            inputValue: 'every'
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.periodically,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.periodically'
            },
            name: 'minutesValueType',
            inputValue: 'periodic'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMinutesPeriodicFields(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.specificminute,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.specificminute'
            },
            name: 'minutesValueType',
            inputValue: 'specific'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMinutesCheckBoxes(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.range,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.range'
            },
            name: 'minutesValueType',
            inputValue: 'between'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMinutesBetweenFields()],
        listeners: {
            change: function (input, value) {
                var vm = this.lookupViewModel();
                vm.set('minutesValueType', value.minutesValueType);
            }
        }
    }]
});