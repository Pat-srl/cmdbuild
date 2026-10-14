Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MonthTabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-card-croneditor-tabs-monthtab',
    data: {
        monthValueType: null,
        specific: {},
        specificValue: '*',
        everyValue: '*',
        periodicValue: '*/1',
        betweenValue: '1-1',
        months: ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']

    },
    formulas: {
        monthNames: function () {
            var months = this.get('months');
            var monthNames = {};
            for (var i = 0; i <= 12; i++) {
                monthNames[months[i]] = Ext.Date.monthNames[i];
            }
            return monthNames;
        },
        minutesTypeManager: {
            bind: {
                type: '{monthValueType}',
                specificValue: '{specificValue}',
                periodicValue: '{periodicValue}',
                betweenValue: '{betweenValue}'
            },
            get: function (data) {
                switch (data.type) {
                    case 'specific':
                    case 'every':
                    case 'periodic':
                    case 'between':
                        this.set('cronParts.month', this.get(Ext.String.format('{0}Value', data.type)));
                        break;
                    default:
                        break;
                }
            }
        },
        specificManager: {
            bind: {
                bindTo: '{specific}',
                deep: true
            },
            get: function (specific) {
                var specificValue = [];
                Ext.Object.getKeys(specific).forEach(function (key) {
                    if (specific[key]) {
                        specificValue.push(key);
                    }
                });
                var months = this.get('months');
                Ext.Array.sort(specificValue, function (a, b) {
                    var iA = months.indexOf(a);
                    var iB = months.indexOf(b);
                    return iA === iB ? 0 : (iA < iB ? -1 : 1);
                });
                var value = specificValue.length ? specificValue.join(',') : '*';
                this.set('specificValue', value);
            }
        },
        periodicManager: {
            bind: {
                periodicValue: '{periodicValue}'
            },
            get: function (data) {
                var values = data.periodicValue.split('/');
                this.set('everyMonthValue', values[1]);
                this.set('startingMonthValue', values[0]);
            }
        },

        everyManager: {
            bind: {
                everyMonthsValue: '{everyMonthValue}',
                startingMonthsValue: '{startingMonthValue}'
            },
            get: function (data) {
                this.set('periodicValue', Ext.String.format('{0}/{1}', data.startingMonthsValue, data.everyMonthsValue));
            }
        },

        betweenManager: {
            bind: {
                betweenValue: '{betweenValue}'
            },
            get: function (data) {
                var values = data.betweenValue.split('-');
                this.set('betweenMonthValue', values[0]);
                this.set('betweenAndMonthValue', values[1]);
            }
        },

        everyBetweenManager: {
            bind: {
                betweenMonthsValue: '{betweenMonthValue}',
                betweenAndMonthsValue: '{betweenAndMonthValue}'
            },
            get: function (data) {
                this.set('betweenValue', Ext.String.format('{0}-{1}', data.betweenMonthsValue, data.betweenAndMonthsValue));
            }
        },

        everyMonths: function () {
            var everyMonths = [];
            for (var i = 1; i <= 12; i++) {
                var item = {
                    value: i,
                    label: i
                };
                everyMonths.push(item);
            }
            return everyMonths;
        },
        startingMonths: function () {
            var startingMonths = [{
                value: '*',
                label: '*'
            }];
            for (var i = 1; i <= 12; i++) {
                var item = {
                    value: i,
                    label: Ext.Date.monthNames[i - 1]
                };
                startingMonths.push(item);
            }
            return startingMonths;
        }
    },
    stores: {
        everyMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{everyMonths}',
            autoDestroy: true
        },
        startingMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMonths}',
            autoDestroy: true
        },
        betweenMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMonths}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },
        betweenAndMonthStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMonths}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        }

    }
});