Ext.define('CMDBuildUI.model.emails.Signature', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'code',
        type: 'string',
        validators: ['presence'],
        critical: true
    }, {
        name: '_default',
        type: 'boolean',
        critical: false,
        persist: false
    }, {
        name: 'description',
        type: 'string',
        validators: ['presence'],
        critical: true
    }, {
        name: 'content_html',
        type: 'string',
        validators: ['presence'],
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        critical: true
    }],

    proxy: {
        url: CMDBuildUI.util.api.Emails.getSignaturesUrl(),
        type: 'baseproxy'
    }

});