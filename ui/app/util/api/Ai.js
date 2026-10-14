/**
 * @file CMDBuildUI.util.api.Ai
 * @module CMDBuildUI.util.api.Ai
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Ai', {
    singleton: true,
    /**
     * Get all AI-related API url.
     *
     * @returns {String}
     */
    getAiUrl: function () {
        return '/ai';
    }
});
