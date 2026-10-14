/**
 * @file CMDBuildUI.util.api.Views
 * @module CMDBuildUI.util.api.Views
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Views', {
    singleton: true,

    /**
     * Get view attributes url.
     *
     * @param {String} viewName
     *
     * @returns {String}
     */
    getAttributesUrl: function (viewName) {
        return Ext.String.format('/views/{0}/attributes', viewName);
    },

    /**
     * Get print items url.
     *
     * @param {String} viewName
     * @param {String} extension
     *
     * @returns {String}
     */
    getPrintItemsUrl: function (viewName, extension) {
        return Ext.String.format('{0}/views/{1}/print/{1}.{2}', CMDBuildUI.util.Config.baseUrl, viewName, extension);
    },

    /**
     * Get import export templates url
     *
     * @param {String} viewName
     */
    getImportExportTemplatesUrl: function (viewName) {
        return Ext.String.format('{0}/etl/templates/by-view/{1}', CMDBuildUI.util.Config.baseUrl, viewName);
    }
});
