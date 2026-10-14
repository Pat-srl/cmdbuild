Ext.define('CMDBuildUI.view.administration.content.gis.externalservices.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.externalservices.ViewController',
        'CMDBuildUI.view.administration.content.gis.externalservices.ViewModel'
    ],

    alias: 'widget.administration-content-gis-externalservices-view',
    controller: 'administration-content-gis-externalservices-view',
    viewModel: {
        type: 'administration-content-gis-externalservices-view'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',

    items: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true
                },
                'externalservices',
                'theConfig'
            ),
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            }
        },
        {
            xtype: 'container',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.gis.mapservice,
                    layout: 'column',
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.gis.mapservice'
                    },
                    items: [
                        CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                            columnWidth: 0.5,
                            padding: '0 15 0 0',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.minimumzoom,
                            name: 'minimumzoom',
                            increment: 1,
                            minValue: 0,
                            maxValue: 25,
                            // multiplier: 1,
                            inputDecimalPrecision: 0,
                            sliderDecimalPrecision: 0,
                            bind: {
                                value: '{theConfig.minimumzoom}'
                            }
                        }),

                        CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                            columnWidth: 0.5,
                            padding: '0 15 0 0',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.maximumzoom,
                            name: 'maximumzoom',
                            increment: 1,
                            minValue: 0,
                            maxValue: 25,
                            // multiplier: 1,
                            inputDecimalPrecision: 0,
                            sliderDecimalPrecision: 0,
                            bind: {
                                value: '{theConfig.maximumzoom}'
                            }
                        }),

                        CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                            columnWidth: 0.5,
                            padding: '0 15 0 0',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.defaultzoom,
                            name: 'defaultzoom',
                            increment: 1,
                            minValue: 0,
                            maxValue: 25,
                            // multiplier: 1,
                            inputDecimalPrecision: 0,
                            sliderDecimalPrecision: 0,
                            bind: {
                                value: '{theConfig.defaultzoom}'
                            }
                        })
                    ]
                },
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.gis.geoserver,
                    layout: 'column',
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getGeoServerEnabledInput(
                            {
                                geoserverenabled: {
                                    bind: {
                                        disabled: '{actions.view}',
                                        value: '{theConfig.geoserverenabled}'
                                    }
                                }
                            },
                            'geoserverenabled'
                        ),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getGeoServerUrlInput({
                            geoserverurl: {
                                bind: {
                                    value: '{theConfig.geoserverurl}'
                                }
                            }
                        }),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getGeoServerWorkspaceInput(
                            {
                                geoserverworkspace: {
                                    bind: {
                                        value: '{theConfig.geoserverworkspace}'
                                    }
                                }
                            },
                            'geoserverworkspace'
                        ),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getGeoServerAdminUserInput(
                            {
                                geoserveradminuser: {
                                    bind: {
                                        value: '{theConfig.geoserveradminuser}'
                                    }
                                }
                            },
                            'geoserveradminuser'
                        ),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getGeoServerAdminPasswordInput({
                            geoserveradminpassword: {
                                renderer: function (value, input) {
                                    if (value && input.xtype == 'displayfield') {
                                        var vlength = value.length;
                                        var hiddenvalue = '';
                                        for (var i = 0; i < vlength; i++) {
                                            hiddenvalue += '*';
                                        }
                                        return hiddenvalue;
                                    }
                                    return value;
                                },
                                bind: {
                                    value: '{theConfig.geoserveradminpassword}'
                                }
                            }
                        })
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ],

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.externalservices);
        this.callParent(arguments);
    }
});
