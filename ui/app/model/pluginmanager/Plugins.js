Ext.define('CMDBuildUI.model.pluginmanager.Plugins', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        statuses: {
            error: 'error',
            ready: 'ready'
        }
    },

    fields: [
        {
            name: 'checksum',
            type: 'string'
        },
        {
            name: 'configs',
            type: 'auto'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'healthCheck',
            type: 'auto'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'requiredCoreVersion',
            type: 'string'
        },
        {
            name: 'requiredLibs',
            type: 'auto'
        },
        {
            name: 'service',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'tag',
            type: 'string'
        },
        {
            name: 'version',
            type: 'string'
        },
        {
            name: '_healthCheck_message',
            type: 'string'
        },
        {
            name: 'expirationDate',
            type: 'date'
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.pluginmanager.PluginConfig',
            name: 'pluginConfigs'
        }
    ],

    /**
     * @return {String} pluginConfigs url
     */
    getPluginConfigsUrl: function () {
        return `/plugin/${this.get('service')}/config`;
    },

    /**
     * Load pluginConfigs
     * @param {Boolean} force If `true` load the store even if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the plugin configs store and a boolean field.
     */
    getPluginConfigs: function (force) {
        const store = this.pluginConfigs();
        let promise = this._pluginConfigsPromise;

        if (!force && store.isLoading() && (promise && promise.promise)) {
            return promise.promise;
        }
        else if (!store.isLoaded() || force) {
            if (!promise)
                promise = new Ext.Deferred();

            store.setProxy({
                type: 'baseproxy',
                url: this.getPluginConfigsUrl()
            });

            store.load({
                callback: function (records, operation, success) {
                    if (success) {
                        promise.resolve(store, true);
                    } else {
                        promise.reject(operation);
                    }
                }
            });
        }
        else if (!promise) {
            promise = new Ext.Deferred();
            promise.resolve(store, false);
        }
        else {
            // return promise
            promise.resolve(store, false);
        }
        return promise.promise;
    }
});
