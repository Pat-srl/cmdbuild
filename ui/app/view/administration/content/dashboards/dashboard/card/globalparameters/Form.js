Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.card.globalparameters.Form', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-dashboards-dashboard-card-globalparameters-form',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.dashboard.card.globalparameters.FormController',
        'CMDBuildUI.view.administration.content.dashboards.dashboard.card.globalparameters.FormModel'
    ],
    layout: 'column',

    controller: 'administration-content-dashboards-dashboard-card-globalparameters-form',
    viewModel: {
        type: 'administration-content-dashboards-dashboard-card-globalparameters-form'
    },
    scrollable: 'y',
    bodyPadding: 10,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [
        {
            xtype: 'combobox',
            itemId: 'parameterTypeCombo',
            padding: '0 0 0 10',
            columnWidth: 0.5,
            fieldLabel: 'Type',
            valueField: 'value',
            displayField: 'label',
            bind: {
                store: '{parameterTypesStore}',
                value: '{theParameter.type}',
                disabled: '{actions.view}'
            }
        },
        {
            xtype: 'container',
            itemId: 'parametersContainer',
            columnWidth: 1,
            items: []
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            itemId: 'bottomtoolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
        }
    ]
});
