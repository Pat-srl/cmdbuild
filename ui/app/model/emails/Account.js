Ext.define('CMDBuildUI.model.emails.Account', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],
    fields: [{
        name: 'name',
        type: 'string',
        validators: ['trimpresence'],
        critical: true
    }, {
        name: 'default',
        type: 'boolean',
        critical: true
    }, {
        name: 'username',
        type: 'string',
        critical: true
    }, {
        name: 'password',
        type: 'string',
        critical: true
    }, {
        name: 'address',
        type: 'string',
        validators: ['trimpresence'],
        critical: true
    }, {
        name: 'smtp_server',
        type: 'string',
        critical: true
    }, {
        name: 'smtp_port',
        type: 'int',
        critical: true
    }, {
        name: 'smtp_ssl',
        type: 'boolean',
        critical: true
    }, {
        name: 'smtp_starttls',
        type: 'boolean',
        critical: true
    }, {
        name: 'imap_output_folder',
        type: 'auto',
        critical: true
    }, {
        name: 'imap_server',
        type: 'string',
        critical: true
    }, {
        name: 'imap_port',
        type: 'int',
        critical: true
    }, {
        name: 'imap_ssl',
        type: 'boolean',
        critical: true
    }, {
        name: 'imap_starttls',
        type: 'boolean',
        critical: true
    }, {
        name: 'maxAttachmentSizeForEmail',
        type: 'string',
        critical: true,
        defaultValue: null
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'auth_type',
        type: 'string',
        critical: true,
        defaultValue: 'default'
    }],
    proxy: {
        url: CMDBuildUI.util.api.Emails.getAccountsUrl(),
        type: 'baseproxy'
    },

    test: function (btn) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-testemailaccount');
        var id = this.phantom ? '_NEW' : this.getId();
        Ext.Ajax.request({
            url: Ext.String.format('{0}/email/accounts/{1}/test', CMDBuildUI.util.Config.baseUrl, id),
            method: 'POST',
            jsonData: this.getData(),
            success: function (response) {
                deferred.resolve(true);
            },
            error: function (response) {
                deferred.resolve(false);
            }
        });
        return deferred.promise;
    }

});