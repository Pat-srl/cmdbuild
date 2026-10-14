Ext.define('CMDBuildUI.view.fields.file.FieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-file-field',

    data: {
        filedata: {
            hidden: true,
            name: null,
            status: null,
            istemp: false,
            extension: null,
            fieldsethidden: true,
            loaded: false
        }
    }

});
