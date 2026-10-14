Ext.define('CMDBuildUI.model.emails.Template', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],

    statics: {
        providers: {
            email: 'email',
            inappnotification: 'chat',
            mobilenotification: 'mobileApp'
        }
    },
    fields: [{
        name: 'name',
        type: 'string',
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'description',
        type: 'string',
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'from',
        type: 'string',
        critical: true
    }, {
        name: 'to',
        type: 'string',
        critical: true
    }, {
        name: 'cc',
        type: 'string',
        critical: true
    }, {
        name: 'bcc',
        type: 'string',
        critical: true
    }, {
        name: 'subject',
        type: 'string',
        critical: true
    }, {
        name: 'body',
        type: 'string',
        critical: true
    }, {
        name: 'account',
        type: 'string',
        critical: true
    }, {
        name: 'keepSynchronization',
        type: 'boolean',
        critical: true
    }, {
        name: 'promptSynchronization',
        type: 'boolean',
        critical: true
    }, {
        name: 'delay',
        type: 'number',
        critical: true
    }, {
        name: 'data',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }, {
        name: 'contentType',
        type: 'string',
        defaultValue: 'text/html',
        critical: true
    }, {
        name: 'signature',
        type: 'string',
        critical: true
    }, {
        name: 'provider',
        type: 'string',
        critical: true
    }, {
        name: 'showOnClasses',
        type: 'string',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        critical: true
    }, {
        name: 'reports',
        type: 'auto',
        defaultValue: [],
        critical: true
    }],

    proxy: {
        url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
        type: 'baseproxy'
    },

    clone: function () {
        var newTemplate = this.copy();
        newTemplate.set('_id', undefined);
        newTemplate.set('name', Ext.String.format('{0}_clone', this.get('name')));
        newTemplate.set('description', Ext.String.format('{0}_clone', this.get('description')));
        newTemplate.crudState = "C";
        newTemplate.phantom = true;
        delete newTemplate.crudStateWas;
        delete newTemplate.previousValues;
        delete newTemplate.modified;
        return newTemplate;
    }
});