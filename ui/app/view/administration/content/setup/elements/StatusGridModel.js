Ext.define('CMDBuildUI.view.administration.content.setup.elements.StatusGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-statusgrid',

    data: {
        services: [],
        servicesStore: null,
        nodes: null,
        servicesStoreReady: false
    },

    formulas: {
        fileListManager: {
            bind: '{servicesToShowStore}',
            get: function (servicesToShowStore) {
                var me = this;
                CMDBuildUI.util.Ajax.setActionId('system.cluster.nodes.services.invoke');
                Ext.Ajax.request({
                    url: Ext.String.format("{0}/system/cluster/nodes/_ALL/invoke", CMDBuildUI.util.Config.baseUrl),
                    method: "POST",
                    jsonData: { "service": "minions", "method": "getAll" }
                }).then(function (response) {
                    if (!me.destroyed) {
                        var allServices = [];
                        var nodes = [];
                        var responseJson = Ext.JSON.decode(response.responseText, true);
                        Ext.Array.forEach(responseJson.data, function (node) {
                            var s = servicesToShowStore;
                            nodes.push(node.cluster_node);
                            Ext.Array.forEach(node.data, function (service) {
                                var _service = Ext.Array.findBy(allServices, function (_service) {
                                    return _service._id == service._id;
                                }) || {};
                                if (_service._id) {
                                    _service[node.cluster_node.nodeId] = service._id;
                                } else {
                                    if (servicesToShowStore.findRecord('_id', service._id)) {
                                        _service._id = service._id;
                                        _service.name = service.name;
                                        _service.status = service.status;
                                        _service.description = service.description;
                                        _service[node.cluster_node.nodeId] = service._id;
                                        _service._can_start = service._can_start;
                                        _service._can_stop = service._can_stop;
                                        _service._is_enabled = service._is_enabled;
                                        allServices.push(_service);
                                    }
                                }
                            });
                        });
                        var servicesStoreFields = ['name'];
                        // normalize all files with all nodes ad keys
                        Ext.Array.forEach(nodes, function (node) {
                            servicesStoreFields.push(node.nodeId);
                        });

                        me.set('services', allServices);
                        me.set('nodes', nodes);
                        me.set('servicesStoreFields', servicesStoreFields);
                        me.set('servicesStoreReady', true);
                    }
                });
            }
        }
    },
    stores: {

        servicesStore: {
            fields: '{servicesStoreFields}',
            proxy: {
                type: 'memory'
            },

            autoDestroy: true,
            pageSize: 0,
            data: '{services}'
        },
        servicesToShowStore: {
            fields: [{
                name: '_id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }, {
                name: 'status',
                type: 'string'
            }, {
                name: '_is_enabled',
                type: 'string'
            }, {
                name: '_can_start',
                type: 'string'
            }],
            proxy: {
                type: 'baseproxy',
                url: '/system_services',
                extraParams: {
                    hidden: false
                }
            },
            autoLoad: true,
            autoDestroy: true
        }
    }

});
