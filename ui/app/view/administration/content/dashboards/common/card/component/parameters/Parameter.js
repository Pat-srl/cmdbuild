Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.Parameter', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.ParameterController',
        'CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.ParameterModel',
        'CMDBuildUI.model.dashboards.DataSourceParameter'
    ],

    controller: 'administration-content-dashboards-card-component-parameters-parameter',
    viewModel: {
        type: 'administration-content-dashboards-card-component-parameters-parameter'
    },
    alias: 'widget.administration-dashboards-common-parameters-parameter',
    config: {
        parameter: null,
        hideGlobalParameterFields: false
    },
    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            bind: {
                title: '{fieldsetTitle}'
            },
            collapsible: false,
            items: []
        }
    ],
    initComponent: function () {
        this.callParent(arguments);
        var view = this,
            vm = view.getViewModel(),
            theParameter = vm.get('theParameter'),
            fieldset = view.down('fieldset');

        vm.set('hideGlobalParameterFields', view.getHideGlobalParameterFields());

        vm.bind(
            {
                bindTo: {
                    theParameterType: '{theParameter.type}'
                }
            },
            function (data) {
                fieldset.removeAll();
                if (data.theParameterType) {
                    switch (CMDBuildUI.model.dashboards.DataSourceParameter.normalizeType(theParameter.get('type'))) {
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.integer:
                            fieldset.add({
                                xtype: 'administration-dashboards-parameters-integer'
                            });
                            break;
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.string:
                            fieldset.add({
                                xtype: 'administration-dashboards-parameters-string'
                            });
                            break;
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.date:
                            fieldset.add({
                                xtype: 'administration-dashboards-parameters-date'
                            });
                            break;
                        case CMDBuildUI.model.dashboards.DataSourceParameter.types.boolean:
                            fieldset.add({
                                xtype: 'administration-dashboards-parameters-boolean'
                            });
                            break;
                        default:
                            break;
                    }
                }
            }
        );
    }
});
