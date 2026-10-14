Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-models-tabitems-attributes-attributes',
    data: {
        selected: {},
        isOtherPropertiesHidden: false
    },    
    stores: {    
        allAttributes: {
            source: '{theModel.attributes}',
            filters: [
                function (item) {
                    return item.canAdminShow();
                }
            ]      
        }
    }
});