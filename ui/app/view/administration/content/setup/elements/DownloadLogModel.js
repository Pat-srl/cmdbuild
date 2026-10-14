Ext.define('CMDBuildUI.view.administration.content.setup.elements.DownloadLogModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-downloadlog',
    data: {
        files: [],
        logFilesStore: null,
        nodes: null,
        logFilesStoreReady: false
    },

    formulas: {
        fileListManager: {
            bind: {

            },
            get: function () {
                var me = this;
                CMDBuildUI.util.Ajax.setActionId('system.cluster.nodes.invoke');
                Ext.Ajax.request({
                    url: Ext.String.format("{0}/system/cluster/nodes/_ALL/invoke", CMDBuildUI.util.Config.baseUrl),
                    method: "POST",
                    jsonData: { "service": "system", "method": "getAllLogFiles" }
                }).then(function (response) {
                    if (!me.destroyed) {
                        var allFiles = [];
                        var nodes = [];
                        var responseJson = Ext.JSON.decode(response.responseText, true);
                        Ext.Array.forEach(responseJson.data, function (node) {
                            nodes.push(node.cluster_node);
                            Ext.Array.forEach(node.data, function (file) {
                                var _file = Ext.Array.findBy(allFiles, function (_file) {
                                    return _file.file == file.file;
                                }) || {};
                                if (_file.file) {
                                    _file[node.cluster_node.nodeId] = file._id;
                                } else {
                                    _file.file = file.file;
                                    _file[node.cluster_node.nodeId] = file._id;
                                    allFiles.push(_file);
                                }
    
                            });
    
                        });
                        var logFileStoreFields = ['file'];
                        // normalize all files with all nodes ad keys
                        var allFilesDownload = {};
                        allFilesDownload.file = 'All files';
                        Ext.Array.forEach(nodes, function (node) {
                            Ext.Array.forEach(allFiles, function (file) {
                                logFileStoreFields.push(node.nodeId);
                                if (typeof file[node.nodeId] === 'undefined') {
                                    file[node.nodeId] = null;
                                }
                            });
                            allFilesDownload[node.nodeId] = 'allfiles';
                        });
                        allFiles.push(allFilesDownload);
    
                        // TODO: set all files download
                        me.set('files', allFiles);
                        me.set('nodes', nodes);
                        me.set('logFileStoreFields', logFileStoreFields);
                        me.set('logFilesStoreReady', true);
                    }
                });
            }
        }
    },
    stores: {

        logFilesStore: {
            fields: '{logFileStoreFields}',
            proxy: {
                type: 'memory'
            },
            
            autoDestroy: true,
            pageSize: 0,
            data: '{files}'
        }
    }

});
