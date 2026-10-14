Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogViewerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-logviewer',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy',
            destroy: 'onDestroy'
        },
        '#startBtn': {
            click: 'onStartBtnClick'
        },
        '#pauseBtn': {
            click: 'onPauseBtnClick'
        },
        '#enableAutoScrollBtn': {
            click: 'onEnableAutoScrollBtnClick'
        },
        '#disableAutoScrollBtn': {
            click: 'onDisableAutoScrollBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.setup.elements.LogViewer} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.services.websocketsEnabled)) {
            this._startLogger(view);
        } else {
            view.getViewModel().set('showWebsocketsDisabledWarning', true);
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.setup.elements.LogViewer} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        this._stopLogger(view);
    },

    /**
     *
     * @param {Ext.panel.Tool} button
     * @param {Ext.event.Event} event
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onStartBtnClick: function (button, event, owner, eOpts) {
        this._startLogger(this.getView());
    },
    /**
     *
     * @param {Ext.panel.Tool} button
     * @param {Ext.event.Event} event
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onPauseBtnClick: function (button, event, owner, eOpts) {
        this._stopLogger(this.getView());
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.setup.elements.LogViewer} view
     */
    onDestroy: function (view) {
        this._startLogger(view);
    },

    /**
     *
     * @param {Ext.panel.Tool} button
     * @param {Ext.event.Event} event
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onEnableAutoScrollBtnClick: function (button, event, owner, eOpts) {
        this.getViewModel().set('autoscrollenabled', true);
    },

    /**
     *
     * @param {Ext.panel.Tool} button
     * @param {Ext.event.Event} event
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onDisableAutoScrollBtnClick: function (button, event, owner, eOpts) {
        this.getViewModel().set('autoscrollenabled', false);
    },

    /**
     * @private
     */
    privates: {
        /**
         *
         */
        _customsocket: null,
        /**
         *
         */
        _scrollLocked: false,
        /**
         *
         * @param {CMDBuildUI.view.administration.content.setup.elements.LogViewer} view
         */
        _startLogger: function (view) {
            var me = this;
            var vm = me.getViewModel();
            CMDBuildUI.util.Ajax.setActionId('system.loggers.stream.post');
            // CMDBuildUI.util.helper.SessionHelper.closeWebSocket();
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/system/loggers/stream',
                method: 'POST'
            }).then(function () {
                // open socket for reading logs
                try {
                    if (!me._customsocket) {
                        CMDBuildUI.util.Logger.log(
                            'logger socket is not initialized',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        var socket = (me._customsocket = new WebSocket(CMDBuildUI.util.Config.socketUrl));
                        CMDBuildUI.util.Logger.log(
                            'logger socket is now initialized',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        socket.onmessage = function (e) {
                            var data = Ext.JSON.decode(e.data || {});
                            if (data && data._event) {
                                vm.get('messagesStore').add(data);
                                if (vm.get('autoscrollenabled')) {
                                    view.down('dataview').getScrollable().scrollTo(0, Infinity, true);
                                }
                            }
                        };
                        CMDBuildUI.util.Logger.log(
                            'logger socket onmessage initialized',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        socket.onopen = function (e) {
                            vm.set('loggeractive', true);
                            CMDBuildUI.util.Logger.log('logger socket opened', CMDBuildUI.util.Logger.levels.debug);
                            if (socket) {
                                socket.send(
                                    Ext.JSON.encode({
                                        _action: 'socket.session.login',
                                        token: CMDBuildUI.util.helper.SessionHelper.getCurrentSession().getId(),
                                        _id: CMDBuildUI.util.Utilities.generateUUID()
                                    })
                                );
                            }
                        };
                        CMDBuildUI.util.Logger.log(
                            'logger socket onopen initialized',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        socket.onclose = function () {
                            if (me && !me.destroyed) {
                                me.getViewModel().set('loggeractive', false);
                                delete me._customsocket;
                            }
                            CMDBuildUI.util.Logger.log('logger socket onclose ', CMDBuildUI.util.Logger.levels.debug);
                        };
                    } else {
                        CMDBuildUI.util.Logger.log(
                            'logger socket alredy initialized',
                            CMDBuildUI.util.Logger.levels.debug
                        );
                    }
                } catch (e) {
                    CMDBuildUI.util.Logger.log(
                        'Error on creating socket.',
                        CMDBuildUI.util.Logger.levels.error,
                        null,
                        e
                    );
                }
            });
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.setup.elements.LogViewer} view
         */
        _stopLogger: function (view) {
            var me = this;

            // close socket for reading logs
            if (me._customsocket) {
                me._customsocket.close();
            }
            CMDBuildUI.util.Ajax.setActionId('system.loggers.stream.delete');
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/system/loggers/stream',
                method: 'DELETE'
            }).then(function () {
                CMDBuildUI.util.Logger.log('logger socket close request success', CMDBuildUI.util.Logger.levels.debug);
                CMDBuildUI.util.helper.SessionHelper.initCommunicationMethod(
                    CMDBuildUI.util.helper.SessionHelper.getCurrentSession().getId()
                );
            });
        }
    }
});
