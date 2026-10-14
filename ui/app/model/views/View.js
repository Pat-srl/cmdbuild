Ext.define('CMDBuildUI.model.views.View', {
    extend: 'CMDBuildUI.model.base.Base',

    mixins: [
        'CMDBuildUI.mixins.model.Attribute',
        'CMDBuildUI.mixins.model.Filter',
        'CMDBuildUI.mixins.model.ImportExportTemplates'
    ],

    statics: {
        types: {
            sql: 'SQL',
            filter: 'FILTER',
            calendar: 'CALENDAR',
            join: 'JOIN'
        }
    },
    isView: true,
    fields: [
        {
            name: 'name',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'filter',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'sourceClassName',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: '_sourceClassName_description',
            type: 'string',
            persist: false
        },
        {
            name: 'sourceFunction',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'sourceCalendarDefinition',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'type',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: true
        },
        {
            name: 'shared',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: 'contextMenuItems',
            type: 'auto',
            critical: true
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

    idProperty: '_id',

    proxy: {
        url: '/views/',
        type: 'baseproxy'
    },

    hasMany: [
        {
            model: 'CMDBuildUI.model.Attribute',
            name: 'viewAttributes'
        },
        {
            model: 'CMDBuildUI.model.AttributeGrouping',
            name: 'attributeGroups'
        },
        {
            model: 'CMDBuildUI.model.base.Filter',
            name: 'filters'
        },
        {
            model: 'CMDBuildUI.model.ContextMenuItem',
            name: 'contextMenuItems',
            associationKey: 'contextMenuItems'
        },
        {
            model: 'CMDBuildUI.model.importexports.Template',
            name: 'importExportTemplates'
        }
    ],

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,

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
        if (this.get('type') === CMDBuildUI.model.views.View.types.join) {
            return this.get('_id');
        } else {
            return this.get('name');
        }
    },

    attributesStoreName: 'viewAttributes',
    /**
     * Get attributes url
     * @return  {String}
     */
    getAttributesUrl: function () {
        if (this.get('type') === CMDBuildUI.model.views.View.types.join) {
            return CMDBuildUI.util.api.Views.getAttributesUrl(this.get('_id'));
        } else {
            return CMDBuildUI.util.api.Views.getAttributesUrl(this.get('name'));
        }
    },

    /**
     * @return {String} import/export templates url
     */
    getImportExportTemplatesUrl: function () {
        return CMDBuildUI.util.api.Views.getImportExportTemplatesUrl(this.get('name'));
    }
});
