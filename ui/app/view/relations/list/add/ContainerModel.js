Ext.define('CMDBuildUI.view.relations.list.add.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-add-container',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
        objectTypeName: null,
        originId: null,
        relationDirection: null,
        selection: null,
        valid: {
            attrs: true
        },
        singleSelection: true
    }
});
