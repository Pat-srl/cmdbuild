Ext.define('CMDBuildUI.model.searchfilters.Searchfilter', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
    },

    fields: [{
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'configuration',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'target',
        type: 'string',
        persist: true,
        critical: true        
    }, {
        name: 'defaultGroups',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'shared',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'active',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }],

    proxy: {
        type: 'baseproxy',
        url: '/classes/_ANY/filters',
        pageSize: 0
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
