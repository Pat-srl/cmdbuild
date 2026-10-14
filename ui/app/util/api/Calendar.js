/**
 * @file CMDBuildUI.util.api.Calendar
 * @module CMDBuildUI.util.api.Calendar
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Calendar', {
    singleton: true,

    /**
     * Get all schedules url.
     *
     * @returns {String}
     */
    getEventsUrl: function () {
        return '/calendar/events';
    },

    /**
     * Get schedules attachments.
     *
     * @param {Number|String} scheduleId
     *
     * @returns {String}
     */
    getAttachmentsUrl: function (scheduleId) {
        return Ext.String.format('/calendar/events/{0}/attachments', scheduleId);
    },

    /**
     * Get schedules print url.
     * @param {String} extension
     *
     * @returns {String}
     */
    getPrintCalendarsUrl: function (extension) {
        return Ext.String.format('{0}/calendar/events/print/calendar.{1}', CMDBuildUI.util.Config.baseUrl, extension);
    }
});
