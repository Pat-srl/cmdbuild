// unused


Ext.define('CMDBuildUI.model.users.CustomPrivileges', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'clone',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return !value;
        }
    }, {
        name: 'create',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return !value;
        }
    }, {
        name: 'delete',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return !value;
        }
    }, {
        name: 'update',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return !value;
        }
    }],

    convertOnSet: true,


    proxy: {
        type: 'memory'
    }
});