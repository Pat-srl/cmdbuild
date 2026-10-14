Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-attributes-attributes',
    data: {
        selected: {}
    },
    stores: {
        allAttributes: {
            source: '{theProcess.attributes}',
            filters: [
                function (item) {
                    return item.canAdminShow();
                }
            ],
            sorters: ['index']
        }
    }
});