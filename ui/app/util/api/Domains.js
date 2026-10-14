/**
 * @file CMDBuildUI.util.api.Domains
 * @module CMDBuildUI.util.api.Domains
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Domains', {
    singleton: true,

    /**
     * Get all domains url.
     *
     * @returns {String}
     */
    getAllDomainsUrl: function () {
        return Ext.String.format('/domains');
    },

    /**
     * Get domain attributes url.
     *
     * @param {String} domainName
     *
     * @returns {String}
     */
    getAttributes: function (domainName) {
        return Ext.String.format('/domains/{0}/attributes', domainName);
    },

    /**
     * Get Foreign Keys domains url.
     *
     * @returns {String}
     */
    getFkDomainsUrl: function () {
        return Ext.String.format('/fkdomains');
    }
});
