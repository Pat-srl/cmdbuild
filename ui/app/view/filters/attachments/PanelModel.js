Ext.define('CMDBuildUI.view.filters.attachments.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-attachments-panel',

    data: {
        attachments: {
            searchtext: null,
            operator_label: false
        },
        metadatavalues: [],
        catetoriesvalues: [],
        displayOnly: false
    },

    formulas: {
        operatorsdata: function () {
            return CMDBuildUI.util.helper.FiltersHelper.getFilterOperators();
        },
        updateFromDefault: {
            bind: {
                filter: '{theFilter}'
            },
            get: function (data) {
                var config = data.filter.get("configuration");
                if (config.attachment && !Ext.isEmpty(config.attachment.query)) {
                    this.set("attachments.searchtext", config.attachment.query)
                }
            }
        },
        visibletextfield: {
            get: function () {
                var view = this.getView();
                return !view.getIsDms();
            }
        }
    },

    stores: {
        metadatalist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{metadatavalues}',
            grouper: {
                property: 'category_description'
            }
        },

        dmscategories: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{catetoriesvalues}'
        },

        operatorslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{operatorsdata}'
        }
    }

});