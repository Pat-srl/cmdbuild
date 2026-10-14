/**
 * @file CMDBuildUI.util.api.DomainTrees
 * @module CMDBuildUI.util.api.DomainTrees
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.DomainTrees', {
    singleton: true,

    /**
     * Get domain trees url.
     *
     * @returns {String}
     */
    getDomainTrees: function () {
        return '/domainTrees';
    },

    /**
     * Get GIS domain tree url.
     *
     * @returns {String}
     */
    getGisDomainTree: function () {
        return '/domainTrees/gisnavigation';
    }
});
