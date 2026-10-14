Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MinutesTabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-card-croneditor-tabs-minutestab',
    data: {
        minutesValueType: null,
        specific: {},
        specificValue: '',
        everyValue: '*',
        periodicValue: '*/1',
        betweenValue: '0-0'

    },
    formulas: {
        minutesTypeManager: {
            bind: {
                type: '{minutesValueType}',
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
                        this.set('cronParts.minutes', this.get(Ext.String.format('{0}Value', data.type)));
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
                        specificValue.push(parseInt(key));
                    }
                });
                Ext.Array.sort(specificValue, Ext.Array.numericSortFn);
                var value = specificValue.length ? specificValue.splice(',') : '*';
                this.set('specificValue', value);
            }
        },
        periodicManager: {
            bind: {
                periodicValue: '{periodicValue}'
            },
            get: function (data) {
                var values = data.periodicValue.split('/');
                this.set('everyMinutesValue', values[1]);
                this.set('startingMinutesValue', values[0]);
            }
        },

        everyManager: {
            bind: {
                everyMinutesValue: '{everyMinutesValue}',
                startingMinutesValue: '{startingMinutesValue}'
            },
            get: function (data) {
                this.set('periodicValue', Ext.String.format('{0}/{1}', data.startingMinutesValue, data.everyMinutesValue));
            }
        },

        betweenManager: {
            bind: {
                betweenValue: '{betweenValue}'
            },
            get: function (data) {
                var values = data.betweenValue.split('-');
                this.set('betweenMinutesValue', values[0]);
                this.set('betweenAndMinutesValue', values[1]);
            }
        },

        everyBetweenManager: {
            bind: {
                betweenMinutesValue: '{betweenMinutesValue}',
                betweenAndMinutesValue: '{betweenAndMinutesValue}'
            },
            get: function (data) {
                this.set('betweenValue', Ext.String.format('{0}-{1}', data.betweenMinutesValue, data.betweenAndMinutesValue));
            }
        },

        everyMinutes: function () {
            var everyMinutes = [];
            for (var i = 1; i <= 60; i++) {
                var item = {
                    value: i,
                    label: i
                };
                everyMinutes.push(item);
            }
            return everyMinutes;
        },
        startingMinutes: function () {
            var startingMinutes = [{
                value: '*',
                label: '*'
            }];
            for (var i = 0; i < 60; i++) {
                var item = {
                    value: i,
                    label: i < 10 ? '0' + i : i
                };
                startingMinutes.push(item);
            }
            return startingMinutes;
        }
    },
    stores: {
        everyMinutesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{everyMinutes}',
            autoDestroy: true
        },
        startingMinutesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMinutes}',
            autoDestroy: true
        },
        betweenMinutesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMinutes}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },
        betweenAndMinutesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingMinutes}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        }

    }
});