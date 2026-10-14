Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-jobruns-viewinrow-viewinrow',
    data: {

    },
    formulas: {       
        meta: {
            bind: '{theJobrun.meta}',
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
        errorsStore: {
            proxy: 'memory',
            fields: ["level", "message", "exception"],
            data: '{theJobrun.errors}'
        }
    }

});