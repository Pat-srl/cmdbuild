Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.ParameterModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-card-component-parameters-parameter',
    requires: ['CMDBuildUI.model.dashboards.DataSourceParameter'],

    data: {
        theParameter: null,
        inithiddenfields: {
            card: true,
            integerfree: true,
            stringfree: true,
            lookup: true,
            classes: true,
            cqlfilter: true
        }
    },

    formulas: {
        fieldsetTitle: {
            bind: '{theParameter.name}',
            get: function (name) {
                return Ext.String.format(
                    '{0}: {1}',
                    CMDBuildUI.locales.Locales.administration.dashboards.parameters.parameter,
                    name
                );
            }
        },
        hiddenfields: {
            bind: {
                parameterType: '{theParameter.type}',
                fieldType: '{theParameter.fieldType}'
            },
            get: function (data) {
                var hiddenfields = this.get('inithiddenfields');
                if (
                    Ext.isEmpty(data.fieldType) &&
                    CMDBuildUI.model.dashboards.DataSourceParameter.normalizeType(data.parameterType) ===
                        CMDBuildUI.model.dashboards.DataSourceParameter.types.date
                ) {
                    this.get('theComponent')
                        .dataSourceParameters()
                        .findRecord('_id', this.get('theParameter._id'))
                        .set('fieldType', data.parameterType);
                }
                if (data.parameterType) {
                    switch (CMDBuildUI.model.dashboards.DataSourceParameter.normalizeType(data.parameterType)) {
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.integer:
                            switch (data.fieldType) {
                                case 'card':
                                    hiddenfields.card = false;
                                    hiddenfields.integerfree = true;
                                    hiddenfields.stringfree = true;
                                    hiddenfields.lookup = true;
                                    hiddenfields.cqlfilter = false;
                                    break;
                                case 'lookup':
                                    hiddenfields.card = true;
                                    hiddenfields.integerfree = true;
                                    hiddenfields.stringfree = true;
                                    hiddenfields.lookup = false;
                                    hiddenfields.cqlfilter = false;
                                    break;

                                case 'free':
                                    hiddenfields.card = true;
                                    hiddenfields.integerfree = false;
                                    hiddenfields.stringfree = true;
                                    hiddenfields.lookup = true;
                                    hiddenfields.cqlfilter = true;
                                    break;

                                default:
                                    hiddenfields.card = true;
                                    hiddenfields.integerfree = true;
                                    hiddenfields.stringfree = true;
                                    hiddenfields.lookup = true;
                                    hiddenfields.cqlfilter = true;
                                    break;
                            }
                            break;
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.string:
                            switch (data.fieldType) {
                                case 'classes':
                                    hiddenfields.classes = false;
                                    hiddenfields.stringfree = true;
                                    break;

                                case 'free':
                                    hiddenfields.classes = true;
                                    hiddenfields.stringfree = false;
                                    break;

                                default:
                                    hiddenfields.classes = true;
                                    hiddenfields.stringfree = true;
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                }
                return hiddenfields;
            }
        }
    }
});
