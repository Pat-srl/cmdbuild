/**
 * @file CMDBuildUI.util.api.Map
 * @module CMDBuildUI.util.api.Map
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Map', {
    singleton: true,

    /**
     * Get getValue details url.
     *
     * @param {String} objectType Object type. One of {@link module:CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Class or Process name.
     * @param {String} objectId Card or process instance id.
     * @param {String} geoAttribute Geo attribute name.
     *
     * @returns {String} The url for the geo attribute details.
     */
    getGeoValueDetailsUrl: function (objectType, objectTypeName, objectId, geoAttribute) {
        let type;
        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                type = 'classes';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                type = 'processes';
                break;
        }
        return Ext.String.format(
            '{0}/{1}/{2}/cards/{3}/geovalues/{4}',
            CMDBuildUI.util.Config.baseUrl,
            type,
            objectTypeName,
            objectId,
            geoAttribute
        );
    }
});
