/**
 * @file CMDBuildUI.util.api.Functions
 * @module CMDBuildUI.util.api.Functions
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Functions', {
    singleton: true,

    /**
     * Get function url by function name.
     *
     * @param {String} functionName
     *
     * @returns {String}
     */
    getFunctionByNameUrl: function (functionName) {
        return Ext.String.format('/functions/{0}', functionName);
    },

    /**
     * Get function outputs url by function name.
     *
     * @param {String} functionName
     *
     * @returns {String}
     */
    getOutputsUrlByFunctionName: function (functionName) {
        return Ext.String.format('/functions/{0}/outputs', functionName);
    },

    /**
     * Get function outputs url by function name.
     *
     * @deprecated
     * Use {@link CMDBuildUI.util.api.Functions#getOutputsUrlByFunctionName CMDBuildUI.util.api.Functions.getOutputsUrlByFunctionName()} instead.
     *
     * @param {String} functionName
     *
     * @returns {String}
     */
    getFunctionOutputsByNameUrl: function (functionName) {
        CMDBuildUI.util.Logger.log(
            'getFunctionOutputsByNameUrl is deprecated. Please use getOutputsUrlByFunctionName',
            CMDBuildUI.util.Logger.levels.warn
        );
        return this.getOutputsUrlByFunctionName(functionName);
    },

    /**
     * Get function parameters url by function name.
     *
     * @param {String} functionName
     *
     * @returns {String}
     */
    getParametersUrlByFunctionName: function (functionName) {
        return Ext.String.format('/functions/{0}/parameters', functionName);
    },

    /**
     * Get function attributes url by function name.
     *
     * @param {String} functionName
     *
     * @returns {String}
     */
    getAttributesUrlByFunctionName: function (functionName) {
        return Ext.String.format('/functions/{0}/attributes', functionName);
    }
});
