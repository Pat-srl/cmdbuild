Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.BigInteger', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-attribute-bigintegerfields',

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
                            itemId: 'unitOfMeasureFieldBigInteger',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                            },
                            name: 'unitOfMeasureBigInteger',
                            autoEl: {
                                'data-testid': 'attribute-unitOfMeasureBigInteger_input'
                            },
                            maxLength: 10,
                            bind: {
                                value: '{theAttribute.unitOfMeasure}'
                            },
                            listeners: {
                                change: function (input, newValue, oldValue) {
                                    const form = this.up('form');
                                    const precisionAttributeLocationField = form.down(
                                        '#unitOfMeasureLocationFieldBigInteger'
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
                            itemId: 'unitOfMeasureLocationFieldBigInteger',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasurelocation'
                            },
                            name: 'unitOfMeasureLocationBigInteger',
                            autoEl: {
                                'data-testid': 'attribute-unitOfMeasureLocationBigInteger_input'
                            },
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
                                const precisionAttributeField = form.down('#unitOfMeasureFieldBigInteger');
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
                            itemId: 'attribute-showseparatorBigInteger',
                            name: 'showSeparator',
                            autoEl: {
                                'data-testid': 'attribute-showseparatorBigInteger_input'
                            },
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
                            autoEl: {
                                'data-testid': 'attribute-unitOfMeasureBigInteger_display'
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
                            autoEl: {
                                'data-testid': 'attribute-unitOfMeasureLocationBigInteger_display'
                            },
                            bind: {
                                value: '{_unitOfMeasureLocation_description}',
                                hidden: '{!theAttribute.unitOfMeasure}'
                            },
                            renderer: function (value) {
                                if (!value) {
                                    const vm = this.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                store: '{unitOfMeasuresStore}',
                                                location: '{theAttribute.unitOfMeasureLocation}'
                                            }
                                        },
                                        function (data) {
                                            if (data.store && data.location) {
                                                vm.set(
                                                    '_unitOfMeasureLocation_description',
                                                    data.store.findRecord('value', data.location).get('label')
                                                );
                                            }
                                        }
                                    );
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
                            itemId: 'attribute-showseparatorBigInteger',
                            name: 'showSeparator',
                            autoEl: {
                                'data-testid': 'attribute-showseparatorBigInteger_input'
                            },
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
