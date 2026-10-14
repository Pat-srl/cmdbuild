/**
 * @file CMDBuildUI.util.api.Groups
 * @module CMDBuildUI.util.api.Groups
 * @author PAT srl
 * @access private
 */
Ext.define('CMDBuildUI.util.api.Groups', {
    singleton: true,

    /**
     * Get all available groups url.
     *
     * @returns {String}
     */
    getRoles: function () {
        return '/roles';
    }
});
