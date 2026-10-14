Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.tabs.MonthTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-tabs-monthtab',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                month: '{cronParts.month}'
            },
            single: true
        }, function (data) {
            var valueType = 'every';
            var currentConfig = data.month;
            if (/\//.test(currentConfig)) {
                valueType = 'periodic';
            } else if (/\,/.test(currentConfig) || /^([0-1]?[0-9]|2[0-3])$/g.test(currentConfig)) {
                valueType = 'specific';
                var specific = {};
                try {
                    values = currentConfig.split(',');
                } catch (error) {
                    values = currentConfig;
                }
                values.forEach(function (value) {
                    specific[value] = true;
                });
                vm.set('specific', specific);
            } else if (currentConfig === '*') {
                valueType = 'every';
            } else if (/\-/.test(currentConfig)) {
                valueType = 'between';
            }
            vm.set('monthValueType', valueType);
            vm.set(Ext.String.format('{0}Value', valueType), currentConfig);
        });
    }
});