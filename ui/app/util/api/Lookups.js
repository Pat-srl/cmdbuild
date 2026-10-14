/**
 * @file CMDBuildUI.util.api.Lookups
 * @module CMDBuildUI.util.api.Lookups
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Lookups', {
    singleton: true,

    /**
     * Get lookup values url.
     *
     * @param {String} type
     *
     * @returns {String}
     */
    getLookupValues: function (type) {
        return Ext.String.format('/lookup_types/{0}/values/', CMDBuildUI.util.Utilities.stringToHex(type));
    },

    /**
     * Get lookup types url.
     *
     * @returns {String}
     */
    getLookupTypes: function () {
        return '/lookup_types';
    }
});
