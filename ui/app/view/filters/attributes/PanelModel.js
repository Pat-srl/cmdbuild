Ext.define('CMDBuildUI.view.filters.attributes.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-attributes-panel',

    data: {
        allfields: undefined
    },
    formulas: {
        operatorsdata: function () {
            return CMDBuildUI.util.helper.FiltersHelper.getFilterOperators();
        }
    },

    stores: {
        attributeslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            sorters: ['label'],
            grouper: {
                property: 'group'
            }
        },
        operatorslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{operatorsdata}'
        },
        blockoperators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: [{
                value: CMDBuildUI.util.helper.FiltersHelper.blocksoperators.and,
                label: CMDBuildUI.locales.Locales.filters.operators.and
            }, {
                value: CMDBuildUI.util.helper.FiltersHelper.blocksoperators.or,
                label: CMDBuildUI.locales.Locales.filters.operators.or
            }]
        }
    }

});