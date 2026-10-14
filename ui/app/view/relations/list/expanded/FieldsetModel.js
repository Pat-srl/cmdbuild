Ext.define('CMDBuildUI.view.relations.list.expanded.FieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-expanded-fieldset',

    data: {
        domain: null,
        direction: null,
        storeinfo: {
            autoload: false
        },
        recordsCount: 0
    },

    formulas: {
        /**
         * Get fieldset title merging domain description and records count
         */
        fieldsetTitle: {
            bind: {
                title: '{basetitle}',
                count: '{recordsCount}'
            },
            get: function (data) {
                return Ext.String.format('{0} ({1})', data.title, data.count);
            }
        },

        /**
         * Get collapsed property. 
         * Compare records count with the reference limit specified in configuration
         */
        fieldsetCollapsed: {
            bind: {
                count: '{recordsCount}'
            },
            get: function (data) {
                return data.count > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.relationlimit);
            }
        }
    },

    stores: {
        records: {
            model: '{storeinfo.model}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true,
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            pageSize: 0,
            listeners: {
                load: 'onStoreLoad'
            },
            remoteFilter: true,
            remoteSort: true
        }
    }

});
