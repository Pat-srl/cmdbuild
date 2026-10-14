Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.DayTabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-card-croneditor-tabs-daytab',
    data: {
        dayValueType: 'every',
        everyDayOfTheWeekValue: '*',
        startingDayOfTheWeekValue: '*',
        specificDayOfTheWeek: {},
        specificDayOfTheMonth: {},
        specificDayOfTheWeekValue: '*',
        everyValue: '*',
        periodicDayOfTheWeekValue: '*/1',
        periodicDayOfTheMonthValue: '*/1',
        betweenDayOfTheWeekValue: '1-1',
        lastChosenWeekdayInputValue: '1',
        lastNDayBeforeTheEndInputValue: '1',
        lastWeekdaysBeforeNearestDayOfTheMonthInputValue: '1',
        occurrenceNInputValue: '1',
        occurrenceDayInputValue: '1',
        everyDayOfTheMonthValue: '1',
        startingDayOfTheMonthValue: '*',
        daysOfTheWeek: ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT']
    },

    formulas: {
        daysTypeManager: {
            bind: {
                type: '{dayValueType}',
                specificDayOfTheWeekValue: '{specificDayOfTheWeekValue}',
                periodicValue: '{periodicDayOfTheWeekValue}',
                betweenValue: '{betweenDayOfTheWeekValue}',
                specificDayOfTheMonthValue: '{specificDayOfTheMonthValue}',
                lastNDayBeforeTheEndInputValue: '{lastNDayBeforeTheEndInputValue}'
            },
            get: function (data) {
                switch (data.type) {
                    case 'specificDayOfTheWeek':
                    case 'periodicDayOfTheWeek':
                    case 'betweenDayOfTheWeek':
                        this.set('cronParts.dayOfTheMonth', '?');
                        this.set('cronParts.dayOfTheWeek', this.get(Ext.String.format('{0}Value', data.type)));
                        break;
                    case 'specificDayOfTheMonth':
                    case 'periodicDayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', '?');
                        this.set('cronParts.dayOfTheMonth', this.get(Ext.String.format('{0}Value', data.type)));
                        break;
                    case 'lastDayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', '?');
                        this.set('cronParts.dayOfTheMonth', 'L');
                        break;
                    case 'lastWeekDayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', '?');
                        this.set('cronParts.dayOfTheMonth', 'LW');
                        break;
                    case 'lastChosenWeekDayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', Ext.String.format('{0}L', this.get('lastChosenWeekdayInputValue')));
                        this.set('cronParts.dayOfTheMonth', '?');
                        break;

                    case 'lastNdaysBeforeTheEndOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', '?');
                        this.set('cronParts.dayOfTheMonth', Ext.String.format('L-{0}', this.get('lastNDayBeforeTheEndInputValue')));
                        break;
                    case 'lastWeekdaysBeforeNearestDayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', '?');
                        this.set('cronParts.dayOfTheMonth', Ext.String.format('{0}W', this.get('lastWeekdaysBeforeNearestDayOfTheMonthInputValue')));
                        break;
                    case 'theNWeekdayOfTheMonth':
                        this.set('cronParts.dayOfTheWeek', Ext.String.format('{0}#{1}', this.get('occurrenceDayInputValue'), this.get('occurrenceNInputValue')));
                        this.set('cronParts.dayOfTheMonth', '?');
                        break;
                    case 'every':
                    default:
                        this.set('cronParts.dayOfTheWeek', '*');
                        this.set('cronParts.dayOfTheMonth', '?');
                        break;
                }
            }
        },

        specificDayOfTheWeekManager: {
            bind: {
                bindTo: '{specificDayOfTheWeek}',
                deep: true
            },
            get: function (specific) {
                var specificValue = [];
                if (typeof specific === 'object' && this.get('dayValueType') === 'specificDayOfTheWeek') {
                    try {
                        Ext.Object.getKeys(specific).forEach(function (key) {
                            if (specific[key]) {
                                specificValue.push(key);
                            }
                        });
                    } catch (e) {}
                    var daysOfTheWeek = this.get('daysOfTheWeek');
                    Ext.Array.sort(specificValue, function (a, b) {
                        var iA = daysOfTheWeek.indexOf(a);
                        var iB = daysOfTheWeek.indexOf(b);
                        return iA === iB ? 0 : (iA < iB ? -1 : 1);
                    });
                    var newSpecific = specificValue.length ? specificValue.join(',') : 'SUN';
                    this.set('specificDayOfTheWeekValue', newSpecific);
                }
            }
        },

        specificDayOfTheMonthManager: {
            bind: {
                bindTo: '{specificDayOfTheMonth}',
                deep: true
            },
            get: function (specific) {
                var specificValue = [];
                Ext.Object.getKeys(specific).forEach(function (key) {
                    if (specific[key]) {
                        specificValue.push(key);
                    }
                });

                Ext.Array.sort(specificValue, Ext.Array.numericSortFn);
                this.set('specificDayOfTheMonthValue', specificValue.length ? specificValue.join(',') : '*');
            }
        },

        periodicDayOfTheWeekManager: {
            bind: {
                periodicDayOfTheWeekValue: '{periodicDayOfTheWeekValue}'
            },
            get: function (data) {
                var values = data.periodicDayOfTheWeekValue.split('/');
                this.set('everyDayOfTheWeekValue', values[1]);
                this.set('startingDayOfTheWeekValue', values[0]);
            }
        },

        everyDayOfTheWeekManager: {
            bind: {
                everyDayOfTheWeekValue: '{everyDayOfTheWeekValue}',
                startingDayOfTheWeekValue: '{startingDayOfTheWeekValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'periodicDayOfTheWeek') {
                    this.set('cronParts.dayOfTheWeek', Ext.String.format('{0}/{1}', data.startingDayOfTheWeekValue, data.everyDayOfTheWeekValue));
                    this.set('cronParts.dayOfTheMonth', '?');
                    this.set('periodicDayOfTheWeek', Ext.String.format('{0}/{1}', data.startingDayOfTheWeekValue, data.everyDayOfTheWeekValue));
                }
            }
        },
        periodicDayOfTheMonthManager: {
            bind: {
                periodicDayOfTheMonthValue: '{periodicDayOfTheMonthValue}'
            },
            get: function (data) {
                var values = data.periodicDayOfTheMonthValue.split('/');
                this.set('everyDayOfTheMonthValue', values[1]);
                this.set('startingDayOfTheMonthValue', values[0]);
            }
        },
        everyDayOfTheMonthManager: {
            bind: {
                everyDayOfTheMonthValue: '{everyDayOfTheMonthValue}',
                startingDayOfTheMonthValue: '{startingDayOfTheMonthValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'periodicDayOfTheMonth') {
                    this.set('cronParts.dayOfTheWeek', '?');
                    this.set('cronParts.dayOfTheMonth', Ext.String.format('{0}/{1}', data.startingDayOfTheMonthValue, data.everyDayOfTheMonthValue));
                    this.set('periodicDayOfTheMonth', Ext.String.format('{0}/{1}', data.startingDayOfTheMonthValue, data.everyDayOfTheMonthValue));
                }
            }
        },

        betweenDayOfTheWeekManager: {
            bind: {
                betweenDayOfTheWeek: '{betweenDayOfTheWeek}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'betweenDayOfTheWeek') {
                    var values = data.betweenDayOfTheWeek.split('-');
                    this.set('betweenDayOfTheWeekInputValue', values[0]);
                    this.set('betweenAndDayOfTheWeekInputValue', values[1]);
                }
            }
        },

        betweenDayOfTheWeekValueManager: {
            bind: {
                betweenDayOfTheWeekValue: '{betweenDayOfTheWeekValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'betweenDayOfTheWeek') {
                    var values = data.betweenDayOfTheWeekValue.split('-');
                    this.set('cronParts.dayOfTheWeek', '?');
                    this.set('cronParts.dayOfTheMonth', Ext.String.format('{0}-{1}', values[1], values[0]));
                }
            }
        },

        everyDayOfTheWeekBetweenManager: {
            bind: {
                betweenDayOfTheWeekValue: '{betweenDayOfTheWeekInputValue}',
                betweenAndDayOfTheWeekValue: '{betweenAndDayOfTheWeekInputValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'lastChosenWeekDayOfTheMonth') {
                    this.set('betweenDayOfTheWeekValue', Ext.String.format('{0}-{1}', data.betweenDayOfTheWeekValue, data.betweenAndDayOfTheWeekValue));
                }
            }
        },

        lastChosenWeekdayInputValueManager: {
            bind: {
                lastChosenWeekdayInputValue: '{lastChosenWeekdayInputValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'lastChosenWeekDayOfTheMonth' && !Ext.isEmpty(data.lastChosenWeekdayInputValue)) {
                    this.set('cronParts.dayOfTheWeek', Ext.String.format('{0}L', data.lastChosenWeekdayInputValue));
                    this.set('cronParts.dayOfTheMonth', '?');
                }
            }
        },

        lastWeekdaysBeforeNearestDayOfTheMonthManager: {
            bind: {
                lastWeekdaysBeforeNearestDayOfTheMonthInputValue: '{lastWeekdaysBeforeNearestDayOfTheMonthInputValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'lastWeekdaysBeforeNearestDayOfTheMonth' && !Ext.isEmpty(data.lastWeekdaysBeforeNearestDayOfTheMonthInputValue)) {
                    this.set('cronParts.dayOfTheWeek', '?');
                    this.set('cronParts.dayOfTheMonth', Ext.String.format('{0}W', data.lastWeekdaysBeforeNearestDayOfTheMonthInputValue));
                }
            }
        },

        weekDayOccurenceInputValueManager: {
            bind: {
                occurrenceNInputValue: '{occurrenceNInputValue}',
                occurrenceDayInputValue: '{occurrenceDayInputValue}'
            },
            get: function (data) {
                if (this.get('dayValueType') === 'theNWeekdayOfTheMonth') {
                    this.set('cronParts.dayOfTheWeek', Ext.String.format('{0}#{1}', data.occurrenceDayInputValue, data.occurrenceNInputValue));
                    this.set('cronParts.dayOfTheMonth', '?');
                }
            }
        },

        dayOfTheWeekNames: function () {
            var daysOfTheWeek = this.get('daysOfTheWeek');
            var dayNames = {};
            for (var i = 0; i < 7; i++) {
                dayNames[daysOfTheWeek[i]] = Ext.Date.dayNames[i];
            }
            return dayNames;
        },

        everyDayOfTheWeek: function () {
            var everyDays = [];
            for (var i = 1; i <= 7; i++) {
                var item = {
                    value: i,
                    label: i
                };
                everyDays.push(item);
            }
            return everyDays;
        },

        startingDayOfTheWeek: function () {
            var startingDays = [{
                value: '*',
                label: '*'
            }];
            for (var i = 1; i <= 7; i++) {
                var item = {
                    value: i,
                    label: Ext.Date.dayNames[i - 1]
                };
                startingDays.push(item);
            }
            return startingDays;
        },

        everyDayOfTheMonth: function () {
            var everyDays = [];
            for (var i = 1; i <= 31; i++) {
                var item = {
                    value: i,
                    label: i
                };
                everyDays.push(item);
            }
            return everyDays;
        },

        startingDayOfTheMonth: function () {
            var startingDays = [{
                value: '*',
                label: '*'
            }];
            for (var i = 1; i <= 31; i++) {
                var item = {
                    value: i,
                    label: i < 10 ? '0' + i : i
                };
                startingDays.push(item);
            }
            return startingDays;
        },

        occurrences: function () {
            return [{
                value: '1',
                label: CMDBuildUI.locales.Locales.administration.tasks.first
            }, {
                value: '2',
                label: CMDBuildUI.locales.Locales.administration.tasks.second
            }, {
                value: '3',
                label: CMDBuildUI.locales.Locales.administration.tasks.third
            }, {
                value: '4',
                label: CMDBuildUI.locales.Locales.administration.tasks.fourth
            }, {
                value: '5',
                label: CMDBuildUI.locales.Locales.administration.tasks.fifth
            }];
        },

        occurrenceDays: function () {
            var startingDays = [];
            for (var i = 1; i <= 7; i++) {
                var item = {
                    value: i,
                    label: Ext.Date.dayNames[i - 1]
                };
                startingDays.push(item);
            }
            return startingDays;
        }
    },

    stores: {

        everyDayOfTheWeekStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{everyDayOfTheWeek}',
            autoDestroy: true
        },

        startingDayOfTheWeekStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheWeek}',
            autoDestroy: true
        },

        everyDayOfTheMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{everyDayOfTheMonth}',
            autoDestroy: true
        },

        startingDayOfTheMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheMonth}',
            autoDestroy: true
        },

        betweenDayOfTheWeekStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheWeek}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },

        betweenAndDayOfTheWeekStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheWeek}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },

        weekdaysStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheWeek}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },

        daysStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingDayOfTheMonth}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },

        occurrencesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{occurrences}',
            autoDestroy: true
        },

        occurrenceDaysStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{occurrenceDays}',
            autoDestroy: true
        }
    }
});