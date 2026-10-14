Ext.define('CMDBuildUI.util.administration.helper.ConfigHelper', {
    singleton: true,

    cacheMinutes: 1,
    privates: {
        _config: null,
        _lastSyncDate: null,
        _blacklistKeys: [
            'org.cmdbuild.database.db.admin.password',
            'org.cmdbuild.database.db.url',
            'org.cmdbuild.database.checkConnectionAtStartup',
            'org.cmdbuild.database.db.driverClassName',
            'org.cmdbuild.database.db.admin.username',
            'org.cmdbuild.database.db.username',
            'org.cmdbuild.database.db.autopatch.enable',
            'org.cmdbuild.database.db.password'
        ]
    },

    settings: null,

    /**
     *
     * @param {String} key
     * @param {Boolean} logger
     * @param {Boolean} force
     * @param {Boolean} sync
     * @returns {Array || Ext.Deferred}
     */
    getConfig: function (key, logger, force, sync) {
        if (sync) {
            return Ext.Array.filter(this.getConfigs(null, true), function (element) {
                return element._key === key || key.replace(/\./g, '__DOT__') === element._key;
            });
        }
        const deferred = new Ext.Deferred();
        this.getConfigs(force).then(function (configs) {
            const filtered = Ext.Array.filter(configs, function (element) {
                return element._key === key || key.replace(/\./g, '__DOT__') === element._key;
            });

            if (!filtered[0]) {
                if (logger) {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('configuration {0} not found', key),
                        CMDBuildUI.util.Logger.levels.info
                    );
                }
                deferred.resolve(null);
            } else {
                if (filtered[0].hasValue) {
                    if (logger) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Current value is: {0}', filtered[0].value),
                            CMDBuildUI.util.Logger.levels.info
                        );
                    }
                    deferred.resolve(filtered[0].value);
                } else {
                    if (logger) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Current default value is: {0}', filtered[0]['default']),
                            CMDBuildUI.util.Logger.levels.info
                        );
                    }
                    deferred.resolve(filtered[0]['default']);
                }
            }
        });

        return deferred.promise;
    },

    /**
     *
     * @param {Boolean} force
     * @param {Boolean} sync
     * @returns {Array || Ext.Deferred}
     */
    getConfigs: function (force, sync) {
        const me = this;
        const deferred = new Ext.Deferred();

        if (sync) {
            return me._config;
        }

        if (!force && me._config && me._lastSyncDate >= new Date().getTime() - 60 * me.cacheMinutes * 1000) {
            deferred.resolve(me._config);
        } else {
            Ext.Ajax.request({
                url: Ext.String.format('{0}/system/config?detailed=true', CMDBuildUI.util.Config.baseUrl),
                method: 'GET',
                success: function (transport) {
                    const jsonResponse = Ext.JSON.decode(transport.responseText);
                    const setupKeys = Ext.Object.getAllKeys(jsonResponse.data);
                    const result = [];
                    setupKeys.forEach(function (key) {
                        /**
                         * Example data
                         *
                         * default: "DISABLED"
                         * description: "valid values are DISABLED, CMDBUILD_CLASS, DB_FUNCTION"
                         * hasDefinition: true
                         * hasValue: false
                         * _key: "org__DOT__cmdbuild__DOT__multitenant__DOT__mode
                         */
                        if (me._blacklistKeys.indexOf(key) === -1) {
                            jsonResponse.data[key]._key = key.replace(/\./g, '__DOT__');
                            result.push(jsonResponse.data[key]);
                        }
                    });
                    deferred.resolve(result);

                    me._lastSyncDate = new Date().getTime();
                    me._config = result;
                },
                failure: function (reason) {
                    me._config = null;
                    me._lastSyncDate = null;
                }
            });
        }

        return deferred.promise;
    },

    /**
     *
     * @param {Object} theSetup
     * @param {Boolean} reloadOnSucces
     * @param {Boolean} forceDropCache
     * @param {Ext.app.ViewController} controller
     * @returns {Ext.Deferred}
     */
    setConfigs: function (theSetup, reloadOnSucces, forceDropCache, controller) {
        const deferred = new Ext.Deferred();

        const me = this;
        const data = {};
        const setupKeys = Ext.Object.getAllKeys(theSetup);

        me.getConfigs(true).then(function (currentConfigs) {
            Ext.Array.forEach(setupKeys, function (key) {
                const currentConfig = Ext.Array.findBy(currentConfigs, function (config) {
                    return config._key === key;
                });
                if (currentConfig) {
                    const currentValue = currentConfig.hasValue ? currentConfig.value : currentConfig['default'];
                    const fileOnly = currentConfig.location === 'file_only';
                    if (fileOnly || currentValue === theSetup[key]) {
                        return;
                    }
                }

                if (!Ext.String.startsWith(key, 'org__DOT__cmdbuild__DOT__multitenant__DOT__')) {
                    switch (theSetup[key]) {
                        case 'true':
                        case true:
                            data[key.replace(/\__DOT__/g, '.')] = 'true';
                            break;
                        case 'false':
                        case false:
                            data[key.replace(/\__DOT__/g, '.')] = 'false';
                            break;
                        default:
                            data[key.replace(/\__DOT__/g, '.')] = theSetup[key]; // value
                            break;
                    }
                }
            });

            if (!Ext.Object.isEmpty(data)) {
                /**
                 * save configuration via custom ajax call
                 */
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/system/config/_MANY', CMDBuildUI.util.Config.baseUrl),
                    method: 'PUT',
                    jsonData: data,
                    success: function (transport) {
                        const mainVm = Ext.ComponentQuery.query('viewport')[0].getViewModel();
                        if (mainVm && mainVm.get('theSetup')) {
                            const newSetup = {};
                            setupKeys.forEach(function (key) {
                                switch (theSetup[key]) {
                                    case 'true':
                                    case true:
                                        newSetup[key] = 'true';
                                        break;
                                    case 'false':
                                    case false:
                                        newSetup[key] = 'false';
                                        break;
                                    default:
                                        newSetup[key] = theSetup[key]; // value
                                        break;
                                }
                            });
                            mainVm.set('theSetup', newSetup);
                        }
                        if (forceDropCache) {
                            CMDBuildUI.util.administration.helper.AjaxHelper.dropCache().then(function () {
                                window.location.reload();
                            });
                        } else if (reloadOnSucces) {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                if (Ext.getBody().isMasked()) {
                                    Ext.getBody().unmask();
                                }

                                me.getConfigs(true).then(function () {
                                    CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(function () {
                                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                                        if (controller) {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                                'href',
                                                Ext.util.History.getToken(),
                                                controller
                                            );
                                        }
                                    });
                                });
                            });
                        } else {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                if (Ext.getBody().isMasked()) {
                                    Ext.getBody().unmask();
                                }
                                me.getConfigs(true).then(function () {
                                    CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(function () {
                                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                                        if (controller) {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                                'href',
                                                Ext.util.History.getToken(),
                                                controller
                                            );
                                        }
                                    });
                                });
                            });
                        }
                    }
                });
            } else {
                deferred.resolve();
            }
        });
        return deferred.promise;
    },

    /**
     *
     * @param {String} key
     * @param {String} value
     * @param {Boolean} logger
     */
    setConfig: function (key, value, logger) {
        const deferred = new Ext.Deferred();

        const me = this;
        const data = {};
        data[key.replace(/\__DOT__/g, '.')] = value;

        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request({
            url: Ext.String.format('{0}/system/config/_MANY', CMDBuildUI.util.Config.baseUrl),
            method: 'PUT',
            jsonData: data,
            success: function (transport) {
                if (logger) {
                    CMDBuildUI.util.Logger.log('Configuration saved', CMDBuildUI.util.Logger.levels.info);
                    CMDBuildUI.util.Logger.log(
                        'Some config changes need a refresh. Press F5',
                        CMDBuildUI.util.Logger.levels.info
                    );
                }
                me.getConfig(key, true, true).then(function (newValue) {
                    const mainVm = Ext.ComponentQuery.query('viewport')[0].getViewModel();
                    if (mainVm && mainVm.get('theSetup')) {
                        mainVm.set(Ext.String.format('theSetup.{0}', key), newValue);
                    }
                    deferred.resolve(newValue);
                });
            },
            error: function (error) {
                if (logger) {
                    CMDBuildUI.util.Logger.log(
                        'Configuration not saved. try again.',
                        CMDBuildUI.util.Logger.levels.error
                    );
                }
                deferred.reject(error);
            }
        });
        return deferred.promise;
    },

    /**
     *
     * @param {Object} theSetup
     * @param {Boolean} multitenantWasAlreadyActive
     * @returns {Ext.Deferred}
     */
    setMultitenantData: function (theSetup, multitenantWasAlreadyActive) {
        const me = this;
        const deferred = new Ext.Deferred();

        const data = {};
        const setupKeys = Ext.Object.getAllKeys(theSetup);

        setupKeys.forEach(function (key) {
            if (Ext.String.startsWith(key, 'org__DOT__cmdbuild__DOT__multitenant__DOT__')) {
                // remove not needed data
                switch (data.org__DOT__cmdbuild__DOT__multitenant__DOT__mode) {
                    case 'CMDBUILD_CLASS':
                        data.org__DOT__cmdbuild__DOT__multitenant__DOT__dbFunction = '';
                        break;
                    case 'DB_FUNCTION':
                        data.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantClass = '';
                        data.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantDomain = '';
                        break;
                    default:
                        break;
                }
                data[key.replace(/\__DOT__/g, '.')] = theSetup[key]; //value;
            }
        });

        me.setConfig('org.cmdbuild.multitenant.name', data['org.cmdbuild.multitenant.name']).then(function () {
            if (!multitenantWasAlreadyActive) {
                /**
                 * save configuration via custom ajax call
                 */
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/tenants/configure', CMDBuildUI.util.Config.baseUrl),
                    method: 'POST',
                    jsonData: data,
                    success: function (transport) {
                        deferred.resolve(transport);
                    },
                    failure: function (reason) {
                        deferred.reject(reason);
                    }
                });
            } else {
                deferred.resolve();
            }
        });

        return deferred.promise;
    }
});
