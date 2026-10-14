Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MinutesTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-tabs-minutestab',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                minutes: '{cronParts.minutes}'
            },
            single: true
        }, function (data) {
            var valueType = 'every';
            var currentConfig = data.minutes;
            if (/\//.test(currentConfig)) {
                valueType = 'periodic';
            } else if (/\-/.test(currentConfig)) {
                valueType = 'between';
            } else if (/\,/.test(currentConfig) || /^[0-9]|[0-5][0-9]$/g.test(currentConfig)) {
                valueType = 'specific';
                var specific = {};
                var values;
                try {
                    values = currentConfig.split(',');
                } catch (error) {
                    values = currentConfig;
                }
                values.forEach(function (value) {
                    specific[value] = true;
                });
                vm.set('specific', specific);
            } else {
                valueType = 'every';
                currentConfig = '*';
            }
            vm.set('minutesValueType', valueType);
            vm.set(Ext.String.format('{0}Value', valueType), currentConfig);
        });
    }
});