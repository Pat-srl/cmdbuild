Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.SpecificProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-typeproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            layout: 'column',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                    'subtype',
                    {
                        subtype: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.subtype, // the localized object for label of field
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.subtype'
                                }
                            }, // config for fieldcontainer
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                store: '{subtypesStore}',
                                value: '{theGeoAttribute.subtype}'
                            }
                        }
                    },
                    true, // disabledOnEdit
                    false
                ) // onlyCombo
            ]
        },
        {
            xtype: 'panel',
            bind: {
                hidden: '{!type.isLine}'
            },
            items: [CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getLineInputs()]
        },
        {
            xtype: 'panel',
            bind: {
                hidden: '{!type.isPoint}'
            },
            items: [CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getPointInputs()]
        },
        {
            xtype: 'panel',
            bind: {
                hidden: '{!type.isPolygon}'
            },
            items: [CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getPolygonInputs()]
        }
    ]
});
