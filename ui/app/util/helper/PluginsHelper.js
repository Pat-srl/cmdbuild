/**
 * @file CMDBuildUI.util.helper.PluginsHelper
 * @module CMDBuildUI.util.helper.PluginsHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.PluginsHelper', {
    singleton: true,

    /**
     * Get the list of all the available plugins.
     *
     * @param {String} [tag] Optional tag of plugins to filter.
     *
     * @returns {Ext.promise.Promise<CMDBuildUI.model.pluginmanager.Plugins[]>} Promise that resolves with an array of plugin records.
     *
     * @example
     * CMDBuildUI.util.helper.PluginsHelper.getPlugins('connector').then(function (plugins) {
     *     console.log('Found plugins:', plugins);
     * });
     */
    getPlugins: function (tag) {
        return this._ensureStoreLoaded().then(function (pluginsStore) {
            return pluginsStore.getRange().filter(function (plugin) {
                return Ext.isEmpty(tag) || plugin.get('tag') == tag;
            });
        });
    },

    /**
     * Get a specific plugin by name.
     *
     * @param {String} pluginName The name of the plugin to retrieve.
     *
     * @returns {Ext.promise.Promise<CMDBuildUI.model.pluginmanager.Plugins>} Promise that resolves with the plugin record,
     *      or rejects if the plugin is not found.
     *
     * @example
     * CMDBuildUI.util.helper.PluginsHelper.getPlugin('myplugin').then(function (plugin) {
     *     console.log('Plugin found:', plugin.get('service'));
     * });
     */
    getPlugin: function (pluginName) {
        if (Ext.isEmpty(pluginName)) {
            CMDBuildUI.util.Logger.log('Plugin name is required', CMDBuildUI.util.Logger.levels.error);
            return Ext.Deferred.rejected('Missing plugin name');
        }

        return this._ensureStoreLoaded().then(function (store) {
            const plugin = store.findRecord('service', pluginName);
            if (!plugin) {
                CMDBuildUI.util.Logger.log(`Plugin "${pluginName}" not found`, CMDBuildUI.util.Logger.levels.error);
                return Ext.Deferred.rejected(`Plugin "${pluginName}" not found`);
            }
            return plugin;
        });
    },

    /**
     * Get all configuration parameters for the specified plugin as a key-value object.
     *
     * @param {String} pluginName The name of the plugin.
     * @param {String} [config] Optional parameter (unused in current implementation).
     *
     * @returns {Ext.promise.Promise<Object>} Promise that resolves with an object containing all plugin configurations,
     * or rejects if the plugin is not found or the request fails.
     *
     * @example
     * CMDBuildUI.util.helper.PluginsHelper.getPluginConfigs('myplugin').then(function (configs) {
     *     console.log('Plugin configs:', configs); // { key1: value1, key2: value2 }
     * });
     */
    getPluginConfigs: function (pluginName, config) {
        if (Ext.isEmpty(pluginName)) {
            CMDBuildUI.util.Logger.log('Plugin name is required', CMDBuildUI.util.Logger.levels.error);
            return Ext.Deferred.rejected('Missing plugin name');
        }

        return this.getPlugin(pluginName)
            .then(function (plugin) {
                return plugin.getPluginConfigs();
            })
            .then(
                function (configsStore) {
                    return configsStore.getRange().reduce(function (acc, configRecord) {
                        acc[configRecord.get('key')] = configRecord.get('value');
                        return acc;
                    }, {});
                },
                function (error) {
                    CMDBuildUI.util.Logger.log(
                        `Error loading configs for plugin "${pluginName}"`,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    return Ext.Deferred.rejected(error);
                }
            );
    },

    /**
     * Get the value of the specified config for the given plugin.
     *
     * @param {String} pluginName The name of the plugin.
     * @param {String} config The name of the configuration parameter.
     *
     * @returns {Ext.promise.Promise<String>} Promise that resolves with the configuration value,
     *      or rejects if the request fails.
     *
     * @example
     * CMDBuildUI.util.helper.PluginsHelper.getPluginConfig('myplugin', 'apiUrl').then(function (value) {
     *     console.log('Config value:', value);
     * });
     */
    getPluginConfig: function (pluginName, config) {
        if (Ext.isEmpty(pluginName) || Ext.isEmpty(config)) {
            CMDBuildUI.util.Logger.log(
                'Both the plugin and the config are required',
                CMDBuildUI.util.Logger.levels.error
            );
            return Ext.Deferred.rejected('Missing plugin or config');
        }

        return this.getPlugin(pluginName)
            .then(function (plugin) {
                return plugin.getPluginConfigs();
            })
            .then(
                function (configsStore) {
                    const configRecord = configsStore.findRecord('key', config);
                    if (!configRecord) {
                        CMDBuildUI.util.Logger.log(
                            `Config "${config}" not found for plugin "${pluginName}"`,
                            CMDBuildUI.util.Logger.levels.error
                        );
                        return Ext.Deferred.rejected(`Config "${config}" not found for plugin "${pluginName}"`);
                    }
                    return configRecord.get('value');
                },
                function (error) {
                    CMDBuildUI.util.Logger.log(
                        `Error loading configs for plugin "${pluginName}"`,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    return Ext.Deferred.rejected(error);
                }
            );
    },

    /**
     * Ensure the plugins store is loaded, triggering a load if the store is not already populated.
     *
     * @private
     *
     * @returns {Ext.promise.Promise<Ext.data.Store>} Promise that resolves with the loaded store,
     * or rejects if the store fails to load.
     */
    _ensureStoreLoaded: function () {
        var deferred = new Ext.Deferred();
        const store = Ext.getStore('pluginmanager.Plugins');

        if (store.isLoaded()) {
            deferred.resolve(store);
        } else {
            if (!store.isLoading()) store.load();

            store.on(
                'load',
                function (store, records, success, operation) {
                    if (success) {
                        deferred.resolve(store);
                    } else {
                        CMDBuildUI.util.Logger.log('Error loading the plugins', CMDBuildUI.util.Logger.levels.error);
                        deferred.reject();
                    }
                },
                null,
                { single: true }
            );
        }

        return deferred.promise;
    }
});
