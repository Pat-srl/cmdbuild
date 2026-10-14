Ext.define('CMDBuildUI.view.dashboards.common.components.list.ListModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dashboards-components-list-list',

    data: {
        storedata: {},
        seeAllElements: false,
        listMaxHeight: null,
        emptyTextFormatted: ''
    },

    formulas: {
        isShowAllButtonHidden: {
            bind: {
                seeAllElements: '{seeAllElements}'
            },
            get: function (data) {
                return !data.seeAllElements;
            }
        },

        applyFilterManager: {
            bind: { bindTo: { records: '{records}', filter: '{storedata.filter}' } },
            get: function (data) {
                if (data.records && data.filter) {
                    data.records.getAdvancedFilter().addCustomFilter(data.filter);
                }
            }
        }
    },

    stores: {
        records: {
            type: '{storedata.type}',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: '{storedata.extraparams}'
            },
            pageSize: '{storedata.pagesize}',
            autoLoad: '{storedata.autoload}',
            sorters: '{storedata.sorters}',
            autoDestroy: true
        }
    }
});
