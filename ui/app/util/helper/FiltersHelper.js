/**
 * @file CMDBuildUI.util.helper.FiltersHelper
 * @module CMDBuildUI.util.helper.FiltersHelper
 * @author PAT srl
 * @access public
 */

/**
 * @typedef Operator
 * @type {Object}
 * @property {String} value Operator value. One of {@link CMDBuildUI.model.base.Filter#operators CMDBuildUI.util.helper.FiltersHelper.operators}.
 * @property {String} label Operator description.
 * @property {String[]} availablefor List of {@link CMDBuildUI.util.helper.ModelHelper#cmdbuildtypes CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes}
 * for which this operator is available.
 *
 *
 */
Ext.define('CMDBuildUI.util.helper.FiltersHelper', {
    singleton: true,

    /**
     * @constant {Object} operators Available filter operators.
     * @property {String} equal
     * @property {String} notequal
     * @property {String} null
     * @property {String} notnull
     * @property {String} greater
     * @property {String} less
     * @property {String} between
     * @property {String} contain
     * @property {String} notcontain
     * @property {String} begin
     * @property {String} notbegin
     * @property {String} end
     * @property {String} notend
     * @property {String} netcontains
     * @property {String} netcontained
     * @property {String} netcontainsorequal
     * @property {String} netcontainedorequal
     * @property {String} overlap
     * @property {String} notoverlap
     * @property {String} in
     * @property {String} description_contains
     * @property {String} description_notcontain
     * @property {String} description_begin
     * @property {String} description_notbegin
     * @property {String} description_end
     * @property {String} description_notend
     *
     * @example
     * var eq = CMDBuildUI.util.helper.FiltersHelper.operators.equal;
     */
    operators: {
        equal: 'equal',
        notequal: 'notequal',
        null: 'isnull',
        notnull: 'isnotnull',
        greater: 'greater',
        less: 'less',
        between: 'between',
        contain: 'contain',
        notcontain: 'notcontain',
        begin: 'begin',
        notbegin: 'notbegin',
        end: 'end',
        notend: 'notend',
        netcontains: 'net_contains',
        netcontained: 'net_contained',
        netcontainsorequal: 'net_containsorequal',
        netcontainedorequal: 'net_containedorequal',
        overlap: 'overlap',
        notoverlap: 'notoverlap',
        in: 'in',
        description_contains: 'description_contains',
        description_notcontain: 'description_notcontain',
        description_begin: 'description_begin',
        description_notbegin: 'description_notbegin',
        description_end: 'description_end',
        description_notend: 'description_notend'
    },

    /**
     * @constant {Object} blocksoperators Available filter blocks operators.
     * @property {String} and
     * @property {String} or
     *
     * @example
     * var and = CMDBuildUI.util.helper.FiltersHelper.blocksoperators.and;
     */
    blocksoperators: {
        and: 'and',
        or: 'or'
    },

    /**
     * @type {Object}
     * @property {String} runtime
     * @property {String} fixed
     *
     * @example
     * var type = CMDBuildUI.util.helper.FiltersHelper.parameterstypes.fixed;
     */
    parameterstypes: {
        runtime: 'runtime',
        fixed: 'fixed'
    },

    /**
     * @type {Object}
     * @property {String} any
     * @property {String} noone
     * @property {String} oneof
     * @property {String} fromfilter
     *
     * @example
     * var type = CMDBuildUI.util.helper.FiltersHelper.relationstypes.any;
     */
    relationstypes: {
        any: 'any',
        noone: 'noone',
        oneof: 'oneof',
        fromfilter: 'fromfilter'
    },

    /**
     * @type {Object}
     * @property {String} ignore
     * @property {String} migrates
     * @property {String} clone
     *
     * @example
     * var clone = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore;
     */
    cloneFilters: {
        ignore: 'ignore',
        migrates: 'migrates',
        clone: 'clone'
    },

    /**
     * @deprecated
     * Use {@link CMDBuildUI.util.module.FiltersHelper#isOperatorForReferenceOrLookupDescription CMDBuildUI.util.FiltersHelper.isOperatorForReferenceOrLookupDescription()} instead.
     */
    isOperatorForRefernceOrLookupDescription: function (operator) {
        //<debug>
        Ext.log.warn(
            'CMDBuildUI.util.FiltersHelper.isOperatorForRefernceOrLookupDescription() is deprecated. Use CMDBuildUI.util.FiltersHelper.isOperatorForReferenceOrLookupDescription() instead.'
        );
        //</debug>
        return this.isOperatorForReferenceOrLookupDescription(operator);
    },

    /**
     * Return a boolean that indicate if operator is used in reference or lookup description
     *
     * @param {String} operator One of CMDBuildUI.util.helper.FiltersHelper.operators
     * @returns {Boolean}
     */
    isOperatorForReferenceOrLookupDescription: function (operator) {
        return (
            [
                this.operators.description_contains,
                this.operators.description_notcontain,
                this.operators.description_begin,
                this.operators.description_notbegin,
                this.operators.description_end,
                this.operators.description_notend
            ].indexOf(operator) > -1
        );
    },

    /**
     * Returns the friendly description for the operators.
     *
     * @param {String} operator One of CMDBuildUI.util.helper.FiltersHelper.operators
     * @returns {String}
     */
    getOperatorDescription: function (operator) {
        switch (operator) {
            case CMDBuildUI.util.helper.FiltersHelper.operators.begin:
                return CMDBuildUI.locales.Locales.filters.operators.beginswith;
            case CMDBuildUI.util.helper.FiltersHelper.operators.between:
                return CMDBuildUI.locales.Locales.filters.operators.between;
            case CMDBuildUI.util.helper.FiltersHelper.operators.contain:
                return CMDBuildUI.locales.Locales.filters.operators.contains;
            case CMDBuildUI.util.helper.FiltersHelper.operators.end:
                return CMDBuildUI.locales.Locales.filters.operators.endswith;
            case CMDBuildUI.util.helper.FiltersHelper.operators.equal:
            case CMDBuildUI.util.helper.FiltersHelper.operators.overlap:
                return CMDBuildUI.locales.Locales.filters.operators.equals;
            case CMDBuildUI.util.helper.FiltersHelper.operators.greater:
                return CMDBuildUI.locales.Locales.filters.operators.greaterthan;
            case CMDBuildUI.util.helper.FiltersHelper.operators.less:
                return CMDBuildUI.locales.Locales.filters.operators.lessthan;
            case CMDBuildUI.util.helper.FiltersHelper.operators.netcontained:
                return CMDBuildUI.locales.Locales.filters.operators.contained;
            case CMDBuildUI.util.helper.FiltersHelper.operators.netcontainedorequal:
                return CMDBuildUI.locales.Locales.filters.operators.containedorequal;
            case CMDBuildUI.util.helper.FiltersHelper.operators.netcontains:
                return CMDBuildUI.locales.Locales.filters.operators.contains;
            case CMDBuildUI.util.helper.FiltersHelper.operators.netcontainsorequal:
                return CMDBuildUI.locales.Locales.filters.operators.containsorequal;
            case CMDBuildUI.util.helper.FiltersHelper.operators.notbegin:
                return CMDBuildUI.locales.Locales.filters.operators.doesnotbeginwith;
            case CMDBuildUI.util.helper.FiltersHelper.operators.notcontain:
                return CMDBuildUI.locales.Locales.filters.operators.doesnotcontain;
            case CMDBuildUI.util.helper.FiltersHelper.operators.notend:
                return CMDBuildUI.locales.Locales.filters.operators.doesnotendwith;
            case CMDBuildUI.util.helper.FiltersHelper.operators.notequal:
            case CMDBuildUI.util.helper.FiltersHelper.operators.notoverlap:
                return CMDBuildUI.locales.Locales.filters.operators.different;
            case CMDBuildUI.util.helper.FiltersHelper.operators.notnull:
                return CMDBuildUI.locales.Locales.filters.operators.isnotnull;
            case CMDBuildUI.util.helper.FiltersHelper.operators.null:
                return CMDBuildUI.locales.Locales.filters.operators.isnull;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_contains:
                return CMDBuildUI.locales.Locales.filters.operators.descriptioncontains;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_notcontain:
                return CMDBuildUI.locales.Locales.filters.operators.descriptionnotcontain;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_begin:
                return CMDBuildUI.locales.Locales.filters.operators.descriptionbegin;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_notbegin:
                return CMDBuildUI.locales.Locales.filters.operators.descriptionnotbegin;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_end:
                return CMDBuildUI.locales.Locales.filters.operators.descriptionends;
            case CMDBuildUI.util.helper.FiltersHelper.operators.description_notend:
                return CMDBuildUI.locales.Locales.filters.operators.descriptionnotends;
        }
    },

    /**
     * @private
     *
     * @param {Object[]} runtimeattrs
     * @param {viewmodel.classes-cards-grid-container} viewmodel
     *
     * @returns {Ext.form.Field[]}
     */
    getFormForRuntimeAttributes: function (runtimeattrs, viewmodel) {
        var me = this;
        var fields = [];
        var vm = viewmodel;
        var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(vm.get('objectType'), vm.get('objectTypeName'));
        var model = Ext.ClassManager.get(modelName);
        runtimeattrs.forEach(function (a) {
            var field = model.getField(a.attribute);
            var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);

            editor.fieldLabel = Ext.String.format(
                '{0} - {1}',
                field.attributeconf.description_localized,
                me.getOperatorDescription(a.operator)
            );
            editor._tempid = a._tempid;
            editor.allowBlank = false;

            var container = {
                xtype: 'fieldcontainer',
                layout: 'anchor',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                items: [editor]
            };

            if (a.operator === CMDBuildUI.util.helper.FiltersHelper.operators.between) {
                container.items.push(
                    Ext.applyIf(
                        {
                            fieldLabel: '',
                            _tempid: a._tempid + '-v2'
                        },
                        editor
                    )
                );
            }
            fields.push(container);
        });
        return fields;
    },

    /**
     * @private
     *
     * @param {CMDBuild.model.base.Filter} filter
     * @param {viewmodel.classes-cards-grid-container} viewmodel
     */
    applyFilter: function (filter, viewmodel) {
        var deferred = new Ext.Deferred();
        // check runtime attributes
        var runtimeattrs = [];
        var objfilter = typeof filter == 'string' ? JSON.parse(filter) : filter;

        function checkRuntime(v) {
            if (v.parameterType === CMDBuildUI.util.helper.FiltersHelper.parameterstypes.runtime) {
                v._tempid = Ext.String.format(
                    '{0}-{1}',
                    v.attribute,
                    Ext.String.leftPad(Ext.Number.randomInt(0, 9999), 4, '0')
                );
                runtimeattrs.push(v);
            }
        }

        CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(objfilter, checkRuntime);
        if (runtimeattrs.length > 0) {
            var popup;
            var form = {
                xtype: 'form',
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                scrollable: true,
                items: this.getFormForRuntimeAttributes(runtimeattrs, viewmodel),
                listeners: {
                    beforedestroy: function (form) {
                        form.removeAll(true);
                    },
                    afterrender: function (form) {
                        form.isValid();
                    }
                },
                buttons: [
                    {
                        text: CMDBuildUI.locales.Locales.common.actions.apply,
                        formBind: true,
                        ui: 'management-primary-small',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
                        },
                        handler: function (button, e) {
                            var fields = {};
                            var form = button.up('form');
                            form.getForm()
                                .getFields()
                                .getRange()
                                .forEach(function (f) {
                                    fields[f._tempid] = f;
                                });

                            function updateRuntimeValues(f) {
                                if (f.parameterType === 'runtime') {
                                    f.value = [];
                                    var v = fields[f._tempid].getValue();
                                    if (!Ext.isEmpty(v)) {
                                        f.value.push(v);
                                    }
                                    if (
                                        f.operator === CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                                        fields[f._tempid + '-v2'].getValue()
                                    ) {
                                        f.value.push(fields[f._tempid + '-v2'].getValue());
                                    }
                                    delete f._tempid;
                                }
                            }
                            CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(objfilter, updateRuntimeValues);
                            deferred.resolve(objfilter, true);
                            popup.destroy();
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        ui: 'secondary-action-small',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                        },
                        handler: function (button, e) {
                            popup.destroy();
                        }
                    }
                ]
            };
            popup = CMDBuildUI.util.Utilities.openPopup(
                null,
                '',
                form,
                {},
                {
                    width: '40%',
                    height: '40%',
                    alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1
                }
            );
        } else {
            deferred.resolve(objfilter, true);
        }

        return deferred.promise;
    },

    /**
     * Check advanced filter validity
     *
     * @param {CMDBuildUI.util.AdvancedFilter} advancedFilter
     * @returns {Boolean}
     *
     * @example
     * var store = view.getStore();
     * if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(store.getAdvancedFilter()) {
     *      store.load();
     * }
     */
    validityCheckFilter: function (advancedFilter) {
        // deve esserci almeno un attributo per ogni attributo, se l'operatore è uno tra uguale, diverso, maggiore, minore o compreso e non è selezionato
        // il flag "parametro di input" e non si tratta di un campo di tipo stringa o testo, allora il valore non può essere vuoto.
        // Controllare che per i campi di tipo stringa e testo venga inviata una stringa vuota e non null.
        var attributesValidity = true,
            attributesCustomValidity = true,
            relationValidity = true,
            attachmentsValidity = true,
            functionsValidity = !advancedFilter.isFunctionFilterEmpty(),
            attributes = advancedFilter.getAttributes(),
            attributesCustom = advancedFilter.getAttributesCustom(),
            relations = advancedFilter.getRelations();

        if (!Ext.Object.isEmpty(attributes)) {
            attributesValidity = this.validateAttributes(attributes);
        }

        if (!Ext.Object.isEmpty(attributesCustom)) {
            attributesCustomValidity = this.validateAttributesCustom(attributesCustom);
        }

        if (!Ext.Object.isEmpty(relations)) {
            relationValidity = this.validateRelations(relations);
        }

        attachmentsValidity = this.validateAttachmentsFilter(advancedFilter);

        return (
            (attributesValidity && relationValidity && attachmentsValidity && attributesCustomValidity) ||
            functionsValidity
        );
    },

    /**
     * Convert filter configuration to filter as expeted by the server.
     *
     * @param {Object} commonAttrs
     * @param {Object} cateogories
     *
     * @returns {Object} Encoded attachments metadata filter.
     *
     * @example
     * var metadataFilter = CMDBuildUI.util.helper.FiltersHelper.encodeAttachmentsMetadataFilter(attrs, cats);
     */
    encodeAttachmentsMetadataFilter: function (commonAttrs, cateogories) {
        var filters = [];

        /**
         *
         * @param {Object[]} attribute
         */
        function encodeAttribute(attrName, values) {
            var afilter;
            if (values.length === 1) {
                afilter = {
                    simple: {
                        attribute: attrName,
                        operator: values[0].operator,
                        parameterType: values[0].parameterType,
                        value: values[0].value
                    }
                };
            } else if (values.length > 1) {
                afilter = {
                    or: []
                };

                values.forEach(function (a) {
                    afilter.or.push({
                        simple: {
                            attribute: attrName,
                            operator: a.operator,
                            parameterType: a.parameterType,
                            value: a.value
                        }
                    });
                });
            }
            return afilter;
        }

        /**
         *
         * @param {Object} attributes
         */
        function encodeAttributes(attributes) {
            var values = [];
            Ext.Object.getKeys(attributes).forEach(function (attrName) {
                var attrValues = attributes[attrName];
                values.push(encodeAttribute(attrName, attrValues));
            });
            if (values.length === 1) {
                return values[0];
            } else if (values.length > 1) {
                return {
                    and: values
                };
            }
        }

        function encodeCategories() {
            var values = [];
            Ext.Object.getKeys(cateogories).forEach(function (category) {
                var catData = cateogories[category];
                // add filter for category
                catData.attributes.Category = [
                    {
                        attribute: 'Category',
                        operator: 'equal',
                        value: [category]
                    }
                ];
                // push value for category
                values.push({
                    composite: {
                        mode: 'and',
                        elements: [
                            {
                                attribute: {
                                    simple: {
                                        attribute: 'IdClass',
                                        operator: 'equal',
                                        value: [catData.model]
                                    }
                                }
                            },
                            {
                                attribute: encodeAttributes(catData.attributes)
                            }
                        ]
                    }
                });
            });

            if (values.length === 1) {
                return values[0];
            } else if (values.length > 1) {
                return {
                    composite: {
                        mode: 'or',
                        elements: values
                    }
                };
            }
        }

        if (!Ext.Object.isEmpty(commonAttrs)) {
            filters.push({
                attribute: encodeAttributes(commonAttrs)
            });
        }
        if (!Ext.Object.isEmpty(cateogories)) {
            filters.push(encodeCategories());
        }

        if (filters.length == 1) {
            return filters[0];
        } else if (filters.length > 1) {
            return {
                composite: {
                    mode: 'and',
                    elements: filters
                }
            };
        }
    },

    /**
     *
     * @param {Object} filter
     * @param {Object} [filter.attribute]
     * @param {Object} [filter.composite]
     */
    decodeAttachmentsMetadataFilter: function (filter) {
        var commonAttrs = {},
            categories = {};

        // extract simple attribute function
        function extractSimple(simple, repo) {
            if (!repo[simple.attribute]) {
                repo[simple.attribute] = [];
            }
            repo[simple.attribute].push({
                operator: simple.operator,
                value: Ext.isArray(simple.value) ? simple.value : [simple.value]
            });
        }

        // extract or operator function
        function extractOr(or, repo) {
            or.forEach(function (option) {
                extractSimple(option.simple, repo);
            });
        }

        // extract and operator
        function extractAnd(and, repo) {
            and.forEach(function (attr) {
                if (attr.or) {
                    extractOr(attr.or, repo);
                } else if (attr.simple) {
                    extractSimple(attr.simple, repo);
                }
            });
        }

        // extract attribute structure
        function extractAttribute(attribute, repo) {
            if (attribute.and) {
                if (!extractCategory(attribute.and)) {
                    extractAnd(attribute.and, repo);
                }
            } else if (attribute.or) {
                extractOr(attribute.or, repo);
            } else if (attribute.simple) {
                extractSimple(attribute.simple, repo);
            }
        }

        // extract category
        function extractCategory(category) {
            if (
                category.length === 2 &&
                category[0].attribute &&
                category[0].attribute.simple &&
                category[0].attribute.simple.attribute === 'IdClass'
            ) {
                var model = category[0].attribute.simple.value[0],
                    catAttrs = Ext.clone(category[1].attribute),
                    catFilter = Ext.Array.findBy(catAttrs.and, function (i) {
                        return i.simple && i.simple.attribute === 'Category';
                    }),
                    cat =
                        catFilter && catFilter.simple && catFilter.simple.value && catFilter.simple.value.length
                            ? catFilter.simple.value[0]
                            : null;

                if (cat) {
                    if (!categories[cat]) {
                        categories[cat] = {
                            model: model,
                            attributes: {}
                        };
                    }
                    // remove category from attributes filter
                    Ext.Array.erase(
                        catAttrs.and, // array
                        Ext.Array.indexOf(catAttrs.and, catFilter), // index
                        1 // number of items to remove
                    );

                    // extract attributes
                    extractAttribute(catAttrs, categories[cat].attributes);
                }
            }
        }

        function extractCategories(cats) {
            if (cats.composite.mode === 'and') {
                extractCategory(cats.composite.elements);
            } else if (cats.composite.mode === 'or') {
                cats.composite.elements.forEach(function (c) {
                    extractCategory(c.composite.elements);
                });
            }
        }

        if (filter && filter.attribute) {
            extractAttribute(filter.attribute, commonAttrs);
        } else if (filter && filter.composite) {
            if (filter.composite.mode === 'and' && filter.composite.elements.length === 2) {
                if (
                    filter.composite.elements[0].attribute &&
                    filter.composite.elements[0].attribute.simple &&
                    filter.composite.elements[0].attribute.simple.attribute === 'IdClass' &&
                    filter.composite.elements[1].attribute
                ) {
                    extractCategories(filter);
                } else {
                    extractAttribute(filter.composite.elements[0].attribute, commonAttrs);
                    extractCategories(filter.composite.elements[1]);
                }
            } else if (filter.composite.mode === 'or') {
                extractCategories(filter);
            }
        }

        return {
            attributes: commonAttrs,
            categories: categories
        };
    },

    /**
     * Returns the list of available operators for Attributes filter.
     *
     * @returns {Operator[]}
     *
     * @example
     * var operators = CMDBuildUI.util.helper.FiltersHelper.getFilterOperators();
     */
    getFilterOperators: function () {
        return [
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.overlap,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.overlap
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notoverlap,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notoverlap
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.equal
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant,
                    'dmscategory'
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notequal,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notequal
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant,
                    'dmscategory'
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.null,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.null
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notnull,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.greater,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.greater
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.less,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.less
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.between,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.between
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.contain,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.contain
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_contains,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_contains
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notcontain,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_notcontain
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_begin,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_begin
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notbegin,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_notbegin
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_end,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_end
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notend,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.description_notend
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notcontain,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notcontain
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.begin,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.begin
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notbegin,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notbegin
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.end,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.end
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.notend,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.notend
                ),
                availablefor: [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                ]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontains,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.netcontains
                ),
                availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontained,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.netcontained
                ),
                availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontainsorequal,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.netcontainsorequal
                ),
                availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontainedorequal,
                label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                    CMDBuildUI.util.helper.FiltersHelper.operators.netcontainedorequal
                ),
                availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
            }
        ];
    },

    /**
     * @param {Ext.data.Store} store Add group operators
     */
    addOperators: function (store) {
        store.add([
            {
                value: CMDBuildUI.util.helper.FiltersHelper.blocksoperators.and,
                operator: true,
                label: CMDBuildUI.locales.Locales.filters.operators.and,
                group: ' ' + CMDBuildUI.locales.Locales.filters.operator
            },
            {
                value: CMDBuildUI.util.helper.FiltersHelper.blocksoperators.or,
                operator: true,
                label: CMDBuildUI.locales.Locales.filters.operators.or,
                group: ' ' + CMDBuildUI.locales.Locales.filters.operator
            }
        ]);
    },

    /**
     *
     * @param {*} view
     * @param {Object} attribute
     */
    populateAttributeContainer: function (view, attribute) {
        var vm = view.getViewModel(),
            controllerView = view.getController();

        function addFilterRow(v) {
            var _filter = {
                attribute: v.attribute,
                operator: v.operator,
                typeinput: v.parameterType === CMDBuildUI.util.helper.FiltersHelper.parameterstypes.runtime,
                value1: (Ext.isArray(v.value) && v.value[0]) || null,
                value2: (Ext.isArray(v.value) && v.value[1]) || null
            };
            // set optional arguments if isReference && isUserOrGroup && areCurrentFieldsAllowed
            var isReference, isLookup, isUserOrGroup, areCurrentFieldsAllowed;
            try {
                isReference =
                    vm.get('allfields')[v.attribute].attributeconf.type ===
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference;
                isLookup =
                    vm.get('allfields')[v.attribute].attributeconf.type ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup ||
                    vm.get('allfields')[v.attribute].attributeconf.type ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray;
                isUserOrGroup =
                    ['Group', 'User'].indexOf(vm.get('allfields')[v.attribute].attributeconf.targetClass) > -1;
                areCurrentFieldsAllowed = view.getAllowCurrentGroup() || view.getAllowCurrentUser();
            } catch (error) {}

            if (isReference) {
                if (isUserOrGroup && areCurrentFieldsAllowed) {
                    switch (_filter.value1) {
                        case CMDBuildUI.model.users.User.myuser:
                            _filter.currentUser = true;
                            _filter.value1 = null;
                            break;

                        case CMDBuildUI.model.users.User.mygroup:
                            _filter.currentGroup = true;
                            break;
                    }
                } else if (CMDBuildUI.util.helper.FiltersHelper.isOperatorForReferenceOrLookupDescription(v.operator)) {
                    _filter.value1 = null;
                    _filter.referencetext = (Ext.isArray(v.value) && v.value[0]) || null;
                }
            } else if (
                isLookup &&
                CMDBuildUI.util.helper.FiltersHelper.isOperatorForReferenceOrLookupDescription(v.operator)
            ) {
                _filter.value1 = null;
                _filter.referencetext = (Ext.isArray(v.value) && v.value[0]) || null;
            }
            return _filter;
        }

        function addBlockContent(parent, filter) {
            if (filter.and || filter.or) {
                var operator = filter.or
                    ? CMDBuildUI.util.helper.FiltersHelper.blocksoperators.or
                    : CMDBuildUI.util.helper.FiltersHelper.blocksoperators.and;
                var block = parent
                    .down('#blockitems')
                    .add(controllerView.getBlockConfig(operator, parent.getLevel() + 1));
                addBlockContent(block, filter.and || filter.or);
            } else if (Ext.isArray(filter)) {
                filter.forEach(function (f) {
                    addAttrContent(parent, f);
                });
            }
        }

        function addAttrContent(parent, filter) {
            if (filter.simple) {
                parent.down('#blockitems').add(controllerView.getAttributeRowConfig(addFilterRow(filter.simple)));
            } else {
                addBlockContent(parent, filter);
            }
        }

        var mainblock = view.down('#mainblock');
        if (attribute.or) {
            mainblock.setOperator(CMDBuildUI.util.helper.FiltersHelper.blocksoperators.or);
        }
        if (attribute.simple) {
            addAttrContent(mainblock, attribute);
        } else {
            addBlockContent(mainblock, attribute.or || attribute.and);
        }
    },

    /**
     * @private
     */
    privates: {
        /**
         * @param {CMDBuildUI.util.AdvancedFilter} advancedFilter
         *
         * @returns {Boolean}
         */
        validateAttachmentsFilter: function (advancedFilter) {
            if (advancedFilter.isAttachmentsFilterEmpty()) {
                return true;
            } else {
                var validity = true,
                    attachmentsMeta = advancedFilter.getAttachmemtsMeta();
                if (!attachmentsMeta) {
                    return validity;
                }
                var attributes = attachmentsMeta.attributes,
                    categories = attachmentsMeta.categories;

                // check attributes
                for (var key in attributes) {
                    var rows = attributes[key];
                    Ext.Array.forEach(rows, function (row) {
                        var operator = row.operator;
                        if (
                            Ext.isEmpty(operator) ||
                            (!Ext.Array.contains(
                                [
                                    CMDBuildUI.util.helper.FiltersHelper.operators.null,
                                    CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                                ],
                                operator
                            ) &&
                                Ext.isEmpty(row.value[0])) ||
                            (operator == CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                                Ext.isEmpty(row.value[1]))
                        ) {
                            validity = false;
                        }
                    });
                }

                if (!validity) {
                    return validity;
                }

                // check categories
                for (var keyCat in categories) {
                    var rowsCat = categories[keyCat];
                    for (var catAttr in rowsCat.attributes) {
                        Ext.Array.forEach(rowsCat.attributes[catAttr], function (attr) {
                            var operator = attr.operator;
                            if (
                                Ext.isEmpty(operator) ||
                                (!Ext.Array.contains(
                                    [
                                        CMDBuildUI.util.helper.FiltersHelper.operators.null,
                                        CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                                    ],
                                    operator
                                ) &&
                                    Ext.isEmpty(attr.value[0])) ||
                                (operator == CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                                    Ext.isEmpty(attr.value[1]))
                            ) {
                                validity = false;
                            }
                        });
                    }
                }

                return validity;
            }
        },

        /**
         *
         * @param {CMDBuildUI.util.AdvancedFilter} filter
         * @returns {Boolean}
         */
        validateAttributesCustom: function (filter) {
            if (filter.simple) {
                var simpleFilter = filter.simple,
                    operator = simpleFilter.operator,
                    isRuntime =
                        simpleFilter.parameterType === CMDBuildUI.util.helper.FiltersHelper.parameterstypes.runtime;

                if (
                    !isRuntime &&
                    (!operator ||
                        (!simpleFilter._tempid &&
                            ((!Ext.Array.contains(
                                [
                                    CMDBuildUI.util.helper.FiltersHelper.operators.null,
                                    CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                                ],
                                operator
                            ) &&
                                Ext.isEmpty(simpleFilter.value[0])) ||
                                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                                    Ext.isEmpty(simpleFilter.value[1])))))
                ) {
                    return false;
                }
                return true;
            } else {
                var me = this,
                    simpleFilters = filter.and || filter.or || [],
                    validity = false;
                if (!Ext.isEmpty(simpleFilters)) {
                    Ext.Array.each(simpleFilters, function (item, index, allitems) {
                        validity = me.validateAttributesCustom(item);
                        return validity;
                    });
                }
                return validity;
            }
        },

        /**
         *
         * @param {Object} relations
         * @returns {Boolean}
         */
        validateRelations: function (relations) {
            var isValid = true;
            for (var rkey in relations) {
                if (isValid) {
                    var relation = relations[rkey];
                    if (
                        (relation.type == CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof &&
                            Ext.isEmpty(relation.cards)) ||
                        (relation.type == CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter &&
                            !this.validateAttributesCustom(relation.filter))
                    ) {
                        isValid = false;
                    }
                }
            }
            return isValid;
        },

        /**
         *
         * @param {Object} attributes
         * @returns {Boolean}
         */
        validateAttributes: function (attributes) {
            var validity = true;
            for (var key in attributes) {
                var rows = attributes[key];
                Ext.Array.forEach(rows, function (row) {
                    var operator = row.operator;
                    if (
                        Ext.isEmpty(operator) ||
                        (!row._tempid &&
                            !Ext.Array.contains(
                                [
                                    CMDBuildUI.util.helper.FiltersHelper.operators.null,
                                    CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                                ],
                                operator
                            ) &&
                            Ext.isEmpty(row.value[0])) ||
                        (operator == CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                            Ext.isEmpty(row.value[1]))
                    ) {
                        validity = false;
                    }
                });

                if (!validity) {
                    return false;
                }
            }
            return validity;
        }
    }
});
