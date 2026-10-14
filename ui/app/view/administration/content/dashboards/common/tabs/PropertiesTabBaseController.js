Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-common-propertiestab',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            closed: 'onClose'
        }
    },
    onClose: function () {
        var view = this.getView(),
            vm = this.getViewModel();
        if (vm.get(view.entityIdVmKey) !== '_new') {
            this.redirectTo(Ext.History.getToken(), true);
        } else {
            this.redirectTo(view.listRoute, true);
        }
    },
    onBeforeRender: function (view) {
        var me = this,
            vm = me.getViewModel();
        if (view.getShowCard()) {
            var config = {
                xtype: view.entityXtype,
                viewModel: {
                    data: {
                        hideForm: vm.get('hideForm')
                    }
                }
            };
            view.add(config);
        } else {
            view.add({
                xtype: 'administration-content-dashboards-grid',
                region: 'center',
                bind: {
                    hidden: '{isGridHidden}'
                }
            });
        }
    }
});
