Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Time', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-timefields',

    items: [{
        xtype: 'fieldcontainer',
        items: [{
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseconds,            
            bind: {
                value: '{theAttribute.showSeconds}',
                disabled: '{actions.view}'
            }
        }]
    }]
});