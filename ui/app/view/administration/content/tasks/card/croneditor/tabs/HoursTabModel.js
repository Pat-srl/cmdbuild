Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.HoursTabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-card-croneditor-tabs-hourstab',
    data: {
        hoursValueType: null,
        specific: {},
        specificValue: '',
        everyValue: '*',
        periodicValue: '*/1',
        betweenValue: '0-0'
    },
    formulas: {
        hoursTypeManager: {
            bind: {
                type: '{hoursValueType}',
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
                        this.set('cronParts.hours', this.get(Ext.String.format('{0}Value', data.type)));
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
                this.set('everyHoursValue', values[1]);
                this.set('startingHoursValue', values[0]);
            }
        },

        everyManager: {
            bind: {
                everyHoursValue: '{everyHoursValue}',
                startingHoursValue: '{startingHoursValue}'
            },
            get: function (data) {
                this.set('periodicValue', Ext.String.format('{0}/{1}', data.startingHoursValue, data.everyHoursValue));
            }
        },

        betweenManager: {
            bind: {
                betweenValue: '{betweenValue}'
            },
            get: function (data) {
                var values = data.betweenValue.split('-');
                this.set('betweenHoursValue', values[0]);
                this.set('betweenAndHoursValue', values[1]);
            }
        },

        everyBetweenManager: {
            bind: {
                betweenHoursValue: '{betweenHoursValue}',
                betweenAndHoursValue: '{betweenAndHoursValue}'
            },
            get: function (data) {
                this.set('betweenValue', Ext.String.format('{0}-{1}', data.betweenHoursValue, data.betweenAndHoursValue));
            }
        },

        everyHours: function () {
            var everyHours = [];
            for (var i = 1; i <= 24; i++) {
                var item = {
                    value: i,
                    label: i
                };
                everyHours.push(item);
            }
            return everyHours;
        },
        startingHours: function () {
            var startingHours = [{
                value: '*',
                label: '*'
            }];
            for (var i = 0; i < 24; i++) {
                var item = {
                    value: i,
                    label: i < 10 ? '0' + i : i
                };
                startingHours.push(item);
            }
            return startingHours;
        }
    },
    stores: {
        everyHoursStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{everyHours}',
            autoDestroy: true
        },
        startingHoursStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingHours}',
            autoDestroy: true
        },
        betweenHoursStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingHours}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        },
        betweenAndHoursStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{startingHours}',
            filters: [function (item) {
                return item.get('value') !== '*';
            }],
            autoDestroy: true
        }

    }
});