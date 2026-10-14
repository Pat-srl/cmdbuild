Ext.define('CMDBuildUI.view.dashboards.dashboard.ContainerController', {
    extend: 'CMDBuildUI.view.dashboards.common.ContainerControllerBase',
    alias: 'controller.dashboards-container',

    onBeforeRender: function (view) {
        const me = this;
        const vm = view.lookupViewModel();
        // callParent can't be used here: it relies on the calling method context, which is lost inside the async bind callback
        const superOnBeforeRender = me.self.superclass.onBeforeRender;

        vm.bind('{object}', function (obj) {
            vm.set('globalParametersData', obj.get('parameters'));
            if (obj.get('parameters') && obj.get('parameters').length > 0) {
                const items = [];
                items.push({
                    xtype: 'dashboards-parameters-parameters',
                    parameters: obj.get('parameters'),
                    itemDefinition: obj
                });
                view.add(items);
            }

            superOnBeforeRender.call(me, view);
        });
    }
});
