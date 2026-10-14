/**
 * @file CMDBuildUI.model.base.Filter
 * @class CMDBuildUI.model.base.Filter
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.model.base.Filter', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        /**
         * @static
         * @deprecated
         * Use {@link CMDBuildUI.util.helper.FiltersHelper#operators CMDBuildUI.util.helper.FiltersHelper.operators} instead.
         *
         * @type {Object}
         *
         * @example
         * var operator = CMDBuildUI.util.helper.FiltersHelper.operators.equal;
         *
         * @memberof CMDBuildUI.model.base.Filter
         */
        operators: CMDBuildUI.util.helper.FiltersHelper.operators,

        /**
         * @static
         * @deprecated
         * Use {@link CMDBuildUI.util.helper.FiltersHelper#parameterstypes CMDBuildUI.util.helper.FiltersHelper.parameterstypes} instead.
         *
         * @type {Object}
         * @property {String} runtime
         * @property {String} fixed
         *
         * @example
         * var type = CMDBuildUI.util.helper.FiltersHelper.parameterstypes.fixed;
         *
         * @memberof CMDBuildUI.model.base.Filter
         */
        parametersypes: CMDBuildUI.util.helper.FiltersHelper.parameterstypes,

        /**
         * @static
         * @deprecated
         * Use {@link CMDBuildUI.util.helper.FiltersHelper#relationstypes CMDBuildUI.util.helper.FiltersHelper.relationstypes} instead.
         *
         * @type {Object}
         * @property {String} any
         * @property {String} noone
         * @property {String} oneof
         * @property {String} fromfilter
         *
         * @example
         * var type = CMDBuildUI.util.helper.FiltersHelpers.relationstypes.any;
         *
         * @memberof CMDBuildUI.model.base.Filter
         */
        relationstypes: CMDBuildUI.util.helper.FiltersHelper.relationstypes,

        /**
         * @static
         * @deprecated
         * Use {@link CMDBuildUI.util.helper.FiltersHelper#cloneFilters CMDBuildUI.util.helper.FiltersHelper.cloneFilters} instead.
         *
         * @type {Object}
         * @property {String} ignore
         * @property {String} migrates
         * @property {String} clone
         *
         * @example
         * var clone = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore;
         *
         * @memberof CMDBuildUI.model.base.Filter
         */
        cloneFilters: CMDBuildUI.util.helper.FiltersHelper.cloneFilters,

        isOperatorForRefernceOrLookupDescription: function (operator) {
            return this.isOperatorForReferenceOrLookupDescription(operator);
        },

        isOperatorForReferenceOrLookupDescription: function (operator) {
            return CMDBuildUI.util.helper.FiltersHelper.isOperatorForReferenceOrLookupDescription(operator);
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
            critical: true,
            convert: function (value, record) {
                return record.get('_description_translation') || value || record.get('name');
            }
        },
        {
            name: 'ownerType',
            type: 'string',
            critical: true
        },
        {
            name: 'target',
            type: 'string',
            critical: true
        },
        {
            name: 'configuration',
            type: 'auto',
            convert: function (value, record) {
                if (value && Ext.isString(value)) {
                    value = Ext.JSON.decode(value);
                }
                return value;
            },
            serialize: function (value, record) {
                if (value && !Ext.isString(value)) {
                    value = Ext.JSON.encode(value);
                }
                return value;
            },
            critical: true
        },
        {
            name: 'shared',
            type: 'boolean',
            defaultValue: false,
            critical: true
        }
    ],

    isFilter: true,

    proxy: {
        type: 'baseproxy'
    }
});
