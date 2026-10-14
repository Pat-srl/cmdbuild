Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.Boolean', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-boolean',
    layout: 'column',
    columnWidth: 1,
    items: [],
    initComponent: function () {
        var me = this,
            formHelper = CMDBuildUI.view.administration.content.dashboards.common.FormHelper;
        me.callParent(arguments);
        me.add(formHelper.getRow([formHelper.getGlobalParameterField()]));
        me.add(
            formHelper.getLocalParameterFieldsContainer([
                formHelper.getRow([formHelper.getParameterDescriptionField(), formHelper.getParameterEmptyTextField()]),
                formHelper.getRow([formHelper.getParameterDefaultValueThreestateCheckbox()]),
                formHelper.getRow([formHelper.getRequiredField()])
            ])
        );
    }
});
