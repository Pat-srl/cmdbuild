Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bus-messages-viewinrow-viewinrow',
    data: {

    },
    formulas: {
        meta: {
            bind: '{theMessage.meta}',
            get: function (meta) {
                var result = [];
                for (var key in meta) {
                    if (meta.hasOwnProperty(key)) {
                        result.push({
                            key: key,
                            value: meta[key]
                        });
                    }
                }
                return result;
            }
        }
    },
    stores: {
        metaStore: {
            proxy: 'memory',
            model: 'CMDBuildUI.model.base.KeyValue',
            data: '{meta}',
            sorters: ['key']
        },
        attachmentsStore: {
            proxy: 'memory',
            fields: ["name", "storage", "type", "_byteSize", "_contentType"],
            data: '{theMessage.attachments}'
        },
        historyStore: {
            proxy: 'memory',
            model: 'CMDBuildUI.model.administration.BusLog',
            data: '{theMessage._historyRecords}',
            sorters: ['timestamp']
        }
    }

});