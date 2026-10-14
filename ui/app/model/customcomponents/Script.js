Ext.define('CMDBuildUI.model.customcomponents.Script', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'alias',
        type: 'string',
        critical: true
    }, {
        name: 'data',
        type: 'string',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'type',
        type: 'string',
        crititcal: true,        
        defaultValue: 'script'
    }],

    proxy: {
        url: '/components/core/script',
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
            return this.get("description");
        }
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    }
});