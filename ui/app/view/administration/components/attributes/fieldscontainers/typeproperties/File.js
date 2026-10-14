Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.File', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-filefields',
    layout: 'column',

    items: [
        {
            columnWidth: 0.5,
            padding: '0 15 0 0',
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory'
            },
            items: [
                {
                    xtype: 'combo',
                    name: 'dmsCategory',
                    itemId: 'dmsCategory',
                    valueField: 'value',
                    displayField: 'label',
                    forceSelection: true,
                    hidden: true,
                    bind: {
                        value: '{theAttribute.dmsCategory}',
                        disabled: '{actions.edit}',
                        hidden: '{actions.view}',
                        store: '{dmsCategoryValueStore}'
                    }
                },
                {
                    xtype: 'displayfield',
                    hidden: true,
                    bind: {
                        value: '{theAttribute._dmsCategory_description}',
                        hidden: '{!actions.view}'
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpreview,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpreview'
            },
            items: [
                {
                    xtype: 'checkbox',
                    bind: {
                        value: '{theAttribute.showPreview}',
                        disabled: '{actions.view}'
                    }
                }
            ]
        }
    ]
});
