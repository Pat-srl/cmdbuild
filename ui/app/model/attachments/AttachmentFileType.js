Ext.define('CMDBuildUI.model.attachments.AttachmentFileType', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'extensions',
        type: 'auto',
        defaultValue: []
    }, {
        name: 'mimeTypes',
        type: 'auto',
        defaultValue: []
    }, {
        name: '_extensions',
        type: 'string',
        calculate: function (data) {
            return data.extensions.join('\n');
        },
        serialize: function(){
            
        }
    }, {
        name: '_mimeTypes',
        type: 'string',
        calculate: function (data) {
            return data.mimeTypes.join('\n');
        },
        serialize: function(){

        }
    }],

    proxy: {
        url: '/configuration/attachments/categories/',
        type: 'baseproxy'
    },

    sanitize: function(){
        this.sanitizeExtensions();
        this.sanitizeMimeTypes();
        return this;
    },
    sanitizeExtensions: function(){
        var extensions = [];
        Ext.Array.forEach(this.get('extensions'), function(extension){
            // TODO: permit * or other specia char?
            extensions.push(extension.trim());
        });
        this.set('extension', extensions);
    },
    sanitizeMimeTypes: function(){
        var mimeTypes = [];
        Ext.Array.forEach(this.get('mimeTypes'), function(mimeType){
            mimeTypes.push(mimeType.trim());
        });
        this.set('mimeTypes', mimeTypes);
    }
});