Ext.define('CMDBuildUI.mixins.model.Gis', {
    mixinId: 'model-gis-mixin',

    /**
     * @cfg {Function} getGeoLayersUrl
     * A function that returns the url of the geo layers.
     */
    getGeoLayersUrl: Ext.emptyFn,

    /**
     * @cfg {Function} getGeoLayersUrl
     * A function that returns the url of the thematisms.
     */
    getThematismsUrl: Ext.emptyFn,

    /**
     * @cfg {Function} getGeoLayersUrl
     * A function that returns the url of the geo attributes.
     */
    getGeoAttributesUrl: Ext.emptyFn,

    /**
     * Load geolayers
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     *
     * @returns {Ext.Deferred} The promise has as paramenters the geolayers store and a boolean field.
     */
    getGeolayers: function (force) {
        var deferred = new Ext.Deferred(),
            geolayers = this.geolayers(),
            geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled);

        if ((!geolayers.isLoaded() || force) && geoserverEnabled) {
            geolayers.getProxy().setUrl(this.getGeoLayersUrl());
            geolayers.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(geolayers, true);
                    }
                }
            });
        } else {
            deferred.resolve(geolayers, false);
        }
        return deferred.promise;
    },

    /**
     * Load thematisms
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     *
     * @returns {Ext.Deferred} The promise has as paramenters the thematism store and a boolean field.
     */
    getThematisms: function (force) {
        var deferred = new Ext.Deferred();
        var thematisms = this.thematisms();


        if (!thematisms.isLoaded() || force) {
            thematisms.getProxy().setUrl(this.getThematismsUrl());
            thematisms.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(thematisms, true);
                    }
                }
            });
        } else {
            deferred.resolve(thematisms, false);
        }
        return deferred.promise;
    },

    /**
     * Load thematisms
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     *
     * @returns {Ext.Deferred} The promise has as paramenters the geo attributes store and a boolean field.
     */
    getGeoAttributes: function (force) {
        var deferred = new Ext.Deferred();
        var geoattributes = this.geoAttributes();

        if (!geoattributes.isLoaded() || force) {
            var params;

            geoattributes.getProxy().setUrl(this.getGeoAttributesUrl());
            if (!CMDBuildUI.util.helper.SessionHelper.isAdministrationModule()) {
                params = {
                    visible: true
                }
            }
            geoattributes.load({
                params: params,
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(geoattributes, true);
                    }
                }
            });
        } else {
            deferred.resolve(geoattributes, false);
        }
        return deferred.promise;
    }
});