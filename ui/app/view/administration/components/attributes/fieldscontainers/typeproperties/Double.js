Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Double', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-doublefields',

    items: [
        {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!actions.add}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            itemId: 'unitOfMeasureFieldDouble',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                            },
                            name: 'precision',
                            maxLength: 10,
                            bind: {
                                value: '{theAttribute.unitOfMeasure}',
                                disabled: '{theAttribute.inherited}'
                            },
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    const form = this.up('form');
                                    const precisionAttributeLocationField = form.down(
                                        '#unitOfMeasureLocationFieldDouble'
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
                            itemId: 'unitOfMeasureLocationFieldDouble',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation'
                            },
                            name: 'unitOfMeasureLocationDouble',
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
                                const precisionAttributeField = form.down('#unitOfMeasureFieldDouble');
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
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'numberfield',
                            itemId: 'scaleAttributeField',
                            minValue: 0,
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals'
                            },
                            name: 'visibleDecimals',
                            step: 1,
                            bind: {
                                value: '{theAttribute.visibleDecimals}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!actions.edit}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            itemId: 'unitOfMeasureFieldDouble',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                            },
                            name: 'precision',
                            maxLength: 10,
                            bind: {
                                value: '{theAttribute.unitOfMeasure}'
                            },
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    const form = this.up('form');
                                    const precisionAttributeLocationField = form.down(
                                        '#unitOfMeasureLocationFieldDouble'
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
                            itemId: 'unitOfMeasureLocationFieldDouble',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation'
                            },
                            name: 'unitOfMeasureLocationDouble',
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
                                const precisionAttributeField = form.down('#unitOfMeasureFieldDouble');
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
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'numberfield',
                            itemId: 'scaleAttributeField',
                            minValue: 0,
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals'
                            },
                            name: 'visibleDecimals',
                            step: 1,
                            bind: {
                                value: '{theAttribute.visibleDecimals}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
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
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.visibledecimals'
                            },
                            bind: {
                                value: '{theAttribute.visibleDecimals}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
