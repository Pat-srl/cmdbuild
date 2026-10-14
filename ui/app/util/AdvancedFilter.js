/**
 * @file CMDBuildUI.util.AdvancedFilter
 * @module CMDBuildUI.util.AdvancedFilter
 * @author PAT srl
 * @access public
 */

/**
 * @typedef RelationCard
 * @type {Object}
 * @property {String} className Class or Process name.
 * @property {Number} id Card or process instance id.
 */

/**
 * @typedef Ecql
 * @type {Object}
 * @property {String} id Filter id.
 * @property {Object} context
 * @property {Object} context.client An object containing all attributes required by the filter with their client values.
 * @property {Object} context.server An object containing all attributes required by the filter with their server values.
 */
Ext.define('CMDBuildUI.util.AdvancedFilter', {
    isAdvancedFilter: true,
    mixins: ['Ext.mixin.Observable'],

    /**
     * @constant {Object} config An object that containing filter configurations.
     * @property {Object} attributes An object containing attribute filter configuration.
     * @property {Object} attributesCustom  An object containing custom attribute filter configuration.
     * @property {Object} relations An object containing relation filter configuration.
     * @property {String} attachmentsQuery A string for fulltext filter on attachments.
     * @property {Object} attachmemtsMeta An object containing attachments metadata filter.
     * @property {Object} ecql An object containing ecql filter configuration.
     * @property {String} query A string for fulltext filter.
     * @property {String} cql A string for cql filter.
     * @property {String} functions A string for function filter.
     * @property {Object} baseFilter An object with a base filter to apply always.
     * @property {Object} baseFilter.attributes Base for attributes filter.
     * @property {Object} baseFilter.relations Base for relations filter.
     * @property {Object} baseFilter.ecql Base for ecql filter.
     * @property {String} baseFilter.query Base for query filter.
     * @property {String} baseFilter.functions Base for function filter.
     * @property {Object} custom An object containing a custom filter. If not empty the other filters are not sent to the server.
     */
    config: {
        /**
         * An object containing attribute filter configuration.
         *
         * @type {Object}
         *
         */
        attributes: {},

        /**
         * An object containing custom attribute filter configuration.
         *
         * @type {Object}
         *
         */
        attributesCustom: {},

        /**
         * An object containing relation filter configuration.
         *
         * @type {Object}
         *
         */
        relations: {},

        /**
         * A string for fulltext filter on attachments.
         *
         * @type {String}
         *
         */
        attachmentsQuery: null,

        /**
         * An object containing attachments metadata filter.
         *
         * @type {Object}
         *
         */
        attachmemtsMeta: null,

        /**
         * An object containing ecql filter configuration.
         *
         * @type {Object}
         *
         */
        ecql: {},

        /**
         * A string for fulltext filter.
         *
         * @type {String}
         *
         */
        query: null,

        /**
         * A string for cql filter.
         *
         * @type {String}
         *
         */
        cql: null,

        /**
         * A string for function filter.
         *
         * @type {String}
         *
         */
        functions: null,

        /**
         * An object with a base filter to apply always.
         *
         * @type {Object}
         * @property {Object} attributes Base for attributes filter.
         * @property {Object} relations Base for relations filter.
         * @property {Object} ecql Base for ecql filter.
         * @property {String} query Base for query filter.
         * @property {String} functions Base for function filter.
         *
         */
        baseFilter: {},

        /**
         * An object containing a custom filter.
         * If not empty the other filters are not sent to the server.
         *
         * @type {Object}
         *
         */
        custom: {}
    },

    /**
     * Class constructor.
     *
     * @private
     *
     * @param {Object} config
     */
    constructor: function (config) {
        config = config || {};
        var me = this;
        // decode config
        var newconfig = me.decodeFilter(config);
        // decode base filter
        if (config.baseFilter) {
            newconfig.baseFilter = me.decodeFilter(config.baseFilter);
        }

        //https://docs.sencha.com/extjs/6.2.0/classic/Ext.mixin.Observable.html
        this.mixins.observable.constructor.call(this, config);

        // init configs
        this.initConfig(newconfig);
    },

    /**
     * Convert the advanced filter to String, as expected by the server.
     *
     * @returns {String}
     *
     * @example
     * var advancedFilter = new CMDBuildUI.util.AdvancedFilter(view.getFilter());
     * advancedFilter.addAttributeFilter("Number", "equal", "125");
     * var filter = advancedFilter.encode();
     */
    encode: function () {
        var filter = {};

        // returns the custom filter if it is not empty
        if (!Ext.Object.isEmpty(this._custom)) {
            return Ext.JSON.encode(this._custom);
        }

        // attribute
        var attrsbasefilter = this._baseFilter.attributes || {},
            attrsfilter = [];

        if (!Ext.Object.isEmpty(this._attributesCustom)) {
            attrsfilter.push(this._attributesCustom);
        }
        if (!Ext.Object.isEmpty(this._baseFilter.attributesCustom)) {
            attrsfilter.push(this._baseFilter.attributesCustom);
        }
        if (!Ext.Object.isEmpty(attrsbasefilter) || !Ext.Object.isEmpty(this._attributes)) {
            var attributes = [];
            function buildFilter(filter) {
                for (var a in filter) {
                    var af = [];
                    filter[a].forEach(function (attribute) {
                        af.push({
                            simple: {
                                attribute: a,
                                operator: attribute.operator,
                                value: attribute.value
                            }
                        });
                    });
                    if (af.length === 1) {
                        attributes.push(af[0]);
                    } else {
                        attributes.push({
                            or: af
                        });
                    }
                }
            }
            buildFilter(attrsbasefilter);
            buildFilter(this._attributes);

            if (attributes.length === 1) {
                attrsfilter.push(attributes[0]);
            } else {
                attrsfilter.push({
                    and: attributes
                });
            }
        }
        if (attrsfilter.length) {
            filter.attribute = attrsfilter.length === 1 ? attrsfilter[0] : { and: attrsfilter };
        }

        // relation
        var relfilter = Ext.merge({}, this._relations || {}, this._baseFilter.relations || {});
        if (!Ext.Object.isEmpty(relfilter)) {
            var relations = [];
            for (var domain in relfilter) {
                relations.push(
                    Ext.apply(
                        {
                            domain: domain
                        },
                        relfilter[domain]
                    )
                );
            }
            filter.relation = relations;
        }

        // ecql
        if (!Ext.Object.isEmpty(this._ecql)) {
            filter.ecql = this._ecql;
        } else if (this._baseFilter && !Ext.Object.isEmpty(this._baseFilter.ecql)) {
            filter.ecql = this._baseFilter.ecql;
        }

        // cql
        if (!Ext.Object.isEmpty(this._cql)) {
            filter.cql = this._cql;
        } else if (this._baseFilter && !Ext.Object.isEmpty(this._baseFilter.cql)) {
            filter.cql = this._baseFilter.cql;
        }

        // query
        var query = this._query || this._baseFilter.query;
        if (!Ext.isEmpty(query)) {
            filter.query = query;
        }

        // attachments
        var attachmentsquery = this._attachmentsQuery || this._baseFilter.attachmentsQuery;
        if (!Ext.isEmpty(attachmentsquery)) {
            filter.attachment = {
                query: attachmentsquery
            };
        }

        var attachmentsmeta = Ext.merge({}, this._attachmemtsMeta || {}, this._baseFilter.attachmemtsMeta || {});
        if (!Ext.Object.isEmpty(attachmentsmeta)) {
            filter.attachment = filter.attachment || {};
            Ext.merge(
                filter.attachment,
                CMDBuildUI.util.helper.FiltersHelper.encodeAttachmentsMetadataFilter(
                    attachmentsmeta.attributes,
                    attachmentsmeta.categories
                )
            );
        }

        // functions
        var functionName = this._functions || this._baseFilter.functions;
        if (!Ext.isEmpty(functionName)) {
            filter.functions = [
                {
                    name: functionName
                }
            ];
        }

        // return filter
        return !Ext.Object.isEmpty(filter) ? Ext.JSON.encode(filter) : null;
    },

    /**
     * Add an attribute filter to the advanced filter.
     *
     * @param {String} attribute The object attribute to use for filter data.
     * @param {String} operator One of {@link module:CMDBuildUI.util.helper.FiltersHelper#operators CMDBuildUI.util.helper.FiltersHelper.operators} properties.
     * @param {*} value
     * @param {string} [attributeId]
     *
     * @example
     * filter.addAttributeFilter("ParentProc", CMDBuildUI.util.helper.FiltersHelper.operators.null, null);
     */
    addAttributeFilter: function (attribute, operator, value, attributeId) {
        if (!this._attributes[attribute]) {
            this._attributes[attribute] = [];
        }
        if (!Ext.isArray(value)) {
            value = [value];
        }

        var f = {
            operator: operator,
            value: value
        };

        if (attributeId) {
            f.attributeId = attributeId;
        }

        this._attributes[attribute].push(f);

        this.fireEventArgs('change', [this]);
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.AdvancedFilter#removeAttributeFilter CMDBuildUI.util.AdvancedFilter.removeAttributeFilter()} instead.
     */
    removeAttributeFitler: function (attribute, attributeId) {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.AdvancedFilter.removeAttributeFitler() is deprecated. Use CMDBuildUI.util.AdvancedFilter.removeAttributeFilter() instead.'
        );
        //</debug>
        this.removeAttributeFilter(attribute, attributeId);
    },

    /**
     * Remove an attribute filter from the advanced filter.
     *
     * @param {String} attribute The object attribute to remove from filter.
     * @param {String} [attributeId]
     *
     * @example
     * filter.removeAttributeFilter('status');
     */
    removeAttributeFilter: function (attribute, attributeId) {
        if (this._attributes[attribute]) {
            if (attributeId) {
                var replacedAttributes = [];
                this._attributes[attribute].forEach(function (singleAttribute) {
                    if (singleAttribute.attributeId !== attributeId) {
                        replacedAttributes.push(singleAttribute);
                    }
                });

                if (replacedAttributes.length) {
                    this._attributes[attribute] = replacedAttributes;
                } else {
                    delete this._attributes[attribute];
                }
            } else {
                delete this._attributes[attribute];
            }
        }

        this.fireEventArgs('change', [this]);
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.AdvancedFilter#clearAttributesFilter CMDBuildUI.util.AdvancedFilter.clearAttributesFilter()} instead.
     */
    clearAttributesFitler: function () {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.AdvancedFilter.clearAttributesFitler() is deprecated. Use CMDBuildUI.util.AdvancedFilter.clearAttributesFilter() instead.'
        );
        //</debug>
        this.clearAttributesFilter();
    },

    /**
     * Clear all attributes filter from the advanced filter.
     *
     * @example
     * advancedFilter.clearAttributesFilter();
     */
    clearAttributesFilter: function () {
        this._attributes = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Add attributes custom filter.
     * @param {Object} filter The custom filter configuation.
     *
     * @example
     * advancedFilter.addAttributesCustomFilter({
     *      and: [
     *          {attribute: 'Status', operator: 'equal', value: [5563]},
     *          {attribute: 'BeginDate', operator: 'less', value: ['2022-01-01']}
     *      ]
     * });
     */
    addAttributesCustomFilter: function (filter) {
        this._attributesCustom = filter;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear attributes custom filter from the advanced filter.
     *
     * @example
     * advancedFilter.clearAttributesCustomFilter();
     */
    clearAttributesCustomFilter: function () {
        this._attributesCustom = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Add a relation filter to the advanced filter.
     *
     * @param {String} domain Domain name.
     * @param {String} source Class/Process name.
     * @param {String} destination Class/Process name.
     * @param {String} direction One of `_1` or `_2`
     * @param {String} type One of CMDBuildUI.util.helper.FiltersHelper.relationstypes options.
     * @param {RelationCard[]} [cards] Array of cards. Required if type is `oneof`.
     * @param {Object} [filter] Attributes filter. Required if type is `fromfilter`.
     *
     * @example
     * filter.addRelationFilter(
     *      domain.get("name"),
     *      domain.get("source"),
     *      domain.get("destination"),
     *      '_1',
     *      'oneof',
     *      [{
     *          className: 'Employee',
     *          id: 5238
     *      }]
     * );
     */
    addRelationFilter: function (domain, source, destination, direction, type, cards, filter) {
        this._relations[domain] = {
            source: source,
            destination: destination,
            direction: direction,
            type: type
        };
        if (!Ext.isEmpty(cards)) {
            this._relations[domain].cards = cards;
        }
        if (!Ext.isEmpty(filter)) {
            this._relations[domain].filter = filter;
        }

        this.fireEventArgs('change', [this]);
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.AdvancedFilter#removeRelationFilter CMDBuildUI.util.AdvancedFilter.removeRelationFilter()} instead.
     */
    removeRelationFitler: function (domain) {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.AdvancedFilter.removeRelationFitler() is deprecated. Use CMDBuildUI.util.AdvancedFilter.removeRelationFilter() instead.'
        );
        //</debug>
        this.removeRelationFilter(domain);
    },

    /**
     * Remove a relation filter from the advanced filter.
     *
     * @param {String} domain Domain name.
     *
     * @example
     * filter.removeRelationFilter(domain.get("name"));
     */
    removeRelationFilter: function (domain) {
        if (this._relations[domain]) {
            delete this._relations[domain];
        }

        this.fireEventArgs('change', [this]);
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.AdvancedFilter#clearRelationsFilter CMDBuildUI.util.AdvancedFilter.clearRelationsFilter()} instead.
     */
    clearRelationsFitler: function () {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.AdvancedFilter.clearRelationsFitler() is deprecated. Use CMDBuildUI.util.AdvancedFilter.clearRelationsFilter() instead.'
        );
        //</debug>
        this.clearRelationsFilter();
    },

    /**
     * Clear relations filter from the advanced filter.
     *
     * @example
     * filter.clearRelationsFilter();
     */
    clearRelationsFilter: function () {
        this._relations = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Add attachments filter to the advanced filter.
     *
     * @param {Object} attachment
     * @param {String} attachment.query
     *
     * @example
     * filter.addAttachmentsFilter({
     *      query: 'planimetry'
     * });
     */
    addAttachmentsFilter: function (attachment) {
        if (!Ext.Object.isEmpty(attachment) && attachment.query) {
            this._attachmentsQuery = attachment.query;
        }
        this.fireEventArgs('change', [this]);
    },

    /**
     * Add attachments query filter to the advanced filter.
     *
     * @param {String} query
     *
     * @example
     * filter.addAttachmentsQueryFilter('planimetry');
     */
    addAttachmentsQueryFilter: function (query) {
        if (query) {
            this._attachmentsQuery = query;
        }
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear attachments filter from the advanced filter.
     *
     * @example
     * filter.clearAttachmentsFilter();
     */
    clearAttachmentsFilter: function () {
        this.clearAttachmentsQueryFilter();
        this.clearAttachmentsMeta();
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear attachments query filter from the advanced filter.
     *
     * @example
     * filter.clearAttachmentsQueryFilter();
     */
    clearAttachmentsQueryFilter: function () {
        this._attachmentsQuery = null;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear attachments meta filter from the advanced filter.
     *
     * @example
     * filter.clearAttachmentsMeta();
     */
    clearAttachmentsMeta: function () {
        this._attachmemtsMeta = null;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Set ecql filter to the advanced filter.
     *
     * @param {Ecql} ecql
     *
     * @example
     * store.getAdvancedFilter().addEcqlFilter(field.getEcqlFilter());
     */
    addEcqlFilter: function (ecql) {
        this._ecql = ecql;
        this.fireEventArgs('change', [this]);
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.AdvancedFilter#clearEcqlFilter CMDBuildUI.util.AdvancedFilter.clearEcqlFilter()} instead.
     */
    clearEcqlFitler: function () {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.AdvancedFilter.clearEcqlFitler() is deprecated. Use CMDBuildUI.util.AdvancedFilter.clearEcqlFilter() instead.'
        );
        //</debug>
        this.clearEcqlFilter();
    },

    /**
     * Clear ecql filter from the advanced filter.
     *
     * @example
     * filter.clearEcqlFilter();
     */
    clearEcqlFilter: function () {
        this._ecql = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Set cql filter to the advanced filter.
     *
     * @deprecated
     *
     * @param {String} cql
     */
    addCqlFilter: function (cql) {
        this._cql = cql;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear cql filter from the advanced filter.
     *
     * @deprecated
     */
    clearCqlFitler: function () {
        this._cql = null;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Set query filter to the advanced filter.
     *
     * @param {String} query
     *
     * @example
     * store.getAdvancedFilter().addQueryFilter(searchTerm);
     */
    addQueryFilter: function (query) {
        this._query = query;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear query filter from the advanced filter.
     *
     * @example
     * store.getAdvancedFilter().clearQueryFilter();
     */
    clearQueryFilter: function () {
        this._query = null;
        this.fireEventArgs('change', [this]);
    },

    /**
     * Set function filter to the advanced filter.
     *
     * @param {String} fnName
     *
     * @example
     * store.getAdvancedFilter().addFunctionFilter(fnName);
     */
    addFunctionFilter: function (fnName) {
        this._functions = fnName;
    },

    /**
     * Clear function filter from the advanced filter.
     *
     * @example
     * store.getAdvancedFilter().clearFunctionFilter();
     */
    clearFunctionFilter: function () {
        this._functions = null;
    },

    /**
     * Add base filter to the advanced filter.
     *
     * @param {Object} filter
     * @param {Object} filter.attributes Base for attributes filter.
     * @param {Object} filter.relations Base for relations filter.
     * @param {Object} filter.ecql Base for ecql filter.
     * @param {Object} filter.query Base for query filter.
     *
     * @example
     * store.getAdvancedFilter().addBaseFilter({
     *      attributes: {
     *          FlowStatus: [{
     *              operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
     *              value: [vm.get("flowStatus").findRecord("code", "open.running").getId()]
     *          }]
     *      }
     * });
     */
    addBaseFilter: function (filter) {
        this._baseFilter = this.decodeFilter(filter);
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear query filter from the advanced filter.
     *
     * @example
     * store.getAdvancedFilter().clearBaseFilter();
     */
    clearBaseFilter: function () {
        this._baseFilter = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Add custom filter to the advanced filter.
     * If not empty the other filters are not sent to the server.
     *
     * @param {Object|String} filter
     *
     * @example
     * store.getAdvancedFilter().addCustomFilter({
     *      attribute: {
     *          or: [{
     *              simple: {
     *                  attribute: 'Attribute1',
     *                  operator: 'equal',
     *                  value: [123]
     *              }
     *          }, {
     *              simple: {
     *                  attribute: 'MyAttribute2',
     *                  operator: 'equal',
     *                  value: [456]
     *              }
     *          }]
     *      }
     * });
     */
    addCustomFilter: function (filter) {
        if (Ext.isString(filter)) {
            try {
                filter = Ext.JSON.decode(filter);
            } catch (e) {
                filter = null;
            }
        }
        if (Ext.isObject(filter)) {
            this._custom = filter;
        }
        this.fireEventArgs('change', [this]);
    },

    /**
     * Clear custom filter from the advanced filter.
     *
     * @example
     * store.getAdvancedFilter().clearCustomFilter();
     */
    clearCustomFilter: function () {
        this._custom = {};
        this.fireEventArgs('change', [this]);
    },

    /**
     * Returns true if the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var params = {};
     * if (!filter.isEmpty()) {
     *      params.filter = filter.encode();
     * }
     */
    isEmpty: function () {
        return (
            this.isBaseFilterEmpty() &&
            this.isAttributesFilterEmpty() &&
            this.isAttachmentsFilterEmpty() &&
            this.isAttributesCustomFilterEmpty() &&
            this.isFunctionFilterEmpty() &&
            this.isCustomFilterEmpty() &&
            Ext.Object.isEmpty(this._relations) &&
            Ext.Object.isEmpty(this._ecql) &&
            Ext.Object.isEmpty(this._cql) &&
            Ext.isEmpty(this._query)
        );
    },

    /**
     * Returns true if the base filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isBaseFilterEmpty();
     */
    isBaseFilterEmpty: function () {
        return Ext.Object.isEmpty(this._baseFilter);
    },

    /**
     * Returns true if the attributes filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isAttributesFilterEmpty();
     */
    isAttributesFilterEmpty: function () {
        return Ext.Object.isEmpty(this._attributes);
    },

    /**
     * Returns true if the attributes custom filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isAttributesCustomFilterEmpty();
     */
    isAttributesCustomFilterEmpty: function () {
        return Ext.Object.isEmpty(this._attributesCustom);
    },

    /**
     * Returns true if the attachments filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isAttachmentsFilterEmpty();
     */
    isAttachmentsFilterEmpty: function () {
        return Ext.isEmpty(this._attachmentsQuery) && Ext.Object.isEmpty(this._attachmemtsMeta);
    },

    /**
     * Returns true if the function filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isFunctionFilterEmpty();
     */
    isFunctionFilterEmpty: function () {
        return Ext.isEmpty(this._functions);
    },

    /**
     * Returns true if the custom filter in the advanced filter is empty, false otherwise.
     *
     * @returns {Boolean}
     *
     * @example
     * var empty = filter.isCustomFilterEmpty();
     */
    isCustomFilterEmpty: function () {
        return Ext.Object.isEmpty(this._custom);
    },

    /**
     * Clear the advanced filter.
     *
     * @example
     * store.getAdvancedFilter().clearAdvancedFilter();
     */
    clearAdvancedFilter: function () {
        this.clearAttributesFilter();
        this.clearAttributesCustomFilter();
        this.clearRelationsFilter();
        this.clearAttachmentsFilter();
        this.clearCqlFitler();
        this.clearEcqlFilter();
        this.clearQueryFilter();
        this.clearFunctionFilter();
        this.clearCustomFilter();
    },

    /**
     * Clear filter in management.
     *
     * @example
     * store.getAdvancedFilter().clearManagementFilter();
     */
    clearManagementFilter: function () {
        this.clearAttributesCustomFilter();
        this.clearRelationsFilter();
        this.clearFunctionFilter();
        this.clearAttachmentsFilter();
    },

    /**
     * Apply to the filter the configuration of a filter definition.
     *
     * @param {Object|String} filter An object with advanced filter structure.
     *
     * @example
     * // apply saved filter
     * store.getAdvancedFilter().applyAdvancedFilter(filter.get("configuration"));
     */
    applyAdvancedFilter: function (filter) {
        if (filter.baseFilter) {
            this.addBaseFilter(filter.baseFilter);
        }

        var decoded = this.decodeFilter(filter);

        if (decoded.attributes) {
            this._attributes = Ext.merge(this._attributes, decoded.attributes);
        }

        if (decoded.attributesCustom) {
            this._attributesCustom = decoded.attributesCustom;
        }

        if (decoded.relations) {
            this._relations = Ext.merge(this._relations, decoded.relations);
        }

        if (decoded.ecql) {
            this._ecql = decoded.ecql;
        }

        if (decoded.cql) {
            this._cql = decoded.cql;
        }

        if (decoded.query) {
            this._query = decoded.query;
        }

        if (decoded.attachmentsQuery) {
            this._attachmentsQuery = decoded.attachmentsQuery;
        }

        if (decoded.attachmemtsMeta) {
            this._attachmemtsMeta = decoded.attachmemtsMeta;
        }

        if (decoded.functions) {
            this._functions = decoded.functions;
        }

        if (decoded.custom) {
            this._custom = decoded.custom;
        }

        this.fireEventArgs('change', [this]);
    },

    privates: {
        /**
         * Decode a filter.
         * @param {Object|String} filter
         */
        decodeFilter: function (filter) {
            var newfilter = {};
            if (Ext.isString(filter)) {
                // Try to convert string to object. If the string is not an object
                // the string will be used as query filter.
                try {
                    filter = Ext.JSON.decode(filter);
                } catch (e) {
                    filter = {
                        query: filter
                    };
                }
            }

            filter = filter || {};

            if (filter.custom) {
                newfilter.custom = filter.custom;
                return newfilter;
            }

            // query
            if (filter.query) {
                newfilter.query = filter.query;
            }

            // ecql
            if (filter.ecql) {
                newfilter.ecql = filter.ecql;
            }

            // ecql
            if (filter.cql) {
                newfilter.cql = filter.cql;
            }

            // attributes
            if (filter.attribute && (filter.attribute.simple || filter.attribute.and || filter.attribute.or)) {
                newfilter.attributesCustom = filter.attribute;
            } else if (filter.attribute || filter.attributes) {
                newfilter.attributes = {};
                var attributes = filter.attribute || filter.attributes;
                for (var attr in attributes) {
                    if (Ext.isArray(attributes[attr])) {
                        newfilter.attributes[attr] = attributes[attr];
                    } else if (Ext.isObject(attributes[attr])) {
                        newfilter.attributes[attr] = [attributes[attr]];
                    }
                }
            }
            if (filter.attributesCustom) {
                newfilter.attributesCustom = filter.attributesCustom;
            }

            // relations
            if (filter.relation) {
                if (Ext.isArray(filter.relation)) {
                    newfilter.relations = {};
                    filter.relation.forEach(function (r) {
                        newfilter.relations[r.domain] = {
                            source: r.source,
                            destination: r.destination,
                            direction: r.direction,
                            type: r.type
                        };
                        if (r.cards) {
                            newfilter.relations[r.domain].cards = r.cards;
                        }
                        if (r.filter) {
                            newfilter.relations[r.domain].filter = r.filter;
                        }
                    });
                } else if (Ext.isObject(filter.relation)) {
                    newfilter.relations = filter.relation;
                }
            } else if (filter.relations) {
                newfilter.relations = filter.relations;
            }

            // attachments
            if (filter.attachment) {
                if (filter.attachment.query) {
                    newfilter.attachmentsQuery = filter.attachment.query;
                }
                if (filter.attachment.attribute || filter.attachment.composite) {
                    newfilter.attachmemtsMeta = CMDBuildUI.util.helper.FiltersHelper.decodeAttachmentsMetadataFilter(
                        filter.attachment
                    );
                }
            }

            // functions
            if (filter.functions) {
                if (Ext.isArray(filter.functions)) {
                    if (filter.functions.length) {
                        newfilter.functions = filter.functions[0].name;
                    }
                } else {
                    newfilter.functions = filter.functions;
                }
            }

            return newfilter;
        }
    }
});
