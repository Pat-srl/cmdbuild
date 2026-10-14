Ext.define('CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-spaceusage-tablesgrid',
    data: {
        data: [],        
        typeFilterValue: '',
        dataFilter: []
    },

    formulas: {
        typeFilterData: {
            get: function(){
                return [{
                    value: 'class',
                    label: CMDBuildUI.locales.Locales.administration.home['class']
                }, {
                    value: 'map',
                    label: CMDBuildUI.locales.Locales.administration.home.map
                }, {
                    value: 'simpleclass',
                    label: CMDBuildUI.locales.Locales.administration.home.simpleclass
                }, {
                    value: 'system',
                    label: CMDBuildUI.locales.Locales.administration.home.system
                }, {
                    value: 'other',
                    label: CMDBuildUI.locales.Locales.administration.home.other
                }];
            }
        },
        initData: function (get) {
            var me = this;
            me.set('showLoader', true);            
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/functions/_cm3_dashboard_disk_usage/outputs',
                method: "GET",
                timeout: 0
            }).then(function (response, opts) {
                var responseJson = Ext.JSON.decode(response.responseText, true);
                var dataMap = {};

                Ext.Array.forEach(responseJson.data, function (element) {
                    var record = dataMap[element.item] || {};
                    var key;
                    switch (element.status) {
                        case 'A':
                            key = 'active';
                            break;
                        case 'N':
                            key = 'deleted';
                            break;
                        case 'U':
                            key = 'updated';
                            break;
                        default:
                            break;
                    }
                    if (key) {
                        if (!record.table) {
                            record.table = element.item;
                        }
                        var description;
                        try {
                            if (CMDBuildUI.locales.Locales.administration.home[element.type]) {
                                description = CMDBuildUI.locales.Locales.administration.home[element.type];
                            }
                        } catch (error) {
                            description = element.type;
                        }
                        record[Ext.String.format('{0}_size', key)] = element.size;
                        record[key] = element.count;
                        record.type = element.type;
                        record._type_description = description;
                    }
                    dataMap[element.item] = record;
                });
                if(!me.destroyed){
                    me.set('data', Ext.Object.getValues(dataMap));
                    me.set('showLoader', false);
                }               
                    
            }, function () {
                if(!me.destroyed){
                    me.set('showLoader', false);
                }
            });
        },
        dataFilter: {
            bind: {
                typeFilterValue: '{typeFilterValue}'
            },
            get: function (data) {
                var store = this.getStore('tablesStats');
                if (store) {
                    store.getFilters().removeAll();
                    if (!Ext.isEmpty(data.typeFilterValue)) {
                        store.applyFilters([function (item) {
                            return item.get('type') === data.typeFilterValue;
                        }]);
                    }
                }
            }
        }
    },

    stores: {
        typeFilterStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{typeFilterData}'
        },
        
        tablesStats: {
            fields: [{
                type: 'string',
                name: 'table'
            }, {
                type: 'string',
                name: 'type'
            }, {
                type: 'string',
                name: '_type_description'
            }, {
                type: 'integer',
                name: 'active'
            }, {
                type: 'integer',
                name: 'active_size'
            }, {
                type: 'integer',
                name: 'updated'
            }, {
                type: 'integer',
                name: 'updated_size'
            }, {
                type: 'integer',
                name: 'deleted'
            }, {
                type: 'integer',
                name: 'deleted_size'
            }, {
                type: 'integer',
                name: 'total',
                calculate: function (data) {
                    return data.active + data.updated + data.deleted;
                }
            }, {
                type: 'integer',
                name: 'total_size',
                calculate: function (data) {
                    return data.active_size + data.updated_size + data.deleted_size;
                }
            }],            
            data: '{data}'
        },

        tablesStatsLight: {
            source: '{tablesStats}',
            pageSize: 10,
            sorters: ['total_size DESC']
        }
    }
});