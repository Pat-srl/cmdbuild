Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.InfoWindow', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-infowindow',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            layout: 'column',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('infoWindowEnabled', {
                    infoWindowEnabled: {
                        fieldcontainer: {
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.infowindowenabled,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.infowindowenabled'
                            }
                        },
                        bind: {
                            disabled: '{actions.view}',
                            value: '{theGeoAttribute.infoWindowEnabled}'
                        }
                    }
                })
            ]
        },
        {
            hidden: true,
            bind: {
                hidden: '{!theGeoAttribute.infoWindowEnabled}'
            },
            layout: 'column',
            items: [
                {
                    columnWidth: 0.5,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('infoWindowImage', {
                            infoWindowImage: {
                                columnWidth: 1,
                                fieldcontainer: {
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels
                                            .infowindowimage, // the localized object for label of field
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.infowindowimage'
                                    }
                                }, // config for fieldcontainer
                                // userCls: 'with-tool',
                                displayField: 'label',
                                valueField: 'value',
                                bind: {
                                    store: '{linkAttributesStore}',
                                    value: '{theGeoAttribute.infoWindowImage}'
                                }
                            }
                        })
                    ]
                }
            ]
        },
        {
            hidden: true,
            bind: {
                hidden: '{!theGeoAttribute.infoWindowEnabled}'
            },
            layout: 'column',
            items: [
                {
                    columnWidth: 1,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('infoWindowContent', {
                            infoWindowContent: {
                                xtype: 'textarea',
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels
                                        .infowindowcontent, // the localized object for label of field
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.infowindowcontent'
                                },
                                emptyText: '<strong>{attrLabel:AttrName}:</strong> {card:Description}',
                                resizable: {
                                    handles: 's'
                                },
                                name: 'filter',
                                bind: {
                                    readOnly: '{actions.view}',
                                    value: '{theGeoAttribute.infoWindowContent}'
                                }
                            }
                        })
                    ]
                }
            ]
        }
    ]
});
