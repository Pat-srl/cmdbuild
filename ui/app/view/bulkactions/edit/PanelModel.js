Ext.define('CMDBuildUI.view.bulkactions.edit.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.bulkactions-edit-panel',

    data: {
        theObject: {},
        keysBindFields: {},
        updateErrorMessage: true
    },

    stores: {
        attributes: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: 'memory',
            data: '{attributeslist}',
            filters: [{
                property: 'writable',
                value: true
            }],
            sorters: ['_description_translation'],
            grouper: {
                property: '_group_description_translation'
            }
        }
    }

});
