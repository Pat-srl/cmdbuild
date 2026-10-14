Ext.define('CMDBuildUI.model.classes.Class', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        classtypes: {
            simple: 'simple',
            standard: 'standard'
        },
        getClasstypes: function () {
            return [
                {
                    value: 'standard',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.standard // Standard
                },
                {
                    value: 'simple',
                    label: CMDBuildUI.locales.Locales.administration.classes.texts.simple // Simple'
                }
            ];
        },
        masterParentClass: CMDBuildUI.util.helper.ModelHelper.systemClasses.class
    },

    requires: ['Ext.data.validator.Format', 'Ext.data.validator.Length', 'CMDBuildUI.validator.TrimPresence'],

    mixins: [
        'CMDBuildUI.mixins.model.Filter',
        'CMDBuildUI.mixins.model.Domain',
        'CMDBuildUI.mixins.model.Attribute',
        'CMDBuildUI.mixins.model.Hierarchy',
        'CMDBuildUI.mixins.model.FormTrigger',
        'CMDBuildUI.mixins.model.ImportExportTemplates',
        'CMDBuildUI.mixins.model.Gis'
    ],
    isClass: true,
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
            name: 'parent',
            type: 'string',
            critical: true,
            defaultValue: CMDBuildUI.util.helper.ModelHelper.systemClasses.class
        },
        {
            name: 'prototype',
            type: 'boolean',
            critical: true
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'standard',
            critical: true
        },
        {
            name: 'system',
            type: 'boolean'
        },
        {
            name: 'dmsCategories',
            critical: true,
            type: 'auto'
        },
        {
            name: 'dmsCategory',
            critical: true,
            type: 'string'
        },
        {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: 'defaultFilter',
            type: 'string',
            critical: true,
            persist: true
        },
        {
            name: 'defaultImportTemplate',
            type: 'string',
            critical: true,
            persist: true
        },
        {
            name: 'defaultExportTemplate',
            type: 'string',
            critical: true,
            persist: true
        },
        {
            name: 'defaultOrder',
            type: 'auto',
            critical: true
        },
        {
            name: 'formTriggers',
            type: 'auto',
            critical: true
        },
        {
            name: 'contextMenuItems',
            type: 'auto',
            critical: true
        },
        {
            name: 'widgets',
            type: 'auto',
            critical: true
        },
        {
            name: 'attributeGroups',
            type: 'auto',
            critical: true,
            defaultValue: []
        },
        {
            name: 'multitenantMode',
            type: 'string',
            critical: true,
            defaultValue: 'never' // values ca be: never, always, mixed
        },
        {
            name: '_icon',
            type: 'number',
            critical: true,
            persistent: true
        },
        {
            name: '_iconPath',
            type: 'string',
            calculate: function (data) {
                if (data._icon) {
                    return Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, data._icon);
                } else {
                    return null;
                }
            }
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
        },
        {
            name: 'attachmentsInline',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: 'attachmentsInlineClosed',
            type: 'boolean',
            defaultValue: true,
            critical: true
        },
        {
            name: 'noteInline',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: 'noteInlineClosed',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: 'noteInGrid',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: 'domainOrder',
            type: 'auto',
            defaultValue: [],
            critical: true
        },
        {
            name: 'formStructure',
            type: 'auto',
            critical: true
        },
        {
            name: 'validationRule',
            type: 'string',
            critical: true
        },
        {
            name: 'autoValue',
            type: 'string',
            critical: true
        },
        {
            name: 'help',
            type: 'string',
            critical: true
        },
        {
            name: 'uiRouting_mode',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'uiRouting_target',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'uiRouting_custom',
            type: 'auto',
            persist: true,
            critical: true,
            defaultValue: {}
        },
        {
            name: 'barcodeSearchAttr',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'barcodeSearchRegex',
            type: 'string',
            persist: true,
            critical: true
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.Attribute',
            name: 'attributes'
        },
        {
            model: 'CMDBuildUI.model.domains.ObjectDomain',
            name: 'domains'
        },
        {
            model: 'CMDBuildUI.model.domains.FkDomain',
            name: 'fkdomains'
        },
        {
            model: 'CMDBuildUI.model.AttributeOrder',
            name: 'defaultOrder',
            associationKey: 'defaultOrder'
        },
        {
            model: 'CMDBuildUI.model.FormTrigger',
            name: 'formTriggers',
            associationKey: 'formTriggers'
        },
        {
            model: 'CMDBuildUI.model.ContextMenuItem',
            name: 'contextMenuItems',
            associationKey: 'contextMenuItems'
        },
        {
            model: 'CMDBuildUI.model.WidgetDefinition',
            name: 'widgets',
            associationKey: 'widgets'
        },
        {
            model: 'CMDBuildUI.model.base.Filter',
            name: 'filters'
        },
        {
            model: 'CMDBuildUI.model.AttributeGrouping',
            name: 'attributeGroups'
        },
        {
            model: 'CMDBuildUI.model.importexports.Template',
            name: 'importExportTemplates'
        },
        {
            model: 'CMDBuildUI.model.importexports.Gate',
            name: 'importExportGISTemplates'
        },
        {
            model: 'CMDBuildUI.model.thematisms.Thematism',
            name: 'thematisms'
        },
        {
            model: 'CMDBuildUI.model.gis.GeoAttribute',
            name: 'geoAttributes'
        },
        {
            model: 'CMDBuildUI.model.map.GeoLayers',
            name: 'geolayers'
        }
    ],

    validators: {
        name: ['trimpresence'],
        description: ['trimpresence']
    },

    proxy: {
        url: '/classes/',
        type: 'baseproxy',
        reader: {
            type: 'json'
        }
    },

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
    source: 'classes.Classes',

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
     * @return {Boolean}
     */
    isSimpleClass: function () {
        return this.get('type') === CMDBuildUI.model.classes.Class.classtypes.simple;
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    },

    getObjectParent: function () {
        return Ext.getStore('classes.Classes').getById(this.get('parent'));
    },

    getObjectStore: function () {
        return Ext.getStore('classes.Classes');
    },

    /**
     * @return {String} domains url
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Classes.getAttributes(this.get('name'));
    },

    /**
     * @return {String} domains url
     */
    getDomainsUrl: function () {
        return CMDBuildUI.util.api.Classes.getDomains(this.get('name'));
    },

    /**
     * @return {String} import/export templates url
     */
    getImportExportTemplatesUrl: function () {
        return CMDBuildUI.util.api.Classes.getImportExportTemplatesUrl(this.get('name'));
    },

    /**
     * @return {String} import/export gates url
     */
    getImportExportGatesUrl: function () {
        return CMDBuildUI.util.api.Classes.getImportExportGatesUrl(this.get('name'));
    },

    /**
     * @returns {String} geo layers url
     */
    getGeoLayersUrl: function () {
        return CMDBuildUI.util.api.Classes.getExternalGeoAttributes(this.get('name'));
    },

    /**
     * @returns {String} thematisms url
     */
    getThematismsUrl: function () {
        return CMDBuildUI.util.api.Classes.getThematismsUrl(this.get('name'));
    },

    /**
     * @returns {Sting} geo attributes url
     */
    getGeoAttributesUrl: function () {
        return CMDBuildUI.util.api.Classes.getGeoAttributes(this.get('name'));
    }
});
