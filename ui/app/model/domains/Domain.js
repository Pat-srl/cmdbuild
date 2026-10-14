Ext.define('CMDBuildUI.model.domains.Domain', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        cardinalities: {
            onetoone: '1:1',
            onetomany: '1:N',
            manytoone: 'N:1',
            manytomany: 'N:N'
        },
        cascadeAction: {
            restrict: 'restrict',
            deletecard: 'delete',
            setnull: 'setnull',
            auto: 'auto'
        }
    },

    mixins: ['CMDBuildUI.mixins.model.Attribute'],

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
            name: 'source',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'sourceDisplay',
            type: 'string',
            persist: false,
            convert: function (v, record) {
                return record.getSourceDescription();
            }
        },
        {
            name: 'sourceProcess',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'destination',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'destinationDisplay',
            type: 'string',
            persist: false,
            convert: function (v, record) {
                return record.getDestinationDescription();
            }
        },
        {
            name: 'destinationProcess',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'cardinality',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'descriptionDirect',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'descriptionInverse',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'isMasterDetail',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'descriptionMasterDetail',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            persist: true,
            critical: true
        },
        {
            name: 'disabledDestinationDescendants',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        },
        {
            name: 'disabledSourceDescendants',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        },
        {
            name: 'masterDetailAggregateAttrs',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        },
        {
            name: 'cascadeActionDirect',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'cascadeActionInverse',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'cascadeActionInverse_askConfirm',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'cascadeActionInverse_askConfirm',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'sourceInline',
            type: 'boolean',
            defaultValue: false,
            persist: true,
            critical: true
        },
        {
            name: 'sourceDefaultClosed',
            type: 'boolean',
            defaultValue: false,
            persist: true,
            critical: true
        },
        {
            name: 'destinationInline',
            type: 'boolean',
            defaultValue: false,
            persist: true,
            critical: true
        },
        {
            name: 'destinationDefaultClosed',
            type: 'boolean',
            defaultValue: false,
            persist: true,
            critical: true
        },
        {
            name: 'filterMasterDetail',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'masterDetailDisabledCreateAttrs',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        },
        {
            name: 'sourceFilter',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'targetFilter',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'sourceEditable',
            type: 'boolean',
            defaultValue: true,
            persist: true,
            critical: true
        },
        {
            name: 'targetEditable',
            type: 'boolean',
            defaultValue: true,
            persist: true,
            critical: true
        }
    ],

    proxy: {
        url: CMDBuildUI.util.api.Domains.getAllDomainsUrl(),
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    },

    hasMany: [
        {
            name: 'attributes',
            model: 'CMDBuildUI.model.Attribute'
        }
    ],

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.domain,

    source: 'domains.Domains',

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
     * Get get the description of source class or process.
     *
     * @return {String}
     */
    getSourceDescription: function () {
        var fn = this.get('sourceProcess')
            ? CMDBuildUI.util.helper.ModelHelper.getProcessDescription
            : CMDBuildUI.util.helper.ModelHelper.getClassDescription;
        return fn(this.get('source'));
    },

    /**
     * Get get the description of destination class or process.
     *
     * @return {String}
     */
    getDestinationDescription: function () {
        var fn = this.get('destinationProcess')
            ? CMDBuildUI.util.helper.ModelHelper.getProcessDescription
            : CMDBuildUI.util.helper.ModelHelper.getClassDescription;
        return fn(this.get('destination'));
    },

    /**
     * Get translation for Master/Detail description.
     *
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */

    getTranslatedDescriptionMasterDetail: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('descriptionMasterDetail') || this.get('description');
        }
        return (
            this.get('_descriptionMasterDetail_translation') ||
            this.get('descriptionMasterDetail') ||
            this.get('_description_translation') ||
            this.get('description')
        );
    },

    /**
     * Get translation for direct description.
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescriptionDirect: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('descriptionDirect');
        }
        return this.get('_descriptionDirect_translation') || this.get('descriptionDirect');
    },

    /**
     * Get translation for inverse description.
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescriptionInverse: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('descriptionInverse');
        }
        return this.get('_descriptionInverse_translation') || this.get('descriptionInverse');
    },

    /**
     * @return {String} domains url
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Domains.getAttributes(this.get('name'));
    }
});
