Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-attributes-attributes',
    data: {
        selected: {},
        isOtherPropertiesHidden: false
    },
    stores: {
        allAttributes: {
            source: '{theObject.attributes}',
            filters: [
                function (item) {
                    return item.canAdminShow();
                }
            ],
            sorters: ['index']
        }
    }
});