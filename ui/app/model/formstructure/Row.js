Ext.define('CMDBuildUI.model.formstructure.Row', {
    extend: 'Ext.data.Model',

    require: ['CMDBuildUI.model.formstructure.Column'],

    fields: [{
        name: 'columns',
        type: 'auto'
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.formstructure.Column',
        name: 'columns',
        associationKey: 'columns'
    }]

});