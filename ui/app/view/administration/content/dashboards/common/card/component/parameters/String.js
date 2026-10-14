Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.String', {
    extend: 'Ext.panel.Panel',
    viewModel: {},
    alias: 'widget.administration-dashboards-parameters-string',
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
                formHelper.getRow([formHelper.getStringFieldTypeField()]),
                formHelper.getRow([formHelper.getAllClassesFieldContainer()], {
                    bind: {
                        hidden: '{hiddenfields.classes}'
                    }
                }),
                formHelper.getRow([formHelper.getStringFreeFieldContainer()], {
                    bind: {
                        hidden: '{hiddenfields.stringfree}'
                    }
                })
            ])
        );
    }
});
