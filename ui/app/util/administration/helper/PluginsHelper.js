/**
 * @file CMDBuildUI.util.administration.helper.PluginsHelper
 * @module CMDBuildUI.util.administration.helper.PluginsHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.administration.helper.PluginsHelper', {
    singleton: true,

    /**
     * Get all configuration parameters for a specific plugin.
     *
     * @param {String} pluginName The name of the plugin.
     *
     * @returns {Ext.promise.Promise<Object>} Promise that resolves with the plugin configurations object,
     *      or rejects if the request fails.
     *
     * @example
     * CMDBuildUI.util.administration.helper.PluginsHelper.getPluginConfigs('myplugin').then(function (configs) {
     *     console.log('Plugin configs:', configs);
     * });
     */
    getPluginConfigs: function (pluginName) {
        return CMDBuildUI.util.helper.PluginsHelper.getPluginConfigs(pluginName);
    },

    /**
     * Create a new configuration parameter for a plugin.
     *
     * @param {String} pluginName The name of the plugin.
     * @param {String} key The configuration key.
     * @param {String} value The configuration value.
     * @param {String} [access='public'] The access level for the configuration ('public' or 'private').
     *
     * @returns {Ext.promise.Promise<Object>} Promise that resolves with the created configuration data,
     *      or rejects if the request fails.
     *
     * @example
     * CMDBuildUI.util.administration.helper.PluginsHelper.createPluginConfig('myplugin', 'apiUrl', 'https://api.example.com').then(function (config) {
     *     console.log('Config created:', config);
     * });
     */
    createPluginConfig: function (pluginName, key, value, access) {
        const me = this;

        if (Ext.isEmpty(pluginName) || Ext.isEmpty(key) || Ext.isEmpty(value)) {
            CMDBuildUI.util.Logger.log(
                'Plugin, key and value must all be specified',
                CMDBuildUI.util.Logger.levels.error
            );
            return Ext.Deferred.rejected('Missing plugin, key or value');
        }

        return CMDBuildUI.util.helper.PluginsHelper.getPlugin(pluginName)
            .then(function (plugin) {
                return plugin.getPluginConfigs();
            })
            .then(
                function (pluginConfigsStore) {
                    let newRecord = pluginConfigsStore.add({
                        plugin: pluginName,
                        key: key,
                        value: value,
                        access: access || 'public'
                    })[0];

                    newRecord.phantom = true;

                    let deferredSync = new Ext.Deferred();

                    me.safePluginsStoreSync(pluginConfigsStore).then(
                        function () {
                            deferredSync.resolve();
                        },
                        function (error) {
                            CMDBuildUI.util.Logger.log(
                                `Error creating the config ${key} for the plugin ${pluginName}`,
                                CMDBuildUI.util.Logger.levels.error
                            );
                            deferredSync.reject(error);
                        }
                    );

                    return deferredSync.promise;
                },
                function (error) {
                    CMDBuildUI.util.Logger.log(
                        `Error retrieving the plugin "${pluginName}"`,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    return Ext.Deferred.rejected(error);
                }
            );
    },

    /**
     * Update an existing configuration parameter for a plugin.
     *
     * @param {String} pluginName The name of the plugin.
     * @param {String} key The configuration key to update.
     * @param {String} value The new configuration value.
     *
     * @returns {Ext.promise.Promise<Object>} Promise that resolves with the updated configuration data,
     *      or rejects if the request fails.
     *
     * @example
     * CMDBuildUI.util.administration.helper.PluginsHelper.updatePluginConfig('myplugin', 'apiUrl', 'https://newapi.example.com').then(function (config) {
     *     console.log('Config updated:', config);
     * });
     */
    updatePluginConfig: function (pluginName, key, value) {
        const me = this;

        if (Ext.isEmpty(pluginName) || Ext.isEmpty(key) || Ext.isEmpty(value)) {
            CMDBuildUI.util.Logger.log(
                'Plugin, key and value must all be specified',
                CMDBuildUI.util.Logger.levels.error
            );
            return Ext.Deferred.rejected('Missing plugin, key or value');
        }

        return CMDBuildUI.util.helper.PluginsHelper.getPlugin(pluginName)
            .then(function (plugin) {
                return plugin.getPluginConfigs();
            })
            .then(
                function (pluginConfigsStore) {
                    const currentRecord = pluginConfigsStore.getById(key);

                    if (Ext.isEmpty(currentRecord)) {
                        CMDBuildUI.util.Logger.log(
                            `Config "${key}" not found for plugin "${pluginName}"`,
                            CMDBuildUI.util.Logger.levels.error
                        );
                        return Ext.Deferred.rejected(`Config "${key}" not found for plugin "${pluginName}"`);
                    }

                    currentRecord.set('value', value);

                    let deferredSync = new Ext.Deferred();

                    me.safePluginsStoreSync(pluginConfigsStore).then(
                        function () {
                            deferredSync.resolve();
                        },
                        function (error) {
                            CMDBuildUI.util.Logger.log(
                                `Error updating the config ${key} for the plugin ${pluginName}`,
                                CMDBuildUI.util.Logger.levels.error
                            );
                            deferredSync.reject(error);
                        }
                    );

                    return deferredSync.promise;
                },
                function (error) {
                    CMDBuildUI.util.Logger.log(
                        `Error retrieving the plugin "${pluginName}"`,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    return Ext.Deferred.rejected(error);
                }
            );
    },

    /**
     * Update multiple configuration parameters for a plugin at once.
     *
     * @param {String} pluginName The name of the plugin.
     * @param {Object} configs Object containing key-value pairs of configurations to update.
     *
     * @returns {Ext.promise.Promise<Object>} Promise that resolves with the updated configurations data,
     *      or rejects if the request fails.
     *
     * @example
     * CMDBuildUI.util.administration.helper.PluginsHelper.updatePluginConfigs('myplugin', {
     *     apiUrl: 'https://api.example.com',
     *     apiKey: 'secret123'
     * }).then(function (configs) {
     *     console.log('Configs updated:', configs);
     * });
     */
    updatePluginConfigs: function (pluginName, configs) {
        const me = this;

        if (Ext.isEmpty(pluginName) || Ext.isEmpty(configs) || !Ext.isObject(configs)) {
            CMDBuildUI.util.Logger.log(
                'Plugin and configs object must be specified',
                CMDBuildUI.util.Logger.levels.error
            );
            return Ext.Deferred.rejected('Missing plugin or configs object');
        }

        return CMDBuildUI.util.helper.PluginsHelper.getPlugin(pluginName)
            .then(function (plugin) {
                return plugin.getPluginConfigs();
            })
            .then(
                function (pluginConfigsStore) {
                    for (const key in configs) {
                        const value = configs[key];

                        const currentRecord = pluginConfigsStore.getById(key);

                        if (Ext.isEmpty(currentRecord)) {
                            CMDBuildUI.util.Logger.log(
                                `Config "${key}" not found for plugin "${pluginName}"`,
                                CMDBuildUI.util.Logger.levels.warn
                            );
                            continue;
                        }

                        currentRecord.set('value', value);
                    }

                    let deferredSync = new Ext.Deferred();

                    me.safePluginsStoreSync(pluginConfigsStore).then(
                        function () {
                            deferredSync.resolve();
                        },
                        function (error) {
                            CMDBuildUI.util.Logger.log(
                                `Error updating the configs ${Object.keys(configs)} for the plugin ${pluginName}`,
                                CMDBuildUI.util.Logger.levels.error
                            );
                            deferredSync.reject(error);
                        }
                    );

                    return deferredSync.promise;
                },
                function (error) {
                    CMDBuildUI.util.Logger.log(
                        `Error retrieving the plugin "${pluginName}"`,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    return Ext.Deferred.rejected(error);
                }
            );
    },

    /**
     * Safely synchronizes a plugin configuration store if it contains pending changes
     * (new, modified, or removed records).
     *
     * @param {Ext.data.Store} store The store to synchronize.
     *
     * @returns {Ext.promise.Promise<Ext.data.Batch|null>} Promise that resolves with the synchronization batch
     * if a sync was performed, or null if no changes were found. Rejects if the sync operation fails.
     *
     * @private
     */
    safePluginsStoreSync: function (store) {
        const deferred = new Ext.Deferred();

        if (store.getNewRecords().length || store.getModifiedRecords().length || store.getRemovedRecords().length) {
            store.sync({
                success: function (batch) {
                    deferred.resolve(batch);
                },
                failure: function (err) {
                    deferred.reject(err);
                }
            });
        } else {
            deferred.resolve(null);
        }

        return deferred.promise;
    }
});
