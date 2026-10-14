Ext.define('CMDBuildUI.view.fields.draganddropfile.DragAndDropFileModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.draganddropfilefield',

    stores: {
        files: {
            model: 'CMDBuildUI.model.dms.File',
            autoDestroy: true,
            listeners: {
                datachanged: 'onStoreDataChanged'
            }
        }
    }
});