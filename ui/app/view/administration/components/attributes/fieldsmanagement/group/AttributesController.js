Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.AttributesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-fieldsmanagement-group-attributes',

    control: {
        '#': {
            afterrender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel(),
            group = view.getGroup(),
            attributes = group.get('attributes').getRange(),
            isAllAttributes = view.getIsAllAttributes();
        vm.set('attributesData', attributes);
        Ext.Array.forEach(attributes, function (attribute) {
            attribute.set('descriptionWithName', attribute.getDescriptionWithName());
        });
        var allAttributesColumn = {
            xtype: 'administration-components-attributes-fieldsmanagement-group-form-column',
            flex: 1,
            style: {
                height: '100%'
            },
            isAllAttributes: isAllAttributes,
            store: Ext.create('Ext.data.Store', {
                fields: ['attribute'],
                proxy: {
                    type: 'memory'
                },
                data: attributes,
                sorters: ['index'],
                autoDestroy: true,
                autoLoad: false
            })
        };
        view.add(allAttributesColumn);

    }
});
