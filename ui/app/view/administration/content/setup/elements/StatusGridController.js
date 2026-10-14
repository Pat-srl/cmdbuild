Ext.define('CMDBuildUI.view.administration.content.setup.elements.StatusGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-statusgrid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'tableview': {
            servicestart: 'onServiceStart',
            servicestop: 'onServiceStop'
        }
    },

    /**
     *
     * @param {administration-content-setup-elements-statusgrid} view
     */
    onBeforeRender: function (view) {

        var vm = view.getViewModel();

        vm.bind({
            bindTo: {
                nodes: '{nodes}',
                servicesStoreReady: '{servicesStoreReady}',
                servicesStore: '{servicesStore}'
            }
        }, function (data) {

            if (data.servicesStoreReady) {
                var columns = [{
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.service,
                    localized: {
                        text: "CMDBuildUI.locales.Locales.administration.systemconfig.service"
                    },
                    dataIndex: 'description',
                    sortable: false
                }];
                Ext.Array.forEach(data.nodes, function (node) {
                    columns.push({
                        text: node.nodeId,
                        sortable: false,
                        dataIndex: 'status',
                        renderer: function (value) {
                            var output = '<span';
                            if (CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statuscolors[value]) {
                                output += ' style="color:' +
                                    CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statuscolors[value] +
                                    ';"';
                            }
                            output += '>' +
                                // icon
                                '<span class="' +
                                CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statusicons[value] +
                                '"></span> ' +
                                // lable
                                CMDBuildUI.locales.Locales.administration.systemconfig['status' + value] +
                                '</span>';
                            return output;
                        }


                    });
                });

                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 75,
                    maxWidth: 75,
                    hideable: false,
                    sortable: false,
                    align: 'center',
                    items: [{
                        // start service
                        autoEl: {
                            'data-testid': 'administration-content-setup-elements-servermanagement-servicestart'
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.systemconfig.servicestart;
                        },
                        handler: function (grid, rowIndex, colIndex, tool, event, record) {
                            grid.fireEvent("servicestart", grid, record);
                        },
                        getClass: function (v, meta, record) {
                            if (record.get("_can_start")) {
                                return 'tasks-grid-action-play disabled ' + CMDBuildUI.util.helper.IconHelper.getIconId('play-circle', 'regular');
                            }
                            return 'tasks-grid-action-play enabled ' + CMDBuildUI.util.helper.IconHelper.getIconId('play-circle', 'regular');
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                            return !record.get("_can_start");
                        }
                    }, '->', {
                        // stop service
                        autoEl: {
                            'data-testid': 'administration-content-setup-elements-servermanagement-servicestop'
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.systemconfig.servicestop;
                        },
                        handler: function (grid, rowIndex, colIndex, tool, event, record) {
                            grid.fireEvent("servicestop", grid, record);
                        },
                        getClass: function (v, meta, record) {
                            if (record.get('_can_stop')) {
                                return 'tasks-grid-action-stop disabled ' + CMDBuildUI.util.helper.IconHelper.getIconId('stop-circle', 'solid');
                            }
                            return 'tasks-grid-action-stop enabled ' + CMDBuildUI.util.helper.IconHelper.getIconId('stop-circle', 'solid');
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                            return !record.get("_can_stop");
                        }
                    }]
                });

                view.reconfigure(vm.getStore('servicesStore'), columns);
            }
        });
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} service
     */
    onServiceStart: function (grid, service) {
        var me = this;
        var url = CMDBuildUI.util.Config.baseUrl +
            '/system_services/' +
            service.get("_id") +
            '/start';
        var fieldset = grid.up('fieldset');
        var mask = CMDBuildUI.util.Utilities.addLoadMask(fieldset);
        // make request
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            success: function () {
                if (!me.destroyed) {
                    me.refreshGrid(mask);
                }
            },
            failure: function () {
                if (!me.destroyed) {
                    me.refreshGrid(mask);
                }
            }
        });
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} service
     */
    onServiceStop: function (grid, service) {
        var me = this;
        var url = CMDBuildUI.util.Config.baseUrl +
            '/system_services/' +
            service.get("_id") +
            '/stop';
        var fieldset = grid.up('fieldset');
        var mask = CMDBuildUI.util.Utilities.addLoadMask(fieldset);
        // make request
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            success: function () {
                if (!me.destroyed) {
                    me.refreshGrid(mask);
                }
            },
            failure: function () {
                if (!me.destroyed) {
                    me.refreshGrid(mask);
                }
            }
        });
    },
    privates: {
        refreshGrid: function (mask) {
            var fieldset = this.getView().up('fieldset');
            Ext.suspendLayouts();
            fieldset.removeAll();
            fieldset.add({
                xtype: 'administration-content-setup-elements-statusgrid'
            });

            Ext.resumeLayouts();
            CMDBuildUI.util.Utilities.removeLoadMask(mask);

        }
    }
});