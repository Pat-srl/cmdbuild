/**
 * @file CMDBuildUI.util.api.Classes
 * @module CMDBuildUI.util.api.Classes
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Classes', {
    singleton: true,

    /**
     * Get Class attributes url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getAttributes: function (className) {
        return Ext.String.format('/classes/{0}/attributes', className);
    },

    /**
     * Get Class GEO attributes.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getGeoAttributes: function (className) {
        return Ext.String.format('/classes/{0}/geoattributes', className);
    },

    /**
     * Get geo values for specific card.
     *
     * @param {String} className
     * @param {Number|String} cardId
     *
     * @returns {String}
     */
    getGeoValuesUrl: function (className, cardId) {
        return Ext.String.format(
            '{0}/classes/{1}/cards/{2}/geovalues',
            CMDBuildUI.util.Config.baseUrl,
            className,
            cardId
        );
    },

    /**
     * Get geo layers for specific card.
     *
     * @param {String} className
     * @param {Number|String} cardId
     *
     * @returns {String}
     */
    getGeoLayersUrl: function (className, cardId) {
        return Ext.String.format(
            '{0}/classes/{1}/cards/{2}/geolayers',
            CMDBuildUI.util.Config.baseUrl,
            className,
            cardId
        );
    },

    /**
     * This function returns the url for the geolayer service.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getExternalGeoAttributes: function (className) {
        return Ext.String.format('/classes/{0}/cards/_ANY/geolayers', className);
    },

    /**
     * Get cards url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getCardsUrl: function (className) {
        return Ext.String.format('/classes/{0}/cards', className);
    },

    /**
     * Get bim data url for specific card.
     *
     * @param {String} className
     * @param {Number} cardId
     *
     * @returns {String}
     */
    getCardBimUrl: function (className, cardId) {
        return Ext.String.format(
            '{0}/classes/{1}/cards/{2}/bimvalue?if_exists=true',
            CMDBuildUI.util.Config.baseUrl,
            className,
            cardId
        );
    },

    /**
     * Get card relations url.
     *
     * @param {String} className
     * @param {Number} cardId
     *
     * @returns {String}
     */
    getCardRelations: function (className, cardId) {
        return Ext.String.format('/classes/{0}/cards/{1}/relations', className, cardId);
    },

    /**
     * Get print cards list url.
     *
     * @param {String} className
     * @param {String} extension
     *
     * @returns {String}
     */
    getPrintCardsUrl: function (className, extension) {
        return Ext.String.format('{0}/classes/{1}/print/{1}.{2}', CMDBuildUI.util.Config.baseUrl, className, extension);
    },

    /**
     * Get print card url.
     *
     * @param {String} className
     * @param {Number} cardId
     * @param {String} extension
     *
     * @returns {String}
     */
    getPrintCardUrl: function (className, cardId, extension) {
        return Ext.String.format(
            '{0}/classes/{1}/cards/{2}/print/{1}-{2}.{3}',
            CMDBuildUI.util.Config.baseUrl,
            className,
            cardId,
            extension
        );
    },

    /**
     * Get import/export templates url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getImportExportTemplatesUrl: function (className) {
        return Ext.String.format('{0}/etl/templates/by-class/{1}', CMDBuildUI.util.Config.baseUrl, className);
    },

    /**
     * Get import/export gates url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getImportExportGatesUrl: function (className) {
        return Ext.String.format('{0}/etl/gates/by-class/{1}', CMDBuildUI.util.Config.baseUrl, className);
    },

    /**
     * Get tematisms url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getThematismsUrl: function (className) {
        return Ext.String.format('{0}/classes/{1}/geostylerules', CMDBuildUI.util.Config.baseUrl, className);
    },

    /**
     * Get tematism data url.
     *
     * @param {String} className
     * @param {Number|String} thematismId
     *
     * @returns {String}
     */
    getThematismResultUrl: function (className, thematismId) {
        if (thematismId) {
            return Ext.String.format(
                '{0}/classes/{1}/geostylerules/{2}/result',
                CMDBuildUI.util.Config.baseUrl,
                className,
                thematismId
            );
        } else {
            return Ext.String.format(
                '{0}/classes/{1}/geostylerules/tryRules',
                CMDBuildUI.util.Config.baseUrl,
                className
            );
        }
    },

    /**
     * Get class domains url.
     *
     * @param {String} className
     *
     * @returns {String}
     */
    getDomains: function (className) {
        return Ext.String.format('/classes/{0}/domains', className);
    },

    /**
     * Get card attachments.
     *
     * @param {String} className
     * @param {Number|String} cardId
     *
     * @returns {String}
     */
    getAttachments: function (className, cardId) {
        return Ext.String.format('/classes/{0}/cards/{1}/attachments', className, cardId);
    }
});
