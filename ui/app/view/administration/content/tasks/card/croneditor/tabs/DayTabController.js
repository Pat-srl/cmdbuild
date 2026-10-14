Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.DayTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-tabs-daytab',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                dm: '{cronParts.dayOfTheMonth}',
                dw: '{cronParts.dayOfTheWeek}'
            },
            single: true
        }, function (data) {
            var currentDayOfTheMonthConfig = data.dm;
            var currentDayOfWeekConfig = data.dw;
            var relatedInput = 'week';
            var valueType = 'every';
            if (/\//.test(currentDayOfWeekConfig)) {
                valueType = 'periodicDayOfTheWeek';
                var periodicDayOfTheWeekValues = currentDayOfWeekConfig.split('/');
                vm.set('startingDayOfTheWeekValue', periodicDayOfTheWeekValues[0]);
                vm.set('everyDayOfTheWeekValue', periodicDayOfTheWeekValues[1]);
            } else if (/\,/.test(currentDayOfWeekConfig) || /^(0?[1-7]|(SUN|MON|TUE|WED|THU|FRI|SAT))$/g.test(currentDayOfWeekConfig)) {
                var specificDayOfTheWeek = {};
                var values = currentDayOfWeekConfig.split(',');
                valueType = 'specificDayOfTheWeek';
                values.forEach(function (value) {
                    specificDayOfTheWeek[value] = true;
                });
                this.set('specificDayOfTheWeek', specificDayOfTheWeek);
            } else if (currentDayOfWeekConfig.indexOf('L') == 1 && !isNaN(Number(currentDayOfWeekConfig[0]))) {
                valueType = 'lastChosenWeekDayOfTheMonth';
                vm.set('lastChosenWeekdayInputValue', currentDayOfWeekConfig[0]);
            } else if (/\-/.test(currentDayOfWeekConfig)) {
                valueType = 'betweenDayOfTheWeek';
            } else if (/\,/.test(currentDayOfTheMonthConfig) || /^(0?[1-9]|[12][0-9]|3[01])$/g.test(currentDayOfTheMonthConfig)) {
                var specificDayOfTheMonth = {};
                valueType = 'specificDayOfTheMonth';
                currentDayOfTheMonthConfig.split(',').forEach(function (value) {
                    specificDayOfTheMonth[value] = true;
                });
                vm.set('specificDayOfTheMonth', specificDayOfTheMonth);
            } else if (/\//.test(currentDayOfTheMonthConfig)) { // day of month
                relatedInput = 'month';
                valueType = 'periodicDayOfTheMonth';
                var periodicDayOfTheMonthValues = currentDayOfTheMonthConfig.split('/');
                vm.set('startingDayOfTheMonthValue', periodicDayOfTheMonthValues[0]);
                vm.set('everyDayOfTheMonthValue', periodicDayOfTheMonthValues[1]);
            } else if (currentDayOfWeekConfig.indexOf('#') === 1) {
                var occurrencyValues = currentDayOfWeekConfig.split('#');
                relatedInput = 'month';
                valueType = 'theNWeekdayOfTheMonth';
                vm.set('occurrenceDayInputValue', occurrencyValues[0]);
                vm.set('occurrenceNInputValue', occurrencyValues[1]);
            } else if (currentDayOfTheMonthConfig.indexOf('L-') === 0) {
                valueType = 'lastNdaysBeforeTheEndOfTheMonth';
                vm.set('lastNDayBeforeTheEndInputValue', currentDayOfTheMonthConfig.replace('L-', ''));
                relatedInput = 'month';
            } else if (/\-/.test(currentDayOfTheMonthConfig)) {
                relatedInput = 'month';
                valueType = 'betweenDayOfTheWeek';
            } else if (currentDayOfTheMonthConfig === 'L') {
                relatedInput = 'month';
                valueType = 'lastDayOfTheMonth';
            } else if (currentDayOfTheMonthConfig === 'LW') {
                relatedInput = 'month';
                valueType = 'lastWeekDayOfTheMonth';
            } else if (/^(0?[1-9]|[12][0-9]|3[01])W$/g.test(currentDayOfTheMonthConfig)) {
                relatedInput = 'month';
                valueType = 'lastWeekdaysBeforeNearestDayOfTheMonth';
            }
            vm.set('dayValueType', valueType);
            vm.set(Ext.String.format('{0}Value', valueType), relatedInput === 'week' ?
                currentDayOfWeekConfig : currentDayOfTheMonthConfig);
        });
    }
});