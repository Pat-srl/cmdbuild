Ext.define('CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecordsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-activerecords-activerecords',
    data: {
        data: [],
        locked: 0
    },
    formulas: {

        initData: function (get) {
            var me = this;
            me.getStatsData();
            me.getLockedCounter();
        },
        seriesTitles: function () {
            return {
                cards: CMDBuildUI.locales.Locales.administration.home.cards,
                precessinstances: CMDBuildUI.locales.Locales.administration.home.precessinstances,
                attachments: CMDBuildUI.locales.Locales.administration.home.attachments,
                relations: CMDBuildUI.locales.Locales.administration.home.relations
            };
        },
        lockedItemsMessage: {
            bind: '{locked}',
            get: function (locked) {
                return Ext.String.format(CMDBuildUI.locales.Locales.administration.home.therearenlockeditems, locked);
            }
        }
    },
    stores: {
        dataStats: {
            proxy: 'memory',
            sorters: ['index'],
            fields: [{
                type: 'string',
                name: 'year'
            }, {
                type: 'integer',
                name: 'cards'
            }, {
                type: 'integer',
                name: 'procInstances'
            }, {
                type: 'integer',
                name: 'attachments'
            }, {
                type: 'integer',
                name: 'relations'
            }],
            data: '{data}'
        }
    },
    getStatsData: function () {
        var me = this;
        me.set('showLoader', true);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/functions/_cm3_dashboard_records_history/outputs',
            method: "GET",
            timeout: 0
        }).then(function (response, opts) {
            if (!me.destroyed) {
                var responseJson = Ext.JSON.decode(response.responseText, true);
                var store = me.getStore('dataStats');
                store.beginUpdate();
                Ext.Array.forEach(responseJson.data, function (element) {
                    if (store) {
                        var record = store.findRecord('year', element.date);
                        var key;
                        switch (element.type) {
                            case 'card':
                                key = 'cards';
                                break;
                            case 'document':
                                key = 'attachments';
                                break;
                            case 'process':
                                key = 'procInstances';
                                break;
                            case 'process':
                                key = 'procInstances';
                                break;
                            case 'relation':
                                key = 'relations';
                                break;
                            default:
                                break;
                        }
                        if (key) {
                            if (record) {
                                record.set(key, element.count);
                            } else {
                                var newRecord = {};
                                newRecord.year = element.date;
                                newRecord[key] = element.count;
                                store.add(newRecord);
                            }
                        }
                    }
                });
                store.endUpdate();
                me.set('showLoader', false);
            }

        }, function () {
            if (!me.destroyed) {
                me.set('showLoader', false);
            }
        });
    },
    getLockedCounter: function () {
        var me = this;
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/locks',
            method: "GET"
        }).then(function (response, opts) {
            if (!me.destroyed) {
                var responseJson = Ext.JSON.decode(response.responseText, true);
                me.set('locked', responseJson.meta.total);
            }

        }, function () {

        });
    }

});