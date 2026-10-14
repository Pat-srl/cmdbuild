Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatusesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-systemstatus-systemstatuses',
    control: {
        '#': {
            afterrender: 'onAfterRender',
            refresh: 'reload'
        },
        '#serverManagementTool': {
            click: 'onServerManagementToolClick'
        },
        '#serverManagementRefreshTool': {
            click: 'reload'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatuses} view 
     */
    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
        this.reload();
        vm.bind({
            bindTo: '{clusterNodes}',
            deep: true
        }, function (clusterNodes) {
            if (clusterNodes && !vm.destroyed) {
                Ext.suspendLayouts();
                Ext.Array.forEach(clusterNodes, function (node) {
                    var hostnameView = view.down('#' + node.hostname);
                    if (!hostnameView) {
                        view.add({
                            xtype: 'administration-home-widgets-systemstatus-nodestatus',
                            itemId: node.hostname,
                            title: Ext.String.format('{0} [{1}]', node.hostname, node.hostaddress)
                        });
                    }
                });

                Ext.resumeLayouts(true);
            }
        });
    },

    onServerManagementToolClick: function (tool, e, owner, eOpts) {
        this.redirectTo("#administration/setup/system");
    },

    onSystemInfoRefreshBtnClick: function (button) {
        this.reload();
    },

    reload: function () {
        var vm = this.getViewModel();
        vm.set('showLoader', true);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/system/cluster/nodes/_ALL/invoke',
            method: "POST",
            jsonData: {
                service: 'system',
                method: 'status'
            }
        }).then(function (response, opts) {
            if (!vm.destroyed) {

                var responseJson = Ext.JSON.decode(response.responseText, true);
                var clusterNodes = [];
                var systemStatusGridData = [];
                var keyToUse = Ext.Object.getValues(CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatuses.statskeys);
                Ext.Array.forEach(responseJson.data, function (node) {
                    Ext.Array.forEach(keyToUse, function (key) {

                        systemStatusGridData.push({
                            key: key,
                            value: node.data[key],
                            hostname: node.data.hostname
                        });
                    });
                    clusterNodes.push(node.data);
                });
                vm.set('systemStatusGridData', systemStatusGridData);
                vm.set('clusterNodes', clusterNodes);
                vm.set('_isReady', true);
                vm.set('showLoader', false);
            }
        }, function () {
            if (!vm.destroyed) {
                vm.set('showLoader', false);
            }
        });

    }

});