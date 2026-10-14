Ext.define('CMDBuildUI.view.administration.content.setup.elements.DownloadLogController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-downloadlog',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {

        var vm = view.getViewModel();

        vm.bind({
            bindTo: {
                logFilesStoreReady: '{logFilesStoreReady}',
                nodes: '{nodes}'
            }
        }, function (data) {

            if (data.logFilesStoreReady) {
                var columns = [{
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.filename,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.filename'
                    },
                    dataIndex: 'file',
                    sortable: false
                }];

                Ext.Array.forEach(data.nodes, function (node) {
                    columns.push({
                        xtype: 'widgetcolumn',
                        text: node.nodeId,
                        sortable: false,
                        widget: {
                            xtype: 'container',
                            items: [{
                                xtype: 'button',
                                text: CMDBuildUI.locales.Locales.administration.common.actions.download,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.download'
                                },
                                nodeId: node.nodeId,
                                ui: 'administration-secondary-action-small',
                                listeners: {
                                    click: 'onDownloadFileBtnClick'
                                }
                            }]
                        }


                    });
                });

                view.reconfigure(vm.getStore('logFilesStore'), columns);
            }
        });
    },

    onDownloadFileBtnClick: function (button, event, eOpts) {
        button.mask('', 'button-mask');
        var record = button.up().$widgetRecord;
        var node = button.nodeId;
        var id = record.get(node);
        var jsonData, extension;
        if (id === 'allfiles') {
            jsonData = {
                "service": "system",
                "method": "downloadAllLogFiles"
            };
            extension = 'zip';
        } else {
            jsonData = {
                "service": "system",
                "method": "downloadLogFile",
                "params": {
                    "fileName": id
                }
            };
            extension = 'log';
        }
        Ext.Ajax.request({
            url: Ext.String.format("{0}/system/cluster/nodes/{1}/invoke", CMDBuildUI.util.Config.baseUrl, CMDBuildUI.util.Utilities.stringToHex(node)),
            method: "POST",
            jsonData: jsonData
        }).then(function (response) {
            if (!button.destroyed) {
                var responseJson = Ext.JSON.decode(response.responseText, true);
                var url = Ext.String.format('{0}/downloads/{1}/download', CMDBuildUI.util.Config.baseUrl, responseJson._response.downloadId);
                CMDBuildUI.util.administration.File.download(url, extension, true).then(function () {
                    Ext.asap(function () {
                        if (!button.destroyed) {
                            button.unmask();
                        }
                    });
                });
            }
        });
    }
});