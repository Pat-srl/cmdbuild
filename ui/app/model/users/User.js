Ext.define('CMDBuildUI.model.users.User', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],
    statics: {
        myuser: '@MY_USER'
    },
    fields: [{
        name: 'username',
        description: CMDBuildUI.locales.Locales.administration.emails.username,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.emails.username'
        },
        type: 'string',
        validators: [
            'trimpresence'
        ],
        persist: true,
        critical: true,
        showInGrid: true
    }, {
        name: 'description',
        description: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        type: 'string',
        persist: true,
        critical: true,
        showInGrid: true
    }, {
        name: 'email',
        description: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
        localized: {
            description: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
        },
        type: 'string',
        persist: true,
        critical: true,
        showInGrid: true
    }, {
        name: 'lastExpiringNotification',
        type: 'date'
    }, {
        name: 'lastPasswordChange',
        type: 'date'
    }, {
        name: 'passwordExpiration',
        type: 'date'
    }, {
        name: 'service',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'defaultUserGroup',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'multiGroup',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }, {
        name: 'userGroups',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: 'userTenants',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: 'password',
        type: 'string'
    }, {
        name: 'rolePrivileges',
        type: 'auto'
    }, {
        name: 'multiTenantActivationPrivileges',
        type: 'string',
        critical: true,
        defaultValue: 'any'
    }, {
        name: 'initialPage',
        type: 'string',
        critical: true
    }, {
        name: 'language',
        type: 'string',
        critical: true
    }, {
        name: '_description_username',
        calculate: function (data) {
            return !Ext.isEmpty(data.description) ? data.description : data.username;
        }
    }],

    proxy: {
        url: '/users/',
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    },

    clone: function () {
        var newUser = this.copy();
        newUser.set('_id', undefined);
        newUser.set('username', '');
        newUser.set('description', '');
        newUser.crudState = "C";
        newUser.phantom = true;
        delete newUser.crudStateWas;
        delete newUser.previousValues;
        delete newUser.modified;
        return newUser;
    }
});