/**
 * @file CMDBuildUI.util.Ajax
 * @module CMDBuildUI.util.Ajax
 * @author PAT srl
 * @access public
 */

Ext.define('CMDBuildUI.util.Ajax', {
    singleton: true,

    /**
     * Pending counter for automated test
     *
     * @private
     */
    currentPendingCount: 0,

    /**
     * @private
     */
    currentPendingDebug: false,

    /**
     * @private
     */
    currentPendingUrls: [],

    /**
     * @private
     */
    processStatAbort: 'proc.inst.abort',

    /**
     * Installs the Ext.Ajax.request override that automatically prepends
     * the /administration prefix where needed.
     * Must be called once at application startup, after Ext.Ajax.init().
     */
    installAdministrationPrefixInterceptor: function () {
        const originalRequest = Ext.Ajax.request.bind(Ext.Ajax);

        Ext.Ajax.request = function (options) {
            if (options && options.url) {
                const originalUrl = options.url;

                if (CMDBuildUI.util.Utilities.shouldAddAdministrationPrefix(originalUrl)) {
                    const baseUrl = CMDBuildUI.util.Config.baseUrl;
                    const path = originalUrl.startsWith(baseUrl) ? originalUrl.slice(baseUrl.length) : originalUrl;

                    options = Ext.apply({}, options);
                    options.url = baseUrl + '/administration' + path;
                }
            }

            return originalRequest(options);
        };
    },

    /**
     * Initialize Ajax for CMDBuild.
     *
     * @private
     */
    init: function () {
        Ext.Ajax.withCredentials = true;
        // initialize client ID
        this._clientId = CMDBuildUI.util.Utilities.generateRandomString(24);
        // init listeners
        this.initBeforeRequest();
        this.initRequestComplete();
        this.initRequestException();
    },

    /**
     * Initialize beforerequest event handler.
     *
     * @private
     */
    initBeforeRequest: function () {
        /**
         * Fired before a network request is made to retrieve a data object.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on(
            'beforerequest',
            function (conn, options, eOpts) {
                this.currentPending('add', arguments);
                // set headers
                var headers = Ext.applyIf(options.headers || {}, {
                    'Content-Type': 'application/json',
                    'CMDBuild-ActionId': CMDBuildUI.util.Ajax.getActionId(),
                    'CMDBuild-RequestId': CMDBuildUI.util.Utilities.generateUUID(),
                    'CMDBuild-View': CMDBuildUI.util.Ajax.getViewContext(),
                    'CMDBuild-ClientId': CMDBuildUI.util.Ajax.getClientId()
                });

                //remove _id from PUT request
                if (options.method === 'PUT') {
                    Ext.Array.each(options.records, function (value, index) {
                        if (options.jsonData && options.jsonData.hasOwnProperty('_id')) {
                            delete options.jsonData._id;
                        }
                    });
                }

                // add withCredentials property
                if (Ext.isEmpty(options.withCredentials)) {
                    options.withCredentials = true;
                }

                // merge options with custom headers
                Ext.merge(options, {
                    headers: headers
                });
            },
            this
        );
    },

    /**
     * Initialize requestcomplete event handler.
     *
     * @private
     */
    initRequestComplete: function () {
        /**
         * Fired if the request was successfully completed.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on(
            'requestcomplete',
            function (conn, response, options, eOpts) {
                this.currentPending('sub', arguments);
                this.showMessages(response, options);
            },
            this
        );
    },

    /**
     * Initialize requestexception event handler.
     *
     * @private
     */
    initRequestException: function () {
        /**
         * Fired if an error HTTP status was returned from the server.
         *
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on(
            'requestexception',
            function (conn, response, options, eOpts) {
                this.currentPending('sub', arguments);
                if (response.status === 401 && !CMDBuildUI.util.Ajax.sessionexpired) {
                    // Cmdb.Logger.debug("Got unauthorized (401) status from server, check session...");
                    CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(
                        function (session) {
                            CMDBuildUI.util.Ajax.sessionexpired = false;
                            CMDBuildUI.util.Logger.log(
                                'You cannot access this resource.',
                                CMDBuildUI.util.Logger.levels.warn,
                                401
                            );
                        },
                        function (session) {
                            CMDBuildUI.util.Ajax.sessionexpired = true;
                            CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport();
                            if (!CMDBuildUI.util.helper.SessionHelper.getStartingUrl()) {
                                CMDBuildUI.util.helper.SessionHelper.updateStartingUrlWithCurrentUrl();
                            }
                            if (CMDBuildUI.util.Ajax.getActionId() !== 'login') {
                                CMDBuildUI.util.Utilities.redirectTo('login');
                            }
                        }
                    );
                } else if (response.status !== 401) {
                    this.showMessages(response, options);
                }
            },
            this
        );
    },

    /**
     *
     * Get Javascript global variable tracking the number of pending http requests from client (issue #710)
     *
     * See CMDBuildUI.util.Ajax.currentPending(null|add|sub|subtract|enable-debug|disable-debug|reset);
     *
     * @private
     *
     * @param {String} operation
     * @returns {Number} currentPendingCount
     */
    currentPending: function (operation, args) {
        switch (operation) {
            case 'add':
                if (args && args[1] && args[1].url) {
                    Ext.Array.push(this.currentPendingUrls, args[1].url);
                }
                this.currentPendingCount++;
                break;
            case 'sub':
            case 'subtract':
                if (args && args[2] && args[2].url) {
                    Ext.Array.remove(this.currentPendingUrls, args[2].url);
                }
                this.currentPendingCount--;
                break;
            case 'enable-debug':
                this.currentPendingDebug = true;
                break;
            case 'disable-debug':
                this.currentPendingDebug = false;
                break;
            case 'reset':
                Ext.Array.clean(this.currentPendingUrls);
                this.currentPendingCount = 0;
                break;
            case 'count':
                return this.currentPendingCount;
            case 'list':
                return this.currentPendingUrls;
        }
        if (this.currentPendingDebug) {
            CMDBuildUI.util.Logger.log(
                Ext.String.format('Pending ajax: {0}', this.currentPendingCount),
                CMDBuildUI.util.Logger.levels.debug
            );
            Ext.Array.forEach(this.currentPendingUrls, function (url) {
                CMDBuildUI.util.Logger.log(url, CMDBuildUI.util.Logger.levels.debug);
            });
            CMDBuildUI.util.Logger.log('', CMDBuildUI.util.Logger.levels.debug);
        }
    },

    /**
     * Returns the current action id.
     *
     * @returns {String}
     *
     * @example
     * CMDBuildUI.util.Ajax.getActionId();
     */
    getActionId: function () {
        return this._actionid;
    },

    /**
     * Set the request action id.
     *
     * @param {String} actionid
     *
     * @example
     * CMDBuildUI.util.Ajax.setActionId("class.card.edit");
     */
    setActionId: function (actionid) {
        this._actionid = actionid;
    },

    /**
     * Returns the client id used for Ajax requests.
     *
     * @returns {String} client Id
     */
    getClientId: function () {
        return this._clientId;
    },

    /**
     * Update Ajax Timeout.
     *
     * @private
     */
    updateAjaxTimeout: function () {
        var timeout_s =
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.ajaxtimeout) || 60;
        CMDBuildUI.util.Config.ajaxTimeout = timeout_s * 1000;
        Ext.Ajax.setTimeout(CMDBuildUI.util.Config.ajaxTimeout);
    },

    privates: {
        _actionid: null,
        /**
         * @returns {String} admin|default
         */
        getViewContext: function () {
            var vm = CMDBuildUI.util.helper.SessionHelper.getViewportVM();
            if (vm && vm.get('isAdministrationModule')) {
                return 'admin';
            }
            return 'default';
        },

        showMessages: function (response, options) {
            var messages = CMDBuildUI.util.Ajax.getResponseMessage(response);
            if (messages) {
                for (var k in messages) {
                    if (options.hideErrorNotification) {
                        var level;
                        switch (k) {
                            case 'WARNING':
                                level = CMDBuildUI.util.Logger.levels.warn;
                                break;
                            case 'ERROR':
                                level = CMDBuildUI.util.Logger.levels.error;
                                break;
                            case 'INFO':
                                level = CMDBuildUI.util.Logger.levels.info;
                                break;
                            default:
                                level = CMDBuildUI.util.Logger.levels.info;
                        }
                        CMDBuildUI.util.Logger.log(messages[k].message, level, messages[k].code);
                    } else if (response.status !== -1) {
                        var notifier;
                        switch (k) {
                            case 'WARNING':
                                notifier = CMDBuildUI.util.Notifier.showWarningMessage;
                                break;
                            case 'ERROR':
                                notifier = CMDBuildUI.util.Notifier.showErrorMessage;
                                break;
                            case 'INFO':
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                                break;
                            default:
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                        }
                        notifier(messages[k].usermessage, messages[k].code, undefined, messages[k].message);
                    }
                }
            }
        },

        toISOStringWithTimezone: function (date) {
            if (!date) return date;
            /* extract GTM timezone from date.toString() */
            var regex = /(?:GMT)([-+]\d*)/gm;
            var gtm = regex.exec(date.toString())[1];
            /* replace Z (UTC) to respective TimeZone */
            var finaldate = date.toISOString().replace('Z', gtm);
            return finaldate;
        },

        /**
         * @param {Object} response
         * @returns {Object} An object containing error message and error code.
         */
        getResponseMessage: function (response) {
            if (!response.responseText && !response.responseJson) {
                return false;
            }
            let oresponse = response.responseType === 'json' ? response.responseJson : response.responseText;
            if (response.responseText) {
                if (!Ext.isObject(oresponse)) {
                    oresponse = Ext.JSON.decode(oresponse, true);
                }
            }
            let errors = false;
            if (oresponse && oresponse.messages) {
                errors = {};
                const usermessages = {};
                const messages = {};
                let reqid = '';

                const today = Ext.util.Format.date(
                    new Date(),
                    CMDBuildUI.util.helper.UserPreferences.getTimestampWithSecondsFormat()
                );

                if (
                    (response.request && response.request.headers) ||
                    (response.getResponseHeader && response.getResponseHeader('CMDBuild-RequestId'))
                ) {
                    const header = response.request
                        ? response.request.headers['CMDBuild-RequestId']
                        : response.getResponseHeader('CMDBuild-RequestId');
                    const requestId = (header || '').toString().trim();
                    reqid = Ext.String.format(
                        [
                            '<div>',
                            '    <p>',
                            '        <b>Request ID:</b> <code>{0}</code>',
                            '    </p>',
                            '    <p>',
                            '        <b>Date:</b> <code>{1}</code>',
                            '    </p>',
                            '</div>'
                        ].join(''),
                        requestId,
                        today
                    );
                }
                oresponse.messages.forEach(function (m) {
                    if (!usermessages[m.level]) {
                        usermessages[m.level] = [];
                    }
                    if (!messages[m.level]) {
                        messages[m.level] = [];
                    }
                    if (m.show_user) {
                        if (reqid) {
                            usermessages[m.level].reqid = reqid;
                        }
                        if (m._message_translation) {
                            usermessages[m.level].message = m._message_translation;
                        } else {
                            usermessages[m.level].message = m.message;
                        }
                        if (m.code) {
                            usermessages[m.level].code = m.code;
                        }
                    } else {
                        messages[m.level].push(m.message);
                    }
                });

                for (let k1 in usermessages) {
                    errors[k1] = {};
                    if (usermessages[k1].message && usermessages[k1].reqid) {
                        errors[k1].usermessage = Ext.String.format(
                            '{0}<p><b>Info:</b> <code>{1}</code></p>',

                            usermessages[k1].reqid,
                            usermessages[k1].message
                        );
                        if (usermessages[k1].code) {
                            // Add cmdbuild error code
                            errors[k1].code = 'CM ' + usermessages[k1].code;
                        }
                    } else {
                        switch (k1) {
                            case 'WARNING':
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericwarning;
                                break;
                            case 'ERROR':
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericerror;
                                break;
                            case 'INFO':
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericinfo;
                                break;
                            default:
                                errors[k1].usermessage = '';
                        }
                    }
                }

                for (let k2 in messages) {
                    if (!errors[k2]) {
                        errors[k2] = {};
                    }
                    if (messages[k2].length) {
                        errors[k2].message = messages[k2].join('<br />');
                    } else {
                        errors[k2].message = response.statusText;
                    }
                }
            }
            return errors;
        }
    }
});
