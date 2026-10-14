Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-lookuptypes-tabitems-type-fieldsets-generaldatafieldset',

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
                            value: '{theLookupType.name}'
                        }
                    }
                }),
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('parentField', {
                    parentField: {
                        fieldcontainer: {}, // config for fieldcontainer
                        fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label'
                        },
                        displayField: 'name',
                        valueField: 'name',
                        bind: {
                            store: '{allLookupsStore}',
                            value: '{theLookupType.parent}'
                        },
                        triggers: {
                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                        }
                    }
                })
            ]
        }]
    }]
});