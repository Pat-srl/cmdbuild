Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.card.globalparameters.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-dashboard-card-globalparameters-form',

    data: {
        theParameter: null,
        hideGlobalParameterFields: true
    },

    formulas: {
        dataSourceTypes: function () {
            return CMDBuildUI.model.dashboards.Component.getDataSourceTypes();
        },
        integerFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getIntegerFieldTypes();
        },
        stringFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getStringFieldTypes();
        },
        parameterTypes: {
            get: function () {
                return CMDBuildUI.model.dashboards.DataSourceParameter.getTypes();
            }
        },
        panelTitle: {
            bind: {
                description: '{theParameter.description}',
                action: '{action}'
            },
            get: function (data) {
                if (data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                    this.getParent().set(
                        'title',
                        Ext.String.format(
                            '{0} - {1}',
                            CMDBuildUI.locales.Locales.administration.dashboards.parameters.addparameter,
                            data.description
                        )
                    );
                } else if (data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit) {
                    this.getParent().set(
                        'title',
                        Ext.String.format(
                            '{0} - {1}',

                            CMDBuildUI.locales.Locales.administration.dashboards.parameters.editparameter,
                            data.description
                        )
                    );
                } else {
                    this.getParent().set('title', data.description || '');
                }
            }
        }
    },

    stores: {
        dataSourceTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{dataSourceTypes}',
            pageSize: 0,
            proxy: { type: 'memory' }
        },
        integerFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{integerFieldTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },
        stringFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{stringFieldTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },
        integerLookupTypesStore: {
            source: 'lookups.LookupTypes'
        },
        parameterTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{parameterTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        }
    }
});
