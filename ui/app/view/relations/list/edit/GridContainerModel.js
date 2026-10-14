Ext.define('CMDBuildUI.view.relations.list.edit.GridContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-edit-gridcontainer',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
        objectTypeName: null,
        relationDirection: null,
        relselection: [],
        selection: [],
        values: {},
        attributesvalid: false
    },

    formulas: {
        objectTypeDescription: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function(data) {
                if (data.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                    return CMDBuildUI.util.helper.ModelHelper.getClassDescription(data.objectTypeName);
                } else if (data.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    return CMDBuildUI.util.helper.ModelHelper.getProcessDescription(data.objectTypeName);
                }
            }
        },

        disableSaveButton: {
            bind: {
                selection: '{relselection.length}',
                formvalid: '{attributesvalid}'
            }, 
            get: function(data) {
                return !(data.selection && data.formvalid);
            }
        }
    }

});
