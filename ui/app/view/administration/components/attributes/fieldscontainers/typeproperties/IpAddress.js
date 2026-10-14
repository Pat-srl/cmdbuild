Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.IpAddress', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-ipaddressfields',
    
    items: [{
        layout: 'column',
        items: [{
            // ADD / EDIT
            columnWidth: 0.5,
            xtype: 'combo',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.iptype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.iptype'
            },
            displayField: 'label',
            valueField: 'value',
            typeAhead: true,
            queryMode: 'local',
            name: 'ipType',
            bind: {
                value: '{theAttribute.ipType}',
                store: '{ipTypeStore}',
                hidden: '{actions.view}'
            }
            // renderer: CMDBuildUI.util.administration.helper.RendererHelper.getIpType
        }, {
            // VIEW
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.iptype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.iptype'
            },
            bind: {
                value: '{theAttribute.ipType}',
                hidden: '{!actions.view}'
            },
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getIpType
        }]
    }]
});