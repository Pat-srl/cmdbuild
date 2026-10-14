Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.fieldset.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-bus-descriptors-tabitems-properties-fieldsets-generaldatafieldset',

    viewModel: {

    },
    ui: 'administration-formpagination',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        ui: 'administration-formpagination',
        items: [{
                columnWidth: '1',
                xtype: 'fieldcontainer',
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                        name: {
                            vtype: 'nameInputValidationWithDash',
                            allowBlank: false,
                            bind: {
                                value: '{theDescriptor.code}'
                            }
                        }
                    }, true, '[name="description"]'),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theDescriptor.description}'
                            }
                        }
                    })
                ]
            }, {
                columnWidth: 1,
                xtype: 'fieldcontainer',
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.bus.enabled,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.enabled'
                                }
                            },
                            bind: {
                                disabled: '{actions.view}',
                                value: '{theDescriptor.enabled}'
                            }
                        }
                    })
                ]
            }]
    }]
});