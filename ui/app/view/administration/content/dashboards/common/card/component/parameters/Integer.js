Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.Integer', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-integer',
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
                formHelper.getRow([formHelper.getRequiredField()]),
                formHelper.getRow([formHelper.getIntegerFieldTypeField()]),
                formHelper.getRow([formHelper.getIntegerCardFieldContainer()]),
                formHelper.getRow([formHelper.getIntegerFreeFieldContainer()]),
                formHelper.getRow([formHelper.getIntegerLookupFieldContainer()]),
                formHelper.getRow([
                    {
                        xtype: 'container',
                        itemId: 'filtercontainer',
                        columnWidth: 1,
                        items: [formHelper.getParameterFilterTextarea()]
                    }
                ]),
                formHelper.getRow([formHelper.getPreselectIfUnique()])
            ])
        );
    }
});
