Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-card-croneditor-panel',

    data: {
        cronParts: {},
        cronData: [],
        activeTab: 0
    },

    formulas: {
        taskManager: {
            bind: '{theTask.config.cronExpression}',
            get: function (cronExperession) {
                if (cronExperession === 'advanced') {
                    cronExperession = '* * ? * *';
                }
                var cronParts = cronExperession.split(' ');
                this.set('cronParts.minutes', cronParts[0]);
                this.set('cronParts.hours', cronParts[1]);
                this.set('cronParts.dayOfTheMonth', cronParts[2]);
                this.set('cronParts.month', cronParts[3]);
                this.set('cronParts.dayOfTheWeek', cronParts[4]);
            }
        },
        cron: {
            bind: {
                minutes: '{cronParts.minutes}',
                hours: '{cronParts.hours}',
                dayOfTheMonth: '{cronParts.dayOfTheMonth}',
                month: '{cronParts.month}',
                dayOfTheWeek: '{cronParts.dayOfTheWeek}'
            },
            get: function (data) {
                this.set('cronData', [{
                    minutes: data.minutes,
                    hours: data.hours,
                    dayOfTheMonth: data.dayOfTheMonth,
                    month: data.month,
                    dayOfTheWeek: data.dayOfTheWeek
                }]);
                var cron = Ext.String.format('{0} {1} {2} {3} {4}', data.minutes, data.hours, data.dayOfTheMonth, data.month, data.dayOfTheWeek);
                this.get('theTask')._config.set('cronExpression', cron);
                return cron;
            }
        }
    },

    stores: {
        cronStore: {
            fields: ['minutes', 'hours', 'dayOfTheMonth', 'month', 'dayOfTheWeek'],
            proxy: 'memory',
            data: '{cronData}'
        }
    }
});