Ext.define('CMDBuildUI.model.webhooks.Webhook', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        events: {
            create: 'card_create_after',
            update: 'card_update_after',
            delete: 'card_delete_after',
            advance: 'after_advance'
        },
        methods: {
            post: 'post',
            get: 'get',
            put: 'put',
            delete: 'delete'
        }
    },
    fields: [{
        name: 'code',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'body',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'url',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'language',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'language',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'event',
        type: 'string',
        critical: true
    }, {
        name: 'method',
        type: 'string',
        critical: true,
        defaultValue: 'get'
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'headers',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }
    ],

    idProperty: '_id',

    proxy: {
        url: '/etl/webhook/',
        type: 'baseproxy'
    },
    /**
     * Return a clean clone of attribute.
     * 
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function () {
        var clone = this.copy();
        clone.set('_id', undefined);
        clone.set('code', '');
        clone.set('description', '');
        clone.crudState = "C";
        clone.phantom = true;
        delete clone.crudStateWas;
        delete clone.previousValues;
        delete clone.modified;
        return clone;
    }
});