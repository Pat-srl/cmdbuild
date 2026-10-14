
Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.type.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-type-fieldsets-generaldatafieldset',

    viewModel: {},
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                    name: {
                        vtype: "lookupTypeNameInputValidation",
                        allowBlank: false,
                        bind: {
                            value: '{theDMSCategoryType.name}'
                        }
                    }
                })                
            ]
        }]
    }]
});
