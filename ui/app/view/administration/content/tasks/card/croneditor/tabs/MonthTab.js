Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MonthTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-card-croneditor-tabs-monthtab',

    equires: [
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MonthTabModel',
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    viewModel: {
        type: 'administration-content-tasks-card-croneditor-tabs-monthtab'
    },
    controller: 'administration-content-tasks-card-croneditor-tabs-monthtab',
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
                monthValueType: '{monthValueType}'
            }
        },
        items: [{
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.everymonth,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.everymonth'
            },
            name: 'monthValueType',
            inputValue: 'every'
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.periodically,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.periodically'
            },
            name: 'monthValueType',
            inputValue: 'periodic'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMonthPeriodicFields(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.specificmonth,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.specificmonth'
            },
            name: 'monthValueType',
            inputValue: 'specific'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMonthCheckBoxes(), {
            boxLabel: CMDBuildUI.locales.Locales.administration.tasks.range,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.range'
            },
            name: 'monthValueType',
            inputValue: 'between'
        }, CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getMonthBetweenFields()],
        listeners: {
            change: function (input, value) {
                var vm = this.lookupViewModel();
                vm.set('monthValueType', value.monthValueType);
            }
        }
    }]
});