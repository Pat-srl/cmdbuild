/**
 * @file CMDBuildUI.util.Utilities
 * @module CMDBuildUI.util.Utilities
 * @author PAT srl
 * @access public
 */
/**
 * @typedef Link
 * @type {Object}
 * @property {String} url The url of the link
 * @property {String} label The label of the link
 *
 * @memberof CMDBuildUI.util.Utilities
 */
Ext.define('CMDBuildUI.util.Utilities', {
    singleton: true,

    /**
     * @private
     */
    popupOpened: {},

    /**
     * @private
     */
    loadmask: {},

    // Endpoints available in both administration and management contexts.
    // When called from the administration module, these are routed to /administration/*.
    // When called from the management module, the path is left unchanged.
    MIXED_ENDPOINTS: [
        /\/?v\d+\/async\/?/, // async/ → AsyncOperationWs
        /\/?v\d+\/bim\/projects\/?/, // bim/projects/ → BimProjectWs
        /\/?v\d+\/calendar\/triggers\/?/, // calendar/triggers/ → CalendarTriggerWs
        /\/?v\d+\/(?:classes|processes)\/[\w\-_\.]+\/attributes\/?/, // {classes|processes}/{id}/attributes/ → ClassAttributeWs
        /\/?v\d+\/(?:classes|processes|views)\/[\w\-_\.]+\/filters\/?/, // {classes|processes|views}/{id}/filters/ → ClassFilterWs
        /\/?v\d+\/(?:classes|processes)\/[\w\-_\.]+\/domains\/?/, // {classes|processes}/{id}/domains/ → ClassOrProcessDomainsWs
        /\/?v\d+\/(?:classes|processes)\/[\w\-_\.]+\/geoattributes\/?/, // {classes|processes}/{id}/geoattributes/ → GeoAttributeWs
        /\/?v\d+\/(?:classes|processes)\/[\w\-_\.]+\/(?:cards|instances)\/[\w\-_\.]+\/geolayers\/?/, // {classes|processes}/{id}/{cards|instances}/{id}/geolayers/ → GeoserverLayerWs
        /\/?v\d+\/(?:classes|processes)(?:\/[\w\-_.]+)?\/?$/, // {classes|processes}/{id}/ → ClassWs / ProcessWs
        /\/?v\d+\/components\/contextmenu\/?/, // components/contextmenu → ContextMenuComponentWs
        /\/?v\d+\/components\/core\/[\w\-_\.]+\/?/, // components/core/{type}/ → CoreComponentWs
        /\/?v\d+\/components\/widget\/?/, // components/widget → WidgetWs
        /\/?v\d+\/custompages\/?/, // custompages/ → CustomPageWs
        /\/?v\d+\/dashboards\/?/, // dashboards/ → DashboardWs
        /\/?v\d+\/homepages\/?/, // homepages/ → HomepagesWs
        /\/?v\d+\/dms\/categories\/[\w\-_\.]+\/values\/?/, // dms/categories/{id}/values/ → DmsCategoryValueWs
        /\/?v\d+\/dms\/categories\/?/, // dms/categories/ → DmsCategoryWs
        /\/?v\d+\/dms\/models\/?/, // dms/models/ → DmsModelWs
        /\/?v\d+\/domains\/[\w\-_\.]+\/attributes\/?/, // domains/{id}/attributes/ → DomainAttributeWs
        /\/?v\d+\/domains\/?/, // domains/ → DomainWs
        /\/?v\d+\/email\/accounts\/?/, // email/accounts/ → EmailAccountWs
        /\/?v\d+\/email\/signatures\/?/, // email/signatures/ → EmailSignatureWs
        /\/?v\d+\/email\/templates\/?/, // email/signatures/ → EmailSignatureWs
        /\/?v\d+\/etl\/gates\/?/, // etl/gates/ → EtlGateWs
        /\/?v\d+\/etl\/templates\/?/, // etl/templates/ → EtlTemplateWs
        /\/?v\d+\/etl\/webhook\/?/, // etl/webhook/ → EtlWebhookWs
        /\/?v\d+\/fkdomains\/?/, // fkdomains/ → FkDomainWs
        /\/?v\d+\/lookup_types\/[\w\-_\.]+\/values\/?/, // lookup_types/{id}/values/ → LookupValueWs
        /\/?v\d+\/lookup_types\/?/, // lookup_types/ → LookupTypeWs
        /\/?v\d+\/menu\/?/, // menu/ → MenuWs
        /\/?v\d+\/plugin\/[\w_\.]+\/config\/?/, // /plugin/{id}/config → PluginConfigWs
        /\/?v\d+\/domainTrees\/?/, // domainTrees → NavTreeWs
        /\/?v\d+\/offline\/?/, // offline/ → OfflineWs
        /\/?v\d+\/processes\/[\w\-_\.]+\/activities\/?/, // processes/{id}/activities/ → ProcessTaskDefinitionWs
        /\/?v\d+\/reports\/?/, // reports → ReportWs
        /\/?v\d+\/roles\/?/, // roles/ → RoleWs
        /\/?v\d+\/tenants\/?/, // tenants/ → TenantWs
        /\/?v\d+\/translations\/?/, // translations/ → TranslationsWs
        /\/?v\d+\/(?:uploads|downloads)\/?/, // uploads/ | downloads/ → UploadWs
        /\/?v\d+\/users\/?/, // users/ → UserWs
        /\/?v\d+\/views\/?/ // views/ → ViewWs
    ],

    // Endpoints that exist exclusively in the administration namespace.
    // Requests to these paths are always routed to /administration/*,
    // regardless of the current UI context.
    ADMIN_ONLY_ENDPOINTS: [
        /\/?v\d+\/email\/error\/?/, // administration/email/error/ → EmailErrorWs
        /\/?v\d+\/email\/queue\/?/, // administration/email/queue/ → EmailQueueWs
        /\/?v\d+\/etl\/configs\/?/, // administration/etl/configs/ → EtlConfigWs
        /\/?v\d+\/etl\/messages\/?/, // administration/etl/messages/ → EtlMessageWs
        /\/?v\d+\/globalsearch\/?/, // administration/globalsearch → GlobalSearchWs
        /\/?v\d+\/roles\/[\w\-_\.]+\/grants\/?/, // administration/roles/{roleId}/grants/ → GrantWs
        /\/?v\d+\/jobs\/?/, // administration/jobs/ → JobsWs
        /\/?v\d+\/languages\/?/, // administration/languages → LanguagesWs
        /\/?v\d+\/locks\/?/, // administration/locks → LockWs
        /\/?v\d+\/plugin\/[\w\-_\.]+\/config\/?/, // administration/plugin/config → PluginConfigWs
        /\/?v\d+\/roles\/[\w\-_\.]+\/filters\/?/, // administration/roles/{roleId}/filters → RoleClassFilterWs
        /\/?v\d+\/schema\/collect\/?/, // administration/schema/collect → SchemaCollectorWs
        /\/?v\d+\/schema\/?/, // administration/schema/ → SchemaWs
        /\/?v\d+\/search\/?/, // administration/search → SearchWs
        /\/?v\d+\/system\/config\/?/, // administration/system/config → SystemConfigWs
        /\/?v\d+\/system\/plugins\/?/ // administration/system/plugins → SystemPluginsWs
    ],

    /**
     * Update the hash. By default, it will not execute the routes if the current token and the
     * token passed are the same.
     *
     * @param {String/Ext.data.Model} token The token to redirect to.  Can be either a String
     * or a {@link Ext.data.Model Model} instance - if a Model instance is passed it will
     * internally be converted into a String token by calling the Model's
     * {@link Ext.data.Model#toUrl toUrl} function.
     *
     * @param {Boolean} force Force the update of the hash regardless of the current token.
     *
     * @returns {Boolean} Will return `true` if the token was updated.
     *
     * @example
     * CMDBuildUI.util.Utilities.redirectTo("login");
     */
    redirectTo: function (token, force) {
        if (token.isModel) {
            token = token.toUrl();
        }

        var isCurrent = Ext.util.History.getToken() === token,
            ret = false;

        if (!isCurrent) {
            ret = true;
            Ext.util.History.add(token);
        } else if (force) {
            ret = true;
            Ext.route.Router.onStateChange(token);
        }
        return ret;
    },

    /**
     * Open a popup.
     *
     * @param {String} popupId If null, the id will be generated by the system.
     * @param {String} title Popup title.
     * @param {Object|Ext.Component} content Definition of the content to show in the popup.
     * @param {Object} [listeners] Listeners to add to the popup
     * @param {Object} [config] Other configurations to add to the popup. See {@link Ext.panel.Panel}.
     *
     * @example
     * var popup = CMDBuildUI.util.Utilities.openPopup(
     *      null,
     *      "My popup", {
     *          xtype: "panel",
     *          text: "Hello CMDBuild!"
     *      }
     * );
     */
    openPopup: function (popupId, title, content, listeners, config) {
        var me = this;
        popupId = !popupId ? 'popup-panel-' + (Ext.Object.getSize(this.popupOpened) + 1) : popupId.replace(/\./g, '-');
        listeners = listeners || {};

        listeners = Ext.applyIf(listeners, {
            closed: function () {
                this.close();
            },
            beforedestroy: function () {
                this.removeAll(true);
                me._popupAlwaysOnTop--;
            }
        });

        config = Ext.applyIf(config || {}, {
            id: popupId,
            title: title,

            width: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.popupwindow.width) + '%',
            height:
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.popupwindow.height) + '%',
            ui: CMDBuildUI.util.Ajax.getViewContext() === 'admin' ? 'administration' : 'management',
            floating: true,
            closable: true,
            modal: true,
            layout: 'fit',
            alwaysOnTop: me._popupAlwaysOnTop++,
            resizable: true,
            draggable: true,
            userCls: 'cmdbuild-popup',

            items: [content],
            listeners: listeners,

            autoEl: {
                'data-testid': 'popup'
            },

            /**
             * Set position within body
             *
             * @param {Number} x
             * @param {Number} y
             */
            afterSetPosition: function (x, y) {
                var newx, newy;
                var viewsize = Ext.getBody().getViewSize();
                if (x < 0) {
                    newx = '0';
                } else if (x > viewsize.width - this.getWidth()) {
                    newx = viewsize.width - this.getWidth();
                }
                if (y < 0) {
                    newy = '0';
                } else if (y > viewsize.height - this.getHeight()) {
                    newy = viewsize.height - this.getHeight();
                }

                if (newx !== undefined || newy !== undefined) {
                    newx = newx || x;
                    newy = newy || y;
                    this.setPosition(newx, newy);
                }

                //added old code
                var me = this;
                me.onPosition(x, y);
                if (me.hasListeners.move) {
                    me.fireEvent('move', me, x, y);
                }
            },

            /**
             * Set width and height within body
             *
             * @param {Number} width
             * @param {Number} height
             * @param {Number} oldWidth
             * @param {Number} oldHeight
             */
            onResize: function (width, height, oldWidth, oldHeight) {
                var viewsize = Ext.getBody().getViewSize();
                if (width > viewsize.width) {
                    this.setWidth(viewsize.width);
                }
                if (height > viewsize.height) {
                    this.setHeight(viewsize.height);
                }
            }
        });

        var panel = new Ext.panel.Panel(config);

        panel.show();
        this.popupOpened[popupId] = panel;
        return panel;
    },

    /**
     * Close the popup by its id.
     *
     * @param {String|Number} popupId
     *
     * @returns {Boolean}
     *
     * @example
     * CMDBuildUI.util.Utilities.closePopup("my-popup");
     */
    closePopup: function (popupId) {
        popupId = popupId.replace(/\./g, '-');
        if (this.popupOpened[popupId] && typeof this.popupOpened[popupId].destroy == 'function') {
            this.popupOpened[popupId].destroy();
        }
        return delete this.popupOpened[popupId];
    },

    /**
     * Close all opened popups
     */
    closeAllPopups: function () {
        Ext.query('[data-testid="popup"]', false).forEach(function (popup) {
            if (popup && popup.component) {
                popup.component.close();
            }
        });
    },

    /**
     * Generate an UUID.
     *
     * @returns {String} The generated UID.
     *
     * @example
     * CMDBuildUI.util.Utilities.generateUUID();
     */
    generateUUID: function () {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = ((d + Math.random() * 16) % 16) | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3) | 0x8).toString(16);
        });
        return uuid;
    },

    /**
     * Generate a random string.
     *
     * @param {Integer} length
     *
     * @returns {String} The random string.
     *
     * @example
     * var clientId = CMDBuildUI.util.Utilities.generateRandomString(24);
     */
    generateRandomString: function (length) {
        var result = '';
        var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var charactersLength = characters.length;
        for (var i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    },

    /**
     * Add loading mask on component.
     *
     * @param {Ext.component.Component} element The conmponent on which apply the mask.
     * @param {Object} config Configuration of the load mask. See {@link Ext.LoadMask}.
     *
     * @returns {Ext.LoadMask}
     *
     * @example
     * var loader = CMDBuildUI.util.Utilities.addLoadMask(view);
     */
    addLoadMask: function (element, config) {
        // add load mask
        config = Ext.applyIf(
            {
                target: element
            },
            config || {}
        );
        var loadmask = new Ext.LoadMask(config);
        loadmask.show();
        return loadmask;
    },

    /**
     * Remove loading mask.
     *
     * @param {Ext.LoadMask} loadmask The load mask to remove.
     *
     * @example
     * CMDBuildUI.util.Utilities.removeLoadMask(loader);
     */
    removeLoadMask: function (loadmask) {
        loadmask.destroy();
    },

    /**
     * Convert utf-8 string to hex string.
     *
     * @param {String} s The string to transform.
     *
     * @returns {String}
     *
     * @example
     * var hex_lookupType = CMDBuildUI.util.Utilities.stringToHex(lookupType);
     */
    stringToHex: function (s) {
        if (s && !Ext.String.startsWith(s, 'HEX')) {
            // utf8 to latin1
            s = unescape(encodeURIComponent(s));
            var h = '';
            for (var i = 0; i < s.length; i++) {
                h += s.charCodeAt(i).toString(16);
            }
            return 'HEX' + h;
        }
        return s;
    },

    /**
     * Convert hex string to utf-8 string.
     *
     * @param {String} s
     *
     * @returns {String}
     *
     * @example
     * var lookupType = CMDBuildUI.util.Utilities.hexToString(hex_lookupType);
     */
    hexToString: function (h) {
        var s = '';
        if (h && Ext.String.startsWith(h, 'HEX')) {
            h = h.replace('HEX', '');
            for (var i = 0; i < h.length; i += 2) {
                s += String.fromCharCode(parseInt(h.substr(i, 2), 16));
            }
            return decodeURIComponent(escape(s));
        }
        return s;
    },

    /**
     * Remove special characters from string.
     * Leaves only characters from a to z, numbers and _
     *
     * @param {String} str
     *
     * @returns {String}
     *
     * @example
     * var str = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(myString);
     */
    stringRemoveSpecialCharacters: function (str) {
        return str.replace(/[^a-zA-Z0-9_]/g, '');
    },

    /**
     * Convert major-minor chars into legit html &gt; &lt;
     *
     * @param {String} str
     *
     * @returns {String}
     *
     * @example
     * var t = CMDBuildUI.util.Utilities.transformMajorMinor(text);
     */
    transformMajorMinor: function (str) {
        if (str) {
            if (str.indexOf('<') != -1) {
                str = str.replace(/</g, '&lt;');
            }
            if (str.indexOf('>') != -1) {
                str = str.replace(/>/g, '&gt;');
            }
        }
        return str;
    },

    /**
     * Check boot status.
     *
     * @private
     *
     * @returns {Ext.promise.Promise}
     */
    checkBootStatus: function () {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getBootStatusUrl(),
            method: 'GET'
        }).then(
            function (response, opts) {
                if (response.status === 200 && response.responseText) {
                    const jsonresponse = Ext.JSON.decode(response.responseText);
                    const status = jsonresponse.status;
                    CMDBuildUI.util.Config.bootstatus = status;
                    if (status === 'READY_RESTART_REQUIRED') {
                        CMDBuildUI.util.Navigation.getMainContainer().getViewModel().set('isNecessaryReload', true);
                    } else if (status !== 'READY') {
                        deferred.reject();
                    }
                }
                deferred.resolve();
            },
            function () {
                deferred.resolve();
            }
        );
        return deferred.promise;
    },

    /**
     * Compare tho objects and return a new object with the changes.
     *
     * @param {Object} newobj
     * @param {Object} oldobj
     *
     * @returns {Object}
     *
     * @example
     * var changed = CMDBuildUI.util.Utilities.getObjectChanges(newdata, olddata);
     */
    getObjectChanges: function (newobj, oldobj) {
        var changes = {};
        oldobj = oldobj || {};
        // get new or changed values
        for (var key in newobj) {
            if (newobj[key] != oldobj[key]) {
                changes[key] = newobj[key];
            }
        }
        // get removed values
        var oldkeys = Ext.Array.difference(Ext.Object.getAllKeys(oldobj), Ext.Object.getAllKeys(newobj));
        oldkeys.forEach(function (k) {
            changes[k] = undefined;
        });
        return changes;
    },

    /**
     * Open "Print" popup.
     *
     * @param {String} url
     *
     * @example
     * CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams), "csv");
     */
    openPrintPopup: function (url, format) {
        CMDBuildUI.util.Utilities.openPopup(null, CMDBuildUI.locales.Locales.common.grid.print, {
            tbar: [
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'button',
                    ui: 'management-neutral-action',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    itemId: 'downloadbtn',
                    tooltip: CMDBuildUI.locales.Locales.reports.download,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.reports.download'
                    },
                    listeners: {
                        afterrender: function (view) {
                            view.el.set(
                                {
                                    download: CMDBuildUI.locales.Locales.common.grid.print
                                },
                                false
                            );
                        },
                        click: function () {
                            var button = this;
                            Ext.asap(function () {
                                button.setDisabled(true);
                                CMDBuildUI.util.File.download(
                                    url,
                                    format,
                                    false,
                                    {
                                        _download: true
                                    },
                                    {
                                        skipUrlEncode: true
                                    }
                                ).then(function (success) {
                                    button.setDisabled(false);
                                });
                            });
                        }
                    }
                }
            ],
            items: [
                {
                    xtype: 'uxiframe',
                    width: '100%',
                    height: '100%',
                    ariaAttributes: {
                        role: 'document'
                    },
                    listeners: {
                        afterrender: function (view) {
                            var frameurl = url;
                            if (format === 'csv') {
                                frameurl += '&_contenttype=text/plain';
                            }
                            view.load(frameurl);
                        }
                    }
                }
            ]
        });
    },

    /**
     * Show loader on element.
     *
     * @param {Boolean} [show=false]
     * @param {Ext.Component} [element=Viewport]
     *
     * @example
     * CMDBuildUI.util.Utilities.showLoader(true);
     */
    showLoader: function (show, element) {
        var me = this;
        var window;
        Ext.WindowMgr.getBy(function (item) {
            if (['toast', 'quicktip'].indexOf(item.xtype) === -1) {
                window = item;
            }
        });
        var comp = element || window || Ext.ComponentQuery.query('viewport')[0];
        if (show) {
            if (!me.loadmask[comp.id]) {
                me.loadmask[comp.id] = CMDBuildUI.util.Utilities.addLoadMask(comp);
            }
        } else {
            if (me.loadmask[comp.id]) {
                CMDBuildUI.util.Utilities.removeLoadMask(me.loadmask[comp.id]);
                delete me.loadmask[comp.id];
            }
        }
    },

    /**
     * Utility used to validate password with configured parameters.
     *
     * @param {String} newpassword
     * @param {String} oldpassword
     * @param {String} username
     *
     * @returns {Boolean} true if the password is valid.
     *
     * @example
     * var isValid = CMDBuildUI.util.Utilities.validatePassword(data.newpassword, data.oldpassword, data.username);
     */
    validatePassword: function (newpassword, oldpassword, username) {
        if (
            newpassword &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.enabled)
        ) {
            var errors = [];
            // get configuration
            var minlength = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.minlength
                ),
                diffprevious = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.diffprevious
                ),
                diffusername = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.diffusername
                ),
                reqdigit = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.reqdigit
                ),
                reqlowercase = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.reqlowercase
                ),
                requppercase = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.passwordrules.requppercase
                );

            // check minimum length
            if (minlength && newpassword.length < minlength) {
                errors.push(Ext.String.format(CMDBuildUI.locales.Locales.main.password.err_length, minlength));
            }

            // check different from previous
            if (diffprevious && newpassword === oldpassword) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_diffprevious);
            }

            // check different from previous
            if (diffusername && newpassword.toLowerCase() === username.toLowerCase()) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_diffusername);
            }

            // check required digit
            if (reqdigit && !/\d/.test(newpassword)) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_reqdigit);
            }

            // check required lowercase
            if (reqlowercase && !/[a-z]/.test(newpassword)) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_reqlowercase);
            }

            // check required uppercase
            if (requppercase && !/[A-Z]/.test(newpassword)) {
                errors.push(CMDBuildUI.locales.Locales.main.password.err_requppercase);
            }
            return errors.length ? errors.join('<br />') : true;
        }
        return true;
    },

    /**
     * Extract text from HTML.
     *
     * @param {String} html
     * @param {Boolean} [space=false] `true` to insert spaces between nodes.
     *
     * @returns {String}
     *
     * @example
     * var text = CMDBuildUI.util.Utilities.extractTextFromHTML(html);
     */
    extractTextFromHTML: function (html, space) {
        if (html) {
            var span = document.createElement('span');
            span.innerHTML = html;
            if (space) {
                var children = span.querySelectorAll('*');
                for (var i = 0; i < children.length; i++) {
                    if (children[i].textContent) children[i].textContent += ' ';
                    else children[i].innerText += ' ';
                }
            }
            return [span.textContent || span.innerText].toString().replace(/ +/g, ' ');
        }
    },

    /**
     * Remove dangerous tag attributes from HTML string.
     *
     * @param {String} text HTML string.
     *
     * @returns {String} Safe HTML.
     *
     * @example
     * var text = 'This is a dangerous HTML <img src=x onerror="alert(\'ALERT!\')"/>';
     * var safe_html = CMDBuildUI.util.Utilities.removeEventAttributesFromHTML(html);
     */
    removeEventAttributesFromHTML: function (text) {
        return text.replace(
            /(?<!\w)on[\w]+(\s*)=(\s*)("*|'*)(.*?)("*|'*)|(\s*)("*|'*)(\s*)javascript:(.*?)("*|'*)/g,
            ''
        );
    },

    /**
     * Extract URL and Text from an html link.
     *
     * @param {String} link The string representing the <a></a> tag.
     *
     * @returns  {Link}
     *
     * @example
     * var link = CMDBuildUI.util.Utilities.extractUrlAndLabelFromLink('<a href="https://tecnoteca.com">Tecnoteca website</a>'),
     *     url = link.url,
     *     label = link.label;
     */
    extractUrlAndLabelFromLink: function (link) {
        var extract = /<a.*href="([^"]+)?".*>([^<]+)?<\/a>/.exec(link);
        return {
            url: extract ? Ext.util.Format.htmlDecode(extract[1]) : null,
            label: extract ? extract[2] : null
        };
    },

    /**
     * Create html for link with given url and label.
     *
     * @param {String} url The url of the link.
     * @param {String} label The label of the link.
     *
     * @returns {String}
     *
     * @example
     * var link = CMDBuildUI.util.Utilities.createLinkByUrlAndLabel('https://tecnoteca.com, 'Tecnoteca website');
     */
    createLinkByUrlAndLabel: function (url, label) {
        if (url || label) {
            return Ext.String.format(
                '<a href="{0}" target="_blank" rel="noopener noreferrer">{1}</a>',
                url,
                label || url
            );
        }
        return null;
    },

    /**
     * Get the tenant field lable.
     *
     * @returns {String}
     *
     * @example
     * var label = CMDBuildUI.util.Utilities.getTenantLabel();
     */
    getTenantLabel: function () {
        return (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.name) ||
            CMDBuildUI.locales.Locales.administration.common.labels.tenant
        );
    },

    /**
     * Get string for a date relative at now.
     *
     * @param {Date} date The date to have relative string.
     *
     * @returns {String}
     *
     * @example
     * var strdate = CMDBuildUI.util.Utilities.getRelativeDate(date);
     */
    getRelativeDate: function (date) {
        function calculateDelta(date1, date2) {
            return Math.round((date1 - date2) / 1000);
        }

        if (!(date instanceof Date)) {
            date = new Date(date);
        }

        var delta = null,
            now = new Date(),
            minute = 60,
            hour = minute * 60,
            day = hour * 24,
            week = day * 7,
            month = day * 30,
            year = day * 365,
            rtf = new Intl.RelativeTimeFormat(CMDBuildUI.util.helper.SessionHelper.getLanguage(), {
                numeric: 'auto'
            });

        delta = calculateDelta(now, date);

        if (delta > day && delta < week) {
            date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
            delta = calculateDelta(now, date);
        }

        switch (true) {
            case delta < minute:
                return rtf.format(-delta, 'second');

            case delta < hour:
                return rtf.format(-Math.floor(delta / minute), 'minute');

            case delta < day:
                return rtf.format(-Math.floor(delta / hour), 'hour');

            case delta < week:
                return rtf.format(-Math.floor(delta / day), 'day');

            case delta < month:
                return rtf.format(-Math.floor(delta / week), 'week');

            case delta < year:
                return rtf.format(-Math.floor(delta / month), 'month');

            case delta >= year:
                return rtf.format(-Math.floor(delta / year), 'year');
        }
    },

    /**
     * Parse a string representing an interval and returns an object.
     *
     * @param {String} interval String representing the interval.
     *
     * @returns {Object} Parsed interval.
     *
     * @example
     * var p_interval = CMDBuildUI.util.Utilities.parseInerval(interval);
     * console.log('Minutes: ' + p_interval.minutes);
     */
    parseInerval: function (interval) {
        var intervalRegex =
            /(-)?P(?:([.,\d]+)Y)?(?:([.,\d]+)M)?(?:([.,\d]+)W)?(?:([.,\d]+)D)?(?:T(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?)?/;
        var matches = interval.match(intervalRegex);
        return {
            years: matches[2] === undefined ? 0 : matches[2],
            months: matches[3] === undefined ? 0 : matches[3],
            weeks: matches[4] === undefined ? 0 : matches[4],
            days: matches[5] === undefined ? 0 : matches[5],
            hours: matches[6] === undefined ? 0 : matches[6],
            minutes: matches[7] === undefined ? 0 : matches[7],
            seconds: matches[8] === undefined ? 0 : matches[8]
        };
    },

    /**
     * Convert an interval to human readable string.
     *
     * @param {String} interval String representing the interval.
     * @returns {String} Human readable interval.
     */
    intervalToHumanReadable: function (interval) {
        var parsedInterval = this.parseInerval(interval);
        var humanValues = [];

        if (parsedInterval.years) {
            humanValues.push(
                Ext.String.format('{0} {1}', parsedInterval.years, CMDBuildUI.locales.Locales.administration.home.years)
            );
        }
        if (parsedInterval.months) {
            humanValues.push(
                Ext.String.format(
                    '{0} {1}',
                    parsedInterval.months,
                    CMDBuildUI.locales.Locales.administration.home.months
                )
            );
        }
        if (parsedInterval.weeks) {
            humanValues.push(
                Ext.String.format('{0} {1}', parsedInterval.weeks, CMDBuildUI.locales.Locales.administration.home.weeks)
            );
        }
        if (parsedInterval.days) {
            humanValues.push(
                Ext.String.format('{0} {1}', parsedInterval.days, CMDBuildUI.locales.Locales.administration.home.days)
            );
        }
        if (parsedInterval.hours) {
            humanValues.push(
                Ext.String.format('{0} {1}', parsedInterval.hours, CMDBuildUI.locales.Locales.administration.home.hours)
            );
        }
        if (parsedInterval.minutes) {
            humanValues.push(
                Ext.String.format(
                    '{0} {1}',
                    parsedInterval.minutes,
                    CMDBuildUI.locales.Locales.administration.home.minutes
                )
            );
        }
        if (parsedInterval.seconds) {
            humanValues.push(
                Ext.String.format(
                    '{0} {1}',
                    parseInt(parsedInterval.seconds, 0),
                    CMDBuildUI.locales.Locales.administration.home.seconds
                )
            );
        }

        if (humanValues.length === 1) {
            return humanValues[0];
        } else if (humanValues.length > 1) {
            var last = Ext.Array.splice(humanValues, -1)[0];
            return [humanValues.join(', '), CMDBuildUI.locales.Locales.common.and, last].join(' ');
        }
        return '';
    },

    /**
     * Update page favicon.
     * @param {Boolean} alert true to use alert favicon.
     * @private
     */
    updateFavicon: function (alert) {
        var manifest = Ext.manifest.profile === 'openmaint' ? Ext.manifest.profile + '/' : '',
            i16 = Ext.query('link[rel="icon"][sizes="16x16"]', true)[0],
            i32 = Ext.query('link[rel="icon"][sizes="32x32"]', true)[0];
        if (alert) {
            i16.setAttribute('href', manifest + 'resources/images/favicon/favicon-16x16-alert.png');
            i32.setAttribute('href', manifest + 'resources/images/favicon/favicon-32x32-alert.png');
        } else {
            i16.setAttribute('href', manifest + 'resources/images/favicon/favicon-16x16.png');
            i32.setAttribute('href', manifest + 'resources/images/favicon/favicon-32x32.png');
        }
    },

    /**
     * Play alert sound.
     *
     * @example
     * CMDBuildUI.util.Utilities.playAlertNotificationSound();
     */
    playAlertNotificationSound: function () {
        const notificationSoundEnabled = CMDBuildUI.util.helper.UserPreferences.getNotificationSoundEnabled();
        if (Ext.supports.Audio && notificationSoundEnabled) {
            let audio = new Audio('resources/audio/notification.mp3');
            const volume = CMDBuildUI.util.helper.UserPreferences.get(
                CMDBuildUI.model.users.Preference.notifications.soundVolume
            );
            audio.volume = volume ? volume / 100 : 1;
            audio.play();
            audio = null;
        }
    },

    /**
     * @param {Ext.button.Button[]} buttons
     */
    disableFormButtons: function (buttons) {
        // disable buttons
        Ext.Array.forEach(buttons, function (item, index, allitems) {
            if (item) {
                item.disable();
                if (item.showSpinner) {
                    // add spinner to clicked button
                    item.toggleCls(Ext.baseCSSPrefix + 'mask-button');
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button[]} buttons
     */
    enableFormButtons: function (buttons) {
        // enable buttons
        Ext.Array.forEach(buttons, function (item, index, allitems) {
            if (item && !item.isDestroyed) {
                item.enable();
                if (item.showSpinner) {
                    // remove spinner to clicked button
                    item.toggleCls(Ext.baseCSSPrefix + 'mask-button');
                    item.showSpinner = false;
                }
            }
        });
    },

    /**
     *
     * Manage callbacks after execution of widgets on card or process
     * @param {Object/Function} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     * @param {Object} theObject
     */
    manageCallbacks: function (callback, theObject) {
        if (Ext.isFunction(callback)) {
            Ext.callback(callback, null, [theObject, {}, false]);
        } else if (Ext.isObject(callback)) {
            if (Ext.isFunction(callback.failure)) {
                Ext.callback(callback.failure, null, [theObject, {}]);
            }
            if (Ext.isFunction(callback.callback)) {
                Ext.callback(callback.callback, null, [theObject, {}, false]);
            }
        }
    },

    /**
     * Copy text to the clipboard and optionally show a confirmation message.
     *
     * @param {string} textToCopy The text to copy to the clipboard.
     * @param {string} confirmationMessage Message to display after successful copy.
     * @returns {Promise<void>} A promise that resolves when the text has been copied successfully, or rejects if an error occurs.
     *
     * @example
     * CMDBuildUI.util.Utilities.copyToClipboard('Hello world', 'Copied!')
     *     .then(() => console.log('Text copied!'))
     *     .catch(err => console.error('Copy failed:', err));
     */
    copyToClipboard: function (textToCopy, confirmationMessage) {
        return navigator.clipboard
            .writeText(textToCopy)
            .then(function () {
                CMDBuildUI.util.Notifier.showMessage(confirmationMessage, {
                    title: CMDBuildUI.locales.Locales.notifier.copied
                });
            })
            .catch(err => {
                console.error(err);
            });
    },

    /**
     * Determines whether the /administration prefix should be added to the URL.
     * - ADMIN_ONLY: always, regardless of context
     * - MIXED: only if currently in the administration module
     *
     * @param {String} url
     * @returns {Boolean}
     */
    shouldAddAdministrationPrefix: function (url) {
        const vm = CMDBuildUI.util.helper.SessionHelper.getViewportVM();
        const isAdministrationModule = vm && vm.get('isAdministrationModule');

        if (!isAdministrationModule) {
            return false;
        }

        const baseUrl = CMDBuildUI.util.Config.baseUrl;

        // returns the path starting with version, without leading slash and query string
        // e.g. "http://localhost:8080/cmdbuild/services/rest/v4/classes/Company?_dc=1772700718934" -> "v4/classes/Company"
        const path = url
            .split('?')[0]
            .replace(baseUrl.replace(/\/v\d+$/, ''), '')
            .replace(/^\//, '');

        if (/^v\d+\/administration\//.test(path)) {
            return false;
        }

        const isAdmin = this.ADMIN_ONLY_ENDPOINTS.some(function (r) {
            return r.test(path);
        });

        const isMixed = this.MIXED_ENDPOINTS.some(function (r) {
            return r.test(path);
        });

        return isAdmin || isMixed;
    },

    /**
     * Adds 'administration' between version and endpoint in the URL
     * @param {String} url  example: http://localhost:8080/cmdbuild/services/rest/v4/AAA/BBB/CCC
     * @returns {String}    example: http://localhost:8080/cmdbuild/services/rest/v4/administration/AAA/BBB/CCC
     */
    addAdministrationPrefix: function (url) {
        return url.replace(/(\/v\d+)(\/|$)/, '$1/administration$2');
    },

    privates: {
        _popupAlwaysOnTop: 10
    }
});
