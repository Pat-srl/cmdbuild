/**
 * @file CMDBuildUI.util.api.Emails
 * @module CMDBuildUI.util.api.Emails
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Emails', {
    singleton: true,

    /**
     * Get email templates url.
     *
     * @returns {String}
     */
    getTemplatesUrl: function () {
        return '/email/templates/';
    },

    /**
     * Get email accounts url.
     *
     * @private
     *
     * @returns {String}
     */
    getAccountsUrl: function () {
        return '/email/accounts/';
    },

    /**
     * Get card emails url.
     *
     * @param {String} className
     * @param {Number|String} cardId
     *
     * @returns {String}
     */
    getCardEmailsUrl: function (className, cardId) {
        return Ext.String.format('/classes/{0}/cards/{1}/emails', className, cardId);
    },

    /**
     * Get process instance emails url.
     *
     * @param {String} processName
     * @param {Number} instanceId
     *
     * @returns {String}
     */
    getProcessInstanceEmailsUrl: function (processName, instanceId) {
        return Ext.String.format('/processes/{0}/instances/{1}/emails', processName, instanceId);
    },

    /**
     * Get schedule events url.
     *
     * @param {String} eventId
     *
     * @returns {String}
     */
    getCalendarEmailUrl: function (eventId) {
        return Ext.String.format('/calendar/events/{0}/emails', eventId);
    },

    /**
     * Get email signatures url.
     *
     * @returns {String}
     */
    getSignaturesUrl: function () {
        return '/email/signatures/';
    }
});
