Ext.define('CMDBuildUI.mixins.model.Domain', {
    mixinId: 'model-domain-mixin',

    /**
     *
     * @return {String} domains url
     */
    getDomainsUrl: Ext.emptyFn,

    /**
     * Load domains relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getDomains: function (force) {
        var deferred = new Ext.Deferred();
        var domains = this.domains();

        if (domains.isLoading()) {
            domains.addListener('load', function (records, operation, success) {
                if (success) {
                    deferred.resolve(domains, true);
                }
            });
        } else if (!domains.isLoaded() || force) {
            // configure proxy
            domains.getProxy().setUrl(this.getDomainsUrl());
            domains.getProxy().setExtraParams({
                detailed: true
            });
            // load store
            domains.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(domains, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(domains, false);
        }
        return deferred.promise;
    },

    /**
     * Load domains relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getFkDomains: function (force) {
        var deferred = new Ext.Deferred();
        var fkdomains = this.fkdomains();

        if (!fkdomains.isLoaded() || force) {
            // set store advanced filter
            fkdomains.setAdvancedFilter({
                attribute: {
                    simple: {
                        attribute: 'destination',
                        operator: 'in',
                        value: this.getHierarchy()
                    }
                }
            });
            // load store
            fkdomains.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(fkdomains, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(fkdomains, false);
        }
        return deferred.promise;
    }
});
