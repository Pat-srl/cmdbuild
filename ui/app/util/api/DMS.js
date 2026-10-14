/**
 * @file CMDBuildUI.util.api.DMS
 * @module CMDBuildUI.util.api.DMS
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.DMS', {
    singleton: true,

    /**
     * Get DMS category values url.
     *
     * @param {String} type DMS category
     *
     * @returns {String}
     */
    getCategoryValues: function (type) {
        return Ext.String.format('/dms/categories/{0}/values/', CMDBuildUI.util.Utilities.stringToHex(type));
    },

    /**
     * Get DMS categories url.
     *
     * @returns {String}
     */
    getCategoryTypes: function () {
        return '/dms/categories/';
    },

    /**
     * Get DMS Model attributes url.
     *
     * @param {String} modelName
     *
     * @returns {String}
     */
    getModelAttributes: function (modelName) {
        return Ext.String.format('/dms/models/{0}/attributes', modelName);
    }
});
