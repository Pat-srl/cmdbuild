Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Link', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-linkfields',
    items: [{
        xtype: 'fieldcontainer',
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showlabel,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showlabel'
                },
                itemId: 'attribute-showLabel',
                name: 'showLabel',
                autoEl: {
                    'data-testid': 'attribute-showLabel_input'
                },
                readOnly: true,
                bind: {
                    value: '{theAttribute.showLabel}',
                    readOnly: '{actions.view}'
                },
                listeners: {
                    change: function (input, value) {
                        if (!value) {
                            var labelRequiredField = input.up('form').down('#attribute-labelRequired');
                            labelRequiredField.setValue(false);
                        }
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.labelrequired,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.labelrequired'
                },
                itemId: 'attribute-labelRequired',
                name: 'labelRequired',
                autoEl: {
                    'data-testid': 'attribute-labelRequired_input'
                },
                readOnly: true,
                hidden: true,
                bind: {
                    value: '{theAttribute.labelRequired}',
                    readOnly: '{actions.view}',
                    hidden: '{!theAttribute.showLabel}'
                }
            }]
        }]
    }]

});