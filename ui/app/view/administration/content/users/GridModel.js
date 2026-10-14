Ext.define('CMDBuildUI.view.administration.content.users.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-users-grid',
    data: {
        search: {
            value: null
        },
        selected: null,
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    formulas: {
        isObjectyTypeNameSet: {
            bind: '{theUser.username}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return true;
                }
                return false;
            }
        }        
    }

});