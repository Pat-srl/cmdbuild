Ext.define('CMDBuildUI.view.administration.components.filterpanels.functionfilter.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-filterpanels-functionfilters-panel',

    data: {
        _function: null
    },
    formulas: {
        manager: function (get) {
            var filter = this.get('theFilter.configuration');
            if (filter.functions && filter.functions.length) {
                this.set('_function', filter.functions[0].name);
            }
        },
        
        functionFnFilter : function(){
            var me = this;
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/functions',
                method: 'GET',
                params: {
                    filter: Ext.JSON.encode({
                        "Attribute": {
                            "and": [{
                                "simple": {
                                    "attribute": "tags",
                                    "operator": "CONTAIN",
                                    "value": "filterFn"
                                }
                            }]
                        }
                    })
                },
                success: function (response) {                    
                    var datas = JSON.parse(response.responseText).data;
                    var d = [];

                    datas.forEach(function (data) {
                        d.push({
                            label: data.name,
                            value: data.name
                        });
                    });
                    me.set('functionstoredata', d);
                },
                error: function (response) {
                }
            }, this);
            
        }
    },
    stores: {
        getFunctionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{functionstoredata}'
        }
       
    }
});
