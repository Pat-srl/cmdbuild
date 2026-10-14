Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.DayTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-card-croneditor-tabs-daytab',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.DayTabModel',
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    viewModel: {
        type: 'administration-content-tasks-card-croneditor-tabs-daytab'
    },
    conroller: 'administration-content-tasks-card-croneditor-tabs-daytab',
    config: {
        theTask: null
    },

    bind: {
        theTask: '{theTask}'
    },
    autoMask: false,
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
                dayValueType: '{dayValueType}'
            }
        },
        items: [{
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.everyday,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.everyday'
                },
                name: 'dayValueType',
                inputValue: 'every'
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.periodicallydayofweek,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.periodicallydayofweek'
                },
                name: 'dayValueType',
                inputValue: 'periodicDayOfTheWeek'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getDayOfTheWeekPeriodicFields(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.periodicallydayofmonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.periodicallydayofmonth'
                },
                name: 'dayValueType',
                inputValue: 'periodicDayOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getDayOfTheMonthPeriodicFields(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.specificdayofweek,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.specificdayofweek'
                },
                name: 'dayValueType',
                inputValue: 'specificDayOfTheWeek'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getDayOfTheWeekCheckBoxes(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.specificdayofmonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.specificdayofmonth'
                },
                name: 'dayValueType',
                inputValue: 'specificDayOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getDayOfTheMonthCheckBoxes(),
            // {
            //     boxLabel: 'Range day of the week',
            //     name: 'dayValueType',
            //     inputValue: 'betweenDayOfTheWeek'
            // },
            // CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getDayOfTheWeekBetweenFields()
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.onthelastdayofthemonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.onthelastdayofthemonth'
                },
                name: 'dayValueType',
                inputValue: 'lastDayOfTheMonth'
            },
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.onthelastweekdayofthemonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.onthelastweekdayofthemonth'
                },
                name: 'dayValueType',
                inputValue: 'lastWeekDayOfTheMonth'
            },
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.onthelastchosendayofthemonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.onthelastchosendayofthemonth'
                },
                name: 'dayValueType',
                inputValue: 'lastChosenWeekDayOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getLastChosenWeekDayOfTheMonth(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.ndaybeforetheendofthemonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.ndaybeforetheendofthemonth'
                },
                name: 'dayValueType',
                inputValue: 'lastNdaysBeforeTheEndOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getNDaysDayBeforeTheEndOfTheMonth(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.nearestweekdaytochosendayofthemonth,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.nearestweekdaytochosendayofthemonth'
                },
                name: 'dayValueType',
                inputValue: 'lastWeekdaysBeforeNearestDayOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getLastWeekdaysBeforeNearestDayOfTheMonth(),
            {
                boxLabel: CMDBuildUI.locales.Locales.administration.tasks.onthenweekdayoftheweek,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.tasks.onthenweekdayoftheweek'
                },
                name: 'dayValueType',
                inputValue: 'theNWeekdayOfTheMonth'
            },
            CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper.getTheNWeekdayOfTheMonth()
        ],
        listeners: {
            change: function (input, value) {
                var vm = this.lookupViewModel();
                vm.set('dayValueType', value.dayValueType);
            }
        }
    }]
});