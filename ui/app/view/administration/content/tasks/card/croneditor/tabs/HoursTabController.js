Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.HoursTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-tabs-hourstab',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                hours: '{cronParts.hours}'
            },
            single: true
        }, function (data) {
            var valueType = 'every';
            var currentConfig = data.hours;
            if (/\//.test(currentConfig)) {
                valueType = 'periodic';
            } else if (/\-/.test(currentConfig)) {
                valueType = 'between';
            } else if (/\,/.test(currentConfig) || /^([0-1]?[0-9]|2[0-3])$/g.test(currentConfig)) {
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
            vm.set('hoursValueType', valueType);
            vm.set(Ext.String.format('{0}Value', valueType), currentConfig);
        });
    }
});