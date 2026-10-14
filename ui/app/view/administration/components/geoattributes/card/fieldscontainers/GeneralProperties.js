Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper'],
    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            layout: 'column',
            items: [CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper.getTypeField()]
        },
        {
            layout: 'column',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                    {
                        name: {
                            vtype: 'nameInputValidation',
                            allowBlank: false,
                            bind: {
                                value: '{theGeoAttribute.name}'
                            }
                        }
                    },
                    true,
                    '[name="description"]'
                ),

                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                    description: {
                        allowBlank: false,
                        bind: {
                            value: '{theGeoAttribute.description}'
                        },
                        style: {
                            paddingRight: 0
                        },
                        fieldcontainer: {
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateClick'
                        }
                    }
                })
            ]
        },
        {
            layout: 'column',
            items: [
                /**
                 *
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 *
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
                    name: 'zoomMin',
                    increment: 1,
                    minValue: 0,
                    maxValue: 25,
                    // multiplier: 1,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomMin}'
                    }
                }),

                /**
                 *
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 *
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
                    name: 'zoomMax',
                    increment: 1,
                    minValue: 0,
                    maxValue: 25,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomMax}'
                    }
                })
            ]
        },
        {
            layout: 'column',
            items: [
                /**
                 *
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 *
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    name: 'zoomDef',
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
                    increment: 1,
                    minValue: 0,
                    maxValue: 25,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomDef}'
                    }
                })
            ]
        },
        {
            layout: 'column',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                    active: {
                        bind: {
                            disabled: '{actions.view}',
                            value: '{theGeoAttribute.active}'
                        }
                    }
                })
            ]
        }
    ]
});
