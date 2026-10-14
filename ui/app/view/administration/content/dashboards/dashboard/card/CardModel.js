Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-dashboards-card',
    requires: ['CMDBuildUI.model.dashboards.Component', 'CMDBuildUI.model.dashboards.DataSourceParameter'],
    data: {
        hideForm: false,
        rowsData: [],
        componentsData: [],
        globalParametersData: []
    },

    formulas: {
        rowsDataManager: {
            get: function () {
                return [];
            }
        },
        parameterTypes: {
            get: function () {
                return CMDBuildUI.model.dashboards.DataSourceParameter.getTypes();
            }
        },
        fieldsTypes: {
            get: function () {
                var fieldsTypes = [];
                var allFieldsTypes = CMDBuildUI.model.dashboards.DataSourceParameter.getIntegerFieldTypes().concat(
                    CMDBuildUI.model.dashboards.DataSourceParameter.getStringFieldTypes(),
                    [
                        // Date and boolean show field type as default
                        {
                            label: CMDBuildUI.locales.Locales.administration.common.labels.default,
                            value: CMDBuildUI.model.dashboards.DataSourceParameter.types.boolean
                        },
                        {
                            label: CMDBuildUI.locales.Locales.administration.common.labels.default,
                            value: CMDBuildUI.model.dashboards.DataSourceParameter.types.date
                        }
                    ]
                );

                Ext.Array.each(allFieldsTypes, function (fieldType) {
                    if (
                        !Ext.Array.findBy(fieldsTypes, function (item) {
                            return item.value === fieldType.value;
                        })
                    ) {
                        fieldsTypes.push(fieldType);
                    }
                });

                return fieldsTypes;
            }
        }
    },

    stores: {
        // get all components types
        componentsStore: {
            proxy: {
                type: 'memory'
            },
            data: CMDBuildUI.model.dashboards.Component.getComponentTypes(null, [
                CMDBuildUI.model.dashboards.Component.componentType.search
            ])
        },
        rows: {
            proxy: {
                type: 'memory'
            },
            data: '{rowsData}',
            listeners: {
                datachanged: 'onRowsStoreDatachanged'
            }
        },

        components: {
            proxy: {
                type: 'memory'
            },
            data: '{componentsData}',
            listeners: { datachanged: 'onRowsStoreDatachanged' }
        },

        globalParametersStore: {
            model: 'CMDBuildUI.model.dashboards.DataSourceParameter',
            proxy: {
                type: 'memory'
            },
            data: '{globalParametersData}',
            sorters: ['label']
        },
        parameterTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{parameterTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },
        fieldsTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{fieldsTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        }
    }
});
