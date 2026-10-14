Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Integer', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-attribute-integerfields',

    items: [
        {
            xtype: 'container',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            itemId: 'unitOfMeasureFieldInteger',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                            },
                            name: 'unitOfMeasure',
                            maxLength: 10,
                            bind: {
                                value: '{theAttribute.unitOfMeasure}'
                            },
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    const form = this.up('form');
                                    const precisionAttributeLocationField = form.down(
                                        '#unitOfMeasureLocationFieldInteger'
                                    );
                                    if (!newValue) {
                                        precisionAttributeLocationField.setValue(null);
                                    }
                                    precisionAttributeLocationField.validate();
                                }
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            itemId: 'unitOfMeasureLocationFieldInteger',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation'
                            },
                            name: 'unitOfMeasureLocationInteger',
                            clearFilterOnBlur: false,
                            anyMatch: true,
                            autoSelect: true,
                            forceSelection: true,
                            typeAhead: true,
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            hidden: true,
                            bind: {
                                store: '{unitOfMeasuresStore}',
                                value: '{theAttribute.unitOfMeasureLocation}',
                                hidden: '{!theAttribute.unitOfMeasure}'
                            },
                            validator: function (field) {
                                const form = this.up('form');
                                const precisionAttributeField = form.down('#unitOfMeasureFieldInteger');
                                if (precisionAttributeField.getValue() && !this.getValue()) {
                                    return CMDBuildUI.locales.Locales.administration.attributes.strings
                                        .positioningofumrequired;
                                }
                                return true;
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showthousandsseparator,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showthousandsseparator'
                            },
                            itemId: 'attribute-showseparator',
                            name: 'showSeparator',
                            bind: {
                                value: '{theAttribute.showThousandsSeparator}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                            },
                            bind: {
                                value: '{theAttribute.unitOfMeasure}'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation'
                            },
                            hidden: true,
                            bind: {
                                value: '{theAttribute.unitOfMeasureLocation}',
                                hidden: '{!theAttribute.unitOfMeasure}'
                            },
                            renderer: function (value) {
                                if (value) {
                                    const store = Ext.getStore('attributes.UnifOfMeasureLocations');
                                    if (store) {
                                        return store.findRecord('value', value).get('label');
                                    }
                                }
                                return value;
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showthousandsseparator,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showthousandsseparator'
                            },
                            itemId: 'attribute-showseparator',
                            name: 'showSeparator',
                            disabled: true,
                            bind: {
                                value: '{theAttribute.showThousandsSeparator}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
