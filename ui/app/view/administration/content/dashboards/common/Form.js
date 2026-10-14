Ext.define('CMDBuildUI.view.administration.content.dashboards.common.Form', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-dashboards-common-form',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.FieldsetHelper',
        'CMDBuildUI.view.administration.content.dashboards.common.FormController',
        'CMDBuildUI.view.administration.content.dashboards.common.FormModel',
        'CMDBuildUI.view.administration.content.dashboards.common.FormHelper'
    ],

    controller: 'administration-content-dashboards-common-form',
    viewModel: {
        type: 'administration-content-dashboards-common-form'
    },
    scrollable: 'y',
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    initComponent: function () {
        var fieldsetHelper = CMDBuildUI.view.administration.content.dashboards.common.FieldsetHelper;
        var formHelper = CMDBuildUI.util.administration.helper.FormHelper;

        this.items = [
            fieldsetHelper.getGeneralPropertiesFieldset(),
            fieldsetHelper.getDataSourcePropertiesFieldset(),
            fieldsetHelper.getChartAndTextPropertiesFieldset(),
            fieldsetHelper.getListPropertiesFieldset(),
            fieldsetHelper.getTilePropertiesFieldset(),
            fieldsetHelper.getCalendarPropertiesFieldset(),
            fieldsetHelper.getSearchPropertiesFieldset()
        ];

        this.dockedItems = [
            {
                dock: 'top',
                xtype: 'container',
                bind: {},
                items: [
                    {
                        xtype: 'components-administration-toolbars-formtoolbar',
                        hidden: true,
                        bind: { hidden: '{hideToolbar}' },
                        items: formHelper.getTools(
                            { edit: true, activeToggle: true, delete: true, clone: true },
                            'dashboard-component',
                            'theComponent'
                        )
                    }
                ]
            },
            {
                xtype: 'toolbar',
                itemId: 'bottomtoolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: true,
                bind: { hidden: '{actions.view}' },
                items: formHelper.getSaveCancelButtons(
                    true,
                    { testid: 'dashboard-component' },
                    { testid: 'dashboard-component' }
                )
            }
        ];

        this.callParent(arguments);
    }
});
