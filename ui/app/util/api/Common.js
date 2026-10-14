/**
 * @file CMDBuildUI.util.api.Common
 * @module CMDBuildUI.util.api.Common
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Common', {
    singleton: true,

    /**
     * Get boot status url.
     *
     * @returns {String}
     */
    getBootStatusUrl: function () {
        return '/boot/status';
    },

    /**
     * Get check db config url.
     *
     * @returns {String}
     */
    getCheckDBConfigUrl: function () {
        return '/boot/database/check';
    },

    /**
     * Get set database config url.
     *
     * @returns {String}
     */
    getSetDBConfigUrl: function () {
        return '/boot/database/configure';
    },

    /**
     * Get apply patches url.
     *
     * @returns {String}
     */
    getApplyPatchesUrl: function () {
        return '/boot/patches/apply';
    },

    /**
     * Get public configuration url.
     *
     * @returns {String}
     */
    getPublicConfigurationUrl: function () {
        return '/configuration/public';
    },

    /**
     * Get system configuration url.
     *
     * @returns {String}
     */
    getSystemConfigurationUrl: function () {
        return '/configuration/system';
    },

    /**
     * Get current session url.
     *
     * @returns {String}
     */
    getCurrentSessionUrl: function () {
        return '/sessions/current';
    },

    /**
     * Get session keep alive url.
     *
     * @returns {String}
     */
    getKeepAliveUrl: function () {
        return '/sessions/current/keepalive';
    },

    /**
     * Get user preferences url.
     *
     * @returns {String}
     */
    getPreferencesUrl: function () {
        return '/sessions/current/preferences';
    },

    /**
     * Get filters url.
     *
     * @param {String} type Object type. One of {@link CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} [typename] Class/Process/View/... name.
     *
     * @returns {String}
     */
    getFiltersUrl: function (type, typename) {
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                return Ext.String.format('{0}/classes/_CalendarEvent/filters', CMDBuildUI.util.Config.baseUrl);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                var item = CMDBuildUI.util.helper.ModelHelper.getViewFromName(typename, type);
                return Ext.String.format('{0}{1}/filters', item.getProxy().getUrl(), item.getId());
            default:
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(typename, type);
                return Ext.String.format('{0}{1}/filters', item.getProxy().getUrl(), item.getId());
        }
    },

    /**
     * Get messages Archive url.
     *
     * @returns {String}
     */
    getMessagesArchiveUrl: function () {
        return '/sessions/current/messages/_ALL/archive';
    },

    /**
     * Get messages Delete url.
     *
     * @returns {String}
     */
    getMessagesDeleteUrl: function () {
        return '/sessions/current/messages/_ALL/delete';
    }
});
