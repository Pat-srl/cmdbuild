Ext.define('CMDBuildUI.view.administration.content.views.card.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',
    requires: ['CMDBuildUI.view.administration.content.views.card.FieldsHelper'],
    alias: 'widget.administration-content-views-card-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    viewModel: {},
    layout: 'column',
    items: [
        {
            columnWidth: 0.5,
            items: [
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getNameInput(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getDescriptionInput(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getFunctionsInput(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getAllClassesInput(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getFiltersTools(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getIconInput(),
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getActiveInput()
            ]
        }
    ]
});
