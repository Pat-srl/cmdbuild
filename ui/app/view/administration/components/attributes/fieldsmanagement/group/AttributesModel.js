Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-fieldsmanagement-group-attributes',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
    },
    stores: {
        groupAttributes: {
            model: 'CMDBuildUI.model.Attribute',
            data: '{attributesData}',
            proxy: {
                type: 'memory'
            }
        },
        draggableAttributes: {
            model: 'CMDBuildUI.model.Attribute',
            source: '{groupAttributes}'            
        }
    }

});
