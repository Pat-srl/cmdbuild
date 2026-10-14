/**
 * @file CMDBuildUI.util.helper.SessionHelper
 * @module CMDBuildUI.util.helper.SessionHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.SessionHelper', {
    singleton: true,

    /**
     * @constant {String} authorization Header/Cookie name for authentication parameter.
     */
    authorization: 'CMDBuild-Authorization',

    /**
     * @constant {String} localization Header/Cookie name for language parameter.
     */
    localization: 'CMDBuild-Localization',

    /**
     * @private
     */
    logging: false,

    /**
     * Initialize session
     *
     * @private
     *
     * @param {String} token
     */
    initSession: function (token) {
        if (token) {
            CMDBuildUI.util.Ajax.sessionexpired = false;
            this.initCommunicationMethod();
        }
    },

    /**
     * Get current language.
     *
     * @param {Boolean} [isAuthenticated=undefined] Used to determinate from where get information.
     *
     * @returns {String} Current language.
     *
     * @example
     * const language = CMDBuildUI.util.helper.SessionHelper.getLanguage();
     */
    getLanguage: function (isAuthenticated) {
        let lang;
        if (isAuthenticated === undefined) {
            isAuthenticated = !!Ext.util.Cookies.get(CMDBuildUI.util.helper.SessionHelper.authorization);
        }

        if (isAuthenticated) {
            // when user is authenticated get the language from preferences
            lang = CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.language);
        } else {
            // when user is not authenticated get the language from the local storage
            lang = CMDBuildUI.util.helper.LocalStorageHelper.get(
                CMDBuildUI.util.helper.LocalStorageHelper.keys.loginlanguage
            );
        }

        // if language is not customized, uses the instance language
        if (!lang) {
            lang = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.defaultlanguage);
        }

        Ext.getDoc().dom.documentElement.setAttribute('lang', lang);
        return lang;
    },

    /**
     * Check the validity of the current session.
     *
     * @param {Object} [params] Configuration options for the session check request.
     * @param {Boolean} [params.operator] Whether to include operator information in the response.
     *
     * @returns {Ext.promise.Promise}
     *
     * @example
     * CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function () {
     *      // the session is valid
     * }, function () {
     *      // the session is not valid
     *      CMDBuildUI.util.Utilities.redirectTo('login', true);
     * });
     */
    checkSessionValidity: function (params) {
        const me = this;
        const deferred = new Ext.Deferred();

        params = params || {};

        function failure(response, opts) {
            const err = 'Session token expired.';
            CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
            Ext.asap(function () {
                deferred.reject(err);
                me._checksession = false;
            });
        }

        function success(response, opts) {
            const responseJson = JSON.parse(response.responseText);
            const session = Ext.create('CMDBuildUI.model.users.Session', responseJson.data);
            if (session.get('exists')) {
                if (
                    session.get('role') &&
                    (Ext.isEmpty(session.get('availableTenants')) ||
                        !Ext.isEmpty(session.get('activeTenants')) ||
                        session.get('ignoreTenants'))
                ) {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format(
                            'SessionHelper checkSessionValidity have id?: {0}',
                            session.getId() ? true : false
                        ),
                        CMDBuildUI.util.Logger.levels.debug
                    );

                    me.initCommunicationMethod();
                    CMDBuildUI.util.Logger.log(
                        'SessionHelper checkSessionValidity resolve promise',
                        CMDBuildUI.util.Logger.levels.debug
                    );

                    Ext.asap(function () {
                        deferred.resolve(session);
                        CMDBuildUI.util.Logger.log(
                            'SessionHelper checkSessionValidity promise resolved',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                    });
                } else {
                    const err = 'Group or tenant not selected.';
                    CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
                    Ext.asap(function () {
                        deferred.reject(session);
                    });
                }
            } else {
                failure();
            }
            me._checksession = false;
        }

        if (!this._checksession) {
            this._checksession = true;
            // get saved token
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getCurrentSessionUrl(),
                hideErrorNotification: true,
                method: 'GET',
                params: { ext: true, if_exists: true, operatorinfo: !!params.operator }
            }).then(success, failure);
        }

        return deferred.promise;
    },

    /**
     * Set session object into Viewport
     *
     * @private
     *
     * @param {CMDBuildUI.model.users.Session} session The session object.
     */
    setSessionIntoViewport: function (session) {
        const vm = this.getViewportVM();
        if (vm) {
            vm.set('theSession', session);
        }
    },

    /**
     * Get current session.
     *
     * @returns {CMDBuildUI.model.users.Session} Current session object.
     *
     * @example
     * const username = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("username");
     */
    getCurrentSession: function () {
        return this.getViewportVM().get('theSession');
    },

    /**
     * Update instance name.
     *
     * @private
     *
     * @param {String} instancename New instance name.
     */
    updateInstanceName: function (instancename) {
        this.getViewportVM().set('instancename', instancename);
        const title = Ext.getHead().child('title');
        if (title) {
            let text = CMDBuildUI.view.main.header.Logo.applicationname;
            if (instancename) {
                const stripedinstancename = Ext.util.Format.stripTags(instancename);
                text += ' - ' + stripedinstancename;
            }
            title.setText(text);
        }
    },

    /**
     * Update Change password visibility.
     *
     * @private
     *
     * @param {Boolean} value If false, user can not view change password button.
     */
    updateCanChangePasswordVisibility: function (value) {
        this.getViewportVM().set('changepasswordHidden', value);
    },

    /**
     * Update company logo.
     *
     * @private
     *
     * @param {String} companylogoid
     */
    updateCompanyLogoId: function (companylogoid) {
        this.getViewportVM().set('companylogoid', companylogoid);
    },

    /**
     * Update show login change language action.
     *
     * @private
     *
     * @param {Boolean} showLanguageSelector
     */
    updateLanguageInfo: function (showLanguageSelector) {
        this.getViewportVM().set('language', { default: this.getLanguage(), showselector: showLanguageSelector });
    },

    /**
     * Update scheduler info.
     *
     * @private
     *
     * @param {Boolean} enabled
     */
    updateSchedulerInfo: function (enabled) {
        this.getViewportVM().set('scheduler.enabled', enabled);
    },

    /**
     * Update ai chat bot info.
     *
     * @private
     *
     * @param {Boolean} enabled
     * @param {String} name The name of the AI chat bot.
     */
    updateChatBotAiInfo: function (enabled, name) {
        this.getViewportVM().set('chatBotAiEnabled', enabled);
        this.getViewportVM().set('chatBotAiName', name);
    },

    /**
     * Returrns true if the user is working in administration module.
     *
     * @returns {Boolean}
     */
    isAdministrationModule: function () {
        return !!this.getViewportVM().get('isAdministrationModule');
    },

    /**
     * Implementation of window.sessionStorage.setItem()
     *
     * @private
     *
     * @param {String} key The key.
     * @param {*} value The new associated value for `key`.
     *
     */
    setItem: function (key, value) {
        if (!this.localSessionStorage.id) {
            this.localSessionStorage = new Ext.util.LocalStorage({ id: this.LOCAL_STORAGE_ID, session: true });
        }
        this.localSessionStorage.setItem(key, Ext.JSON.encode(value));
    },

    /**
     * Implementation of window.sessionStorage.getItem()
     *
     * @private
     *
     * @param {String|Number} key The key.
     * @param {*} [defaultValue=null] The default associated value for `key`.
     * @returns {*}
     */
    getItem: function (key, defaultValue) {
        if (this.localSessionStorage.id) {
            return Ext.JSON.decode(this.localSessionStorage.getItem(key)) || defaultValue;
        }
        return defaultValue;
    },

    /**
     * Implementation of window.sessionStorage.removeItem()
     *
     * @private
     *
     * @param {String|Number} key The key.
     */
    removeItem: function (key) {
        if (this.localSessionStorage.id) {
            this.localSessionStorage.removeItem(key);
        }
    },

    /**
     * Load localization file.
     *
     * @private
     *
     * @param {String} lang
     *
     * @returns {Ext.promise.Promise}
     */
    loadLocale: function (lang) {
        const deferred = new Ext.Deferred();

        if (!lang) {
            lang = this.getLanguage();
        }
        if (lang && lang !== 'en') {
            Ext.require(
                [
                    Ext.String.format('CMDBuildUI.locales.{0}.LocalesAdministration', lang),
                    Ext.String.format('CMDBuildUI.locales.{0}.Locales', lang)
                ],
                function () {
                    deferred.resolve();
                }
            );

            Ext.Loader.loadScript({ url: Ext.String.format('app/locales/_ext/locale-{0}.js', lang) });
        } else {
            deferred.resolve();
        }

        return deferred.promise;
    },

    /**
     * Load custom components locale file.
     *
     * @private
     *
     * @returns {Ext.promise.Promise}
     */
    loadCustomLocale: function () {
        const deferred = new Ext.Deferred();

        Ext.require(['CMDBuildUI.locales.CustomLocales'], function () {
            deferred.resolve();
        });

        return deferred.promise;
    },

    /**
     * Load custom localization value, or default value if not found.
     *
     * @param {String} key
     * @param {String} defaultValue
     *
     * @returns {String}
     */
    getCustomLocalization: function (key, defaultValue) {
        const path = `CMDBuildUI.locales.CustomLocales.${key}`;

        try {
            const evalResult = eval(path);
            return Ext.isEmpty(evalResult) ? defaultValue : evalResult;
        } catch (e) {
            CMDBuildUI.util.Logger.log(
                Ext.String.format('Label {0} not found', path),
                CMDBuildUI.util.Logger.levels.error
            );
            return defaultValue;
        }
    },

    /**
     * Set the starting url.
     *
     * @private
     *
     * @param {String} url
     */
    setStartingUrl: function (url) {
        this._startingurl = url;
    },

    /**
     * Get starting url.
     *
     * @returns {String} Starting url.
     *
     * @example
     * const startingurl = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
     */
    getStartingUrl: function () {
        return this._startingurl;
    },

    /**
     * Sets current url as starting url.
     *
     * @private
     */
    updateStartingUrlWithCurrentUrl: function () {
        const currentUrl = Ext.History.getToken();
        if (currentUrl.length > 1 && currentUrl !== 'patches') {
            CMDBuildUI.util.helper.SessionHelper.setStartingUrl(currentUrl);
        }
    },

    /**
     * Clear starting url.
     *
     * @private
     */
    clearStartingUrl: function () {
        CMDBuildUI.util.helper.SessionHelper.setStartingUrl(null);
    },

    /**
     * Get active tenants for current user.
     *
     * @returns {Object[]} Active tenants.
     *
     * @example
     * const tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants();
     */
    getActiveTenants: function () {
        const session = this.getCurrentSession();
        const activetenants = session.get('activeTenants');
        const availabletenants = session.get('availableTenantsExtendedData');
        const ignoretenants = session.get('ignoreTenants');

        function activeTenantsFilter(value) {
            return ignoretenants || Ext.Array.contains(activetenants, value.code);
        }
        return availabletenants.filter(activeTenantsFilter);
    },

    /**
     * Update active tenants
     *
     * @private
     *
     * @param {String[]} tenants New active tenants
     */
    updateActiveTenants: function (tenants) {
        this.getCurrentSession().set('activeTenants', tenants);
    },

    /**
     * Initialize the communication method for the application:
     * either WebSocket or polling based on configuration.
     * * @param {Boolean} [forceValue] Optional: force a specific method (true for WS, false for Polling)
     */
    initCommunicationMethod: function (forceValue) {
        const isWebSocketEnabled =
            forceValue !== undefined
                ? forceValue
                : CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.services.websocketsEnabled);

        CMDBuildUI.util.Logger.log(
            Ext.String.format('Communication method: {0}', isWebSocketEnabled ? 'WebSocket' : 'Polling'),
            CMDBuildUI.util.Logger.levels.debug
        );

        try {
            if (isWebSocketEnabled) {
                this.initWebSocket();
            } else {
                this.initPolling();
            }
        } catch (e) {
            CMDBuildUI.util.Logger.log(
                'Error on creating CMDBuild communication method.',
                CMDBuildUI.util.Logger.levels.error,
                null,
                e
            );
        }
    },

    privates: {
        /**
         * An Object contains new Ext.util.LocalStorage
         * @type {Ext.util.LocalStorage}
         */
        localSessionStorage: {},
        /**
         * The id param used in new Ext.util.LocalStorage
         * @type {String}
         */
        LOCAL_STORAGE_ID: 'CMDBUILD-SESSION',

        /**
         * @property {String} _startingurl
         * The starting url
         */
        _startingurl: null,

        /**
         * @property {WebSocket} _socket
         * The web socket used by the application.
         */
        _socket: null,

        /**
         * @property {Number} _socketconnerrors
         * The number of connection errors on web socket
         */
        _socketconnerrors: 0,

        /**
         * @property {Number} _pollingTask
         * setInterval reference
         */
        _pollingTask: null,

        /**
         * @property {Array} _alreadySoundedMessageIds
         * Store the IDs of notifications that have already triggered the sound
         */
        _alreadySoundedMessageIds: [],

        /**
         * Get Viewport ViewModel
         *
         * @returns {CMDBuildUI.view.main.MainModel}
         */
        getViewportVM: function () {
            const viewports = Ext.ComponentQuery.query('viewport');
            if (viewports.length) {
                return viewports[0].getViewModel();
            }
        },

        /**
         *
         * @returns {Null} if the polling are already initialized
         */
        initPolling: function () {
            const me = this;
            me.stopPolling();

            if (this._pollingTask) {
                return;
            }

            const configInterval = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.services.pollingInterval
            );
            const intervalMs = (configInterval || 300) * 1000;

            this._pollingTask = setInterval(function () {
                CMDBuildUI.util.Logger.log('Polling tick', CMDBuildUI.util.Logger.levels.trace);

                const viewportVM = me.getViewportVM();
                if (viewportVM) {
                    const notificationStore = viewportVM.get('notificationStore');
                    if (notificationStore) {
                        notificationStore.load({
                            callback: function (records, operation, success) {
                                if (success && records) {
                                    me.handleIncomingMessages(records);
                                } else {
                                    CMDBuildUI.util.Logger.log(
                                        'Polling notifications failed',
                                        CMDBuildUI.util.Logger.levels.debug
                                    );
                                }
                            }
                        });
                    }
                }
            }, intervalMs);
        },

        /**
         *
         */
        initWebSocket: function () {
            this.stopPolling();

            if (!this._socket) {
                const me = this;
                const socket = (this._socket = new WebSocket(CMDBuildUI.util.Config.socketUrl));
                CMDBuildUI.util.Logger.log(
                    'CMDBuild websocket is now initialized.',
                    CMDBuildUI.util.Logger.levels.debug
                );

                // register on message event
                socket.addEventListener('message', function (e) {
                    const data = Ext.JSON.decode(e.data || '');
                    if (data && data.message && data.show_user) {
                        switch (data._event) {
                            case 'alert':
                                // compose message
                                const msg = me.formatMessage(data);
                                CMDBuildUI.util.Notifier.showInfoMessage(msg);

                                // reload notification store
                                if (data.messageId) {
                                    me.getViewportVM().get('notificationStore').load();
                                    CMDBuildUI.util.Utilities.playAlertNotificationSound();
                                }
                                break;
                            case 'chat':
                                const cl = CMDBuildUI.util.Chat.getChatConversationsList();
                                cl.fireEvent(
                                    'newmessagereceived',
                                    cl,
                                    Ext.create('CMDBuildUI.model.messages.Message', data)
                                );
                                break;
                        }
                    }
                });
                CMDBuildUI.util.Logger.log(
                    'CMDBuild websocket message event initialized',
                    CMDBuildUI.util.Logger.levels.debug
                );

                // send authentication to the socket
                socket.addEventListener('open', function (e) {
                    if (socket) {
                        socket.send(
                            Ext.JSON.encode({
                                _action: 'socket.session.login',
                                _id: CMDBuildUI.util.Utilities.generateUUID()
                            })
                        );
                    }
                    // reset connection errors
                    me._socketconnerrors = 0;
                });
                CMDBuildUI.util.Logger.log(
                    'CMDBuild websocket open event initialized',
                    CMDBuildUI.util.Logger.levels.debug
                );

                // register error event
                socket.addEventListener('error', function (e) {
                    me._socketconnerrors++;
                });

                // register close event
                socket.addEventListener('close', function (e) {
                    delete me._socket;
                    const times = me._socketconnerrors < 10 ? me._socketconnerrors : 10;
                    setTimeout(function () {
                        if (me._socketconnerrors < 25) {
                            me.initCommunicationMethod();
                        }
                    }, 30000 * times);
                });
            } else {
                CMDBuildUI.util.Logger.log('CMDBuild websocket alredy opened', CMDBuildUI.util.Logger.levels.debug);
            }
        },

        /**
         *
         */
        closeWebSocket: function () {
            if (this._socket) {
                this._socket.close();
                delete this._socket;
            }
        },

        /**
         *
         */
        stopPolling: function () {
            if (this._pollingTask) {
                clearInterval(this._pollingTask);
                this._pollingTask = null;
            }
        },

        /**
         * Handle incoming messages from polling or websocket
         * Manages notification display, sound alerts, and read/unread status
         *
         * @private
         * @param {Array} notifications Array of notification objects from server
         */
        handleIncomingMessages: function (notifications) {
            const me = this;

            if (!notifications || !Ext.isArray(notifications)) {
                return;
            }

            const newNotifications = notifications.filter(function (notification) {
                return notification.get('_isNew') === true;
            });

            // show red dot
            if (newNotifications.length > 0) {
                const notificationsToSound = newNotifications.filter(function (notification) {
                    return !Ext.Array.contains(me._alreadySoundedMessageIds, notification.get('messageId'));
                });

                // reproduce sound and show popup
                if (notificationsToSound.length > 0) {
                    CMDBuildUI.util.Utilities.playAlertNotificationSound();

                    notificationsToSound.forEach(function (notification) {
                        const msg = me.formatMessage(notification.getData());
                        CMDBuildUI.util.Notifier.showInfoMessage(msg);
                        me._alreadySoundedMessageIds.push(notification.get('messageId'));
                    });

                    CMDBuildUI.util.Logger.log(
                        Ext.String.format(
                            'Polling: {0} new notifications received, sound played for {1} notifications',
                            newNotifications.length,
                            notificationsToSound.length
                        ),
                        CMDBuildUI.util.Logger.levels.debug
                    );
                } else {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format(
                            'Polling: {0} new notifications received, but already played previously',
                            newNotifications.length
                        ),
                        CMDBuildUI.util.Logger.levels.debug
                    );
                }
            }

            // cleaning _alreadySoundedMessageIds
            const currentMessageIds = notifications.map(function (n) {
                return n.get('messageId');
            });

            me._alreadySoundedMessageIds = me._alreadySoundedMessageIds.filter(function (id) {
                return Ext.Array.contains(currentMessageIds, id);
            });
        },
        formatMessage: function (data) {
            let msg = '';
            if (data.subject) {
                msg += '<div style="margin-bottom: 5px;font-size: 110%;font-weight: bold;">' + data.subject + '</div>';
            }
            if (data.content) {
                msg += data.content;
            } else if (data.message) {
                msg += data.message;
            }
            return msg;
        }
    }
});
