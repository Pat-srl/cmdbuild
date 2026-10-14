Ext.define('CMDBuildUI.model.homepages.Homepage', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [
        {
            name: 'group',
            type: 'string',
            critical: true
        },
        {
            name: 'components',
            type: 'auto',
            critical: true
        },
        {
            name: 'layout',
            type: 'auto',
            critical: true,
            defaultValue: {}
        },
        {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.dashboards.Component',
            name: 'components',
            associationKey: 'components'
        }
    ],

    proxy: {
        url: '/homepages/',
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
