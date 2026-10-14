Ext.define('CMDBuildUI.view.administration.components.keyvaluegrid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-keyvaluegrid-grid',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: '',
        actions: {
            edit: false,
            view: true
        }
    },

    formulas: {

        updateStoreVariables: {
            bind: {
                theKeyvaluedata: '{theKeyvaluedata}'
            },
            get: function (data) {
                var resList = [];
                var templatedataArray = Object.entries(data.theKeyvaluedata);
                templatedataArray.forEach(function (template) {
                    resList.push({
                        key: template[0],
                        value: template[1]
                    });
                });
                return resList;
            }
        }
    },

    stores: {
        keyvaluedataStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{updateStoreVariables}'
        }
    }
});