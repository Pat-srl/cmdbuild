Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.SpecificHelper', {
    singleton: true,

    getMinutesCheckBoxes: function () {
        var getMinutes = function () {
            var minutes = [];
            for (var i = 0; i < 60; i++) {
                minutes.push({
                    boxLabel: i <= 9 ? '0' + i : i,
                    name: i,
                    bind: {
                        value: Ext.String.format('{specific.{0}}', i)
                    }
                });
            }
            return minutes;
        };
        return {
            xtype: 'checkboxgroup',
            hidden: true,
            bind: {
                hidden: '{minutesValueType != "specific"}'
            },
            columns: 10,
            items: getMinutes()
        };
    },
    getMinutesPeriodicFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{minutesValueType != "periodic"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.every,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.every'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{everyMinutesStore}',
                        value: '{everyMinutesValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.minutesstarting,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.minutesstarting'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{startingMinutesStore}',
                        value: '{startingMinutesValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getMinutesBetweenFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{minutesValueType != "between"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.betweenminute,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.betweenminute'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenMinutesStore}',
                        value: '{betweenMinutesValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.andminute,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.andminute'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenAndMinutesStore}',
                        value: '{betweenAndMinutesValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    // hours

    getHoursCheckBoxes: function () {
        var getHours = function () {
            var hours = [];
            for (var i = 0; i < 24; i++) {
                hours.push({
                    boxLabel: i <= 9 ? '0' + i : i,
                    name: i,
                    bind: {
                        value: Ext.String.format('{specific.{0}}', i)
                    }
                });
            }
            return hours;
        };
        return {
            xtype: 'checkboxgroup',
            hidden: true,
            bind: {
                hidden: '{hoursValueType != "specific"}'
            },
            columns: 10,
            items: getHours()
        };
    },
    getHoursPeriodicFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{hoursValueType != "periodic"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.every,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.every'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{everyHoursStore}',
                        value: '{everyHoursValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.hoursstarting,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.hoursstarting'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{startingHoursStore}',
                        value: '{startingHoursValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getHoursBetweenFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{hoursValueType != "between"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.betweenhour,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.betweenhour'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenHoursStore}',
                        value: '{betweenHoursValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.andhour,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.andhour'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenAndHoursStore}',
                        value: '{betweenAndHoursValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },


    // month

    getMonthCheckBoxes: function () {
        var getMonth = function () {
            var months = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
            var month = [];
            for (var i = 0; i < 12; i++) {
                month.push({
                    // boxLabel:Ext.Date.monthNames[i], // i <= 9 ? '0' + (i + 1) : (i + 1),
                    // localized: {
                    //     boxLabel: Ext.String.format('Ext.Date.monthNames[{0}]', i)
                    // },
                    name: months[i],
                    bind: {
                        boxLabel: Ext.String.format('{monthNames.{0}}', months[i]),
                        value: Ext.String.format('{specific.{0}}', months[i])
                    }
                });
            }
            return month;
        };
        return {
            xtype: 'checkboxgroup',
            hidden: true,
            bind: {
                hidden: '{monthValueType != "specific"}'
            },
            columns: 6,
            items: getMonth()
        };
    },
    getMonthPeriodicFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{monthValueType != "periodic"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.every,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.every'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{everyMonthStore}',
                        value: '{everyMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.monthsstarting,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.monthsstarting'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{startingMonthStore}',
                        value: '{startingMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getMonthBetweenFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{monthValueType != "between"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.betweenmonth,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.betweenmonth'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenMonthStore}',
                        value: '{betweenMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.andmonth,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.andmonth'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenAndMonthStore}',
                        value: '{betweenAndMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    // day

    getDayOfTheWeekCheckBoxes: function () {
        var getDay = function () {
            var days = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
            var day = [];

            for (var i = 0; i < 7; i++) {
                day.push({
                    name: days[i],
                    bind: {
                        boxLabel: Ext.String.format('{dayOfTheWeekNames.{0}}', days[i]),
                        value: Ext.String.format('{specificDayOfTheWeek.{0}}', days[i])
                    }
                });
            }
            return day;
        };
        return {
            xtype: 'checkboxgroup',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "specificDayOfTheWeek"}'
            },
            columns: 4,
            items: getDay()
        };
    },

    getDayOfTheMonthCheckBoxes: function () {
        var getDay = function () {
            var day = [];
            for (var i = 1; i <= 31; i++) {
                day.push({
                    name: i,
                    boxLabel: i <= 9 ? '0' + i : i,
                    bind: {
                        value: Ext.String.format('{specificDayOfTheMonth.{0}}', i)
                    }
                });
            }
            return day;
        };
        return {
            xtype: 'checkboxgroup',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "specificDayOfTheMonth"}'
            },
            columns: 10,
            items: getDay()
        };
    },
    getDayOfTheWeekPeriodicFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "periodicDayOfTheWeek"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.every,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.every'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{everyDayOfTheWeekStore}',
                        value: '{everyDayOfTheWeekValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.daysstarting,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.daysstarting'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{startingDayOfTheWeekStore}',
                        value: '{startingDayOfTheWeekValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getDayOfTheMonthPeriodicFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "periodicDayOfTheMonth"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.every,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.every'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{everyDayOfTheMonthStore}',
                        value: '{everyDayOfTheMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.daysofthemonthstarting,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.daysofthemonthstarting'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{startingDayOfTheMonthStore}',
                        value: '{startingDayOfTheMonthValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },


    getDayOfTheWeekBetweenFields: function () {

        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "betweenDayOfTheWeek"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.betweenday,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.betweenday'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenDayOfTheWeekStore}',
                        value: '{betweenDayOfTheWeekInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.andday,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.andday'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{betweenAndDayOfTheWeekStore}',
                        value: '{betweenAndDayOfTheWeekInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getLastChosenWeekDayOfTheMonth: function () {
        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "lastChosenWeekDayOfTheMonth"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.weekday,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.weekday'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{weekdaysStore}',
                        value: '{lastChosenWeekdayInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };
    },

    getNDaysDayBeforeTheEndOfTheMonth: function () {
        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "lastNdaysBeforeTheEndOfTheMonth"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.weekday,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.weekday'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{daysStore}',
                        value: '{lastNDayBeforeTheEndInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };

    },

    getLastWeekdaysBeforeNearestDayOfTheMonth: function () {
        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "lastWeekdaysBeforeNearestDayOfTheMonth"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.day,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.day'
                    },
                    labelAlign: 'left',
                    bind: {
                        store: '{daysStore}',
                        value: '{lastWeekdaysBeforeNearestDayOfTheMonthInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };

    },

    getTheNWeekdayOfTheMonth: function () {
        return {
            xtype: 'panel',
            hidden: true,
            bind: {
                hidden: '{dayValueType != "theNWeekdayOfTheMonth"}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    columnWidth: 0.5,
                    width: 'auto',
                    labelAlign: 'left',
                    bind: {
                        store: '{occurrencesStore}',
                        value: '{occurrenceNInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }, {
                    xtype: 'combobox',
                    width: 'auto',
                    columnWidth: 0.5,
                    labelAlign: 'left',
                    bind: {
                        store: '{occurrenceDaysStore}',
                        value: '{occurrenceDayInputValue}'
                    },
                    labelWidth: 'auto',
                    displayField: 'label',
                    valueField: 'value'
                }]
            }]
        };

    }
});