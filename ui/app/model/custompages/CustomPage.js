Ext.define('CMDBuildUI.model.custompages.CustomPage', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        device: {
            default: 'default',
            mobile: 'mobile'
        }
    },
    fields: [
        {
            name: 'name',
            type: 'string',
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true
        },
        {
            name: 'alias',
            type: 'string',
            critical: true
        },
        {
            name: 'componentId',
            type: 'string',
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            device: 'devices',
            type: 'auto',
            crititcal: false,
            persist: false,
            defaultValue: []
        },
        {
            name: 'icon_font',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_description',
            type: 'string',
            persist: false
        }
    ],

    proxy: {
        url: '/custompages/',
        type: 'baseproxy'
    },

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('description');
        }
        return this.get('_description_translation') || this.get('description');
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    }
});
