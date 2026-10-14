/**
 * @file CMDBuildUI.util.api.Processes
 * @module CMDBuildUI.util.api.Processes
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Processes', {
    singleton: true,

    /**
     * Get process attributes url.
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getAttributes: function (processName) {
        return Ext.String.format('/processes/{0}/attributes', processName);
    },

    /**
     * Get all instances activities url.
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getAllInstancesActivitiesUrl: function (processName) {
        return Ext.String.format('/processes/{0}/instance_activities', processName);
    },

    /**
     * Get start activities url.
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getStartActivitiesUrl: function (processName) {
        return Ext.String.format('/processes/{0}/start_activities', processName);
    },

    /**
     * Get process instances url.
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getInstancesUrl: function (processName) {
        return Ext.String.format('/processes/{0}/instances/{1}', processName);
    },

    /**
     * Get process instance activities url.
     *
     * @param {String} processName
     * @param {Number|String} instanceId
     *
     * @returns {String}
     */
    getInstanceActivitiesUrl: function (processName, instanceId) {
        return Ext.String.format('/processes/{0}/instances/{1}/activities', processName, instanceId);
    },

    /**
     * Get XPDL template url.
     *
     * @private
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getTemplateFileUrl: function (processName) {
        const uri = Ext.String.format('{0}/processes/{1}/template', CMDBuildUI.util.Config.baseUrl, processName);
        return encodeURI(uri);
    },

    /**
     * Get XPDL version file url.
     *
     * @private
     *
     * @param {String} processName
     * @param {String} versionId
     *
     * @returns {String}
     */
    getVersionFileUrl: function (processName, versionId) {
        const uri = Ext.String.format(
            '{0}/processes/{1}/versions/{2}/file',
            CMDBuildUI.util.Config.baseUrl,
            processName,
            versionId
        );
        return encodeURI(uri);
    },

    /**
     * Get process instance relations url.
     *
     * @param {String} processName
     * @param {Number|String} instanceId
     *
     * @returns {String}
     */
    getProcessInstanceRelations: function (processName, instanceId) {
        return Ext.String.format('/processes/{0}/instances/{1}/relations', processName, instanceId);
    },

    /**
     * Get print process instances url.
     *
     * @param {String} processName
     * @param {String} extension
     *
     * @returns {String}
     */
    getPrintInstancesUrl: function (processName, extension) {
        return Ext.String.format(
            '{0}/processes/{1}/print/{1}.{2}',
            CMDBuildUI.util.Config.baseUrl,
            processName,
            extension
        );
    },

    /**
     * Get process domains url.
     *
     * @param {String} processName
     *
     * @returns {String}
     */
    getDomains: function (processName) {
        return Ext.String.format('/processes/{0}/domains', processName);
    },

    /**
     * Get process instance attachments url.
     *
     * @param {String} processName
     * @param {Number|String} instanceId
     *
     * @returns {String}
     */
    getAttachmentsUrl: function (processName, instanceId) {
        return Ext.String.format('/processes/{0}/instances/{1}/attachments', processName, instanceId);
    },

    /**
     * Get process instances resume url.
     *
     * @param {String} processName
     * @param {String|Number} processId
     *
     * @returns {String}
     */
    getInstanceResumeUrl: function (processName, processId) {
        return Ext.String.format('/processes/{0}/instances/{1}/resume', processName, processId);
    }
});
