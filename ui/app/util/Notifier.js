/**
 * @file CMDBuildUI.util.Notifier
 * @module CMDBuildUI.util.Notifier
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.Notifier', {
    singleton: true,

    /**
     * @private
     *
     * @property {Boolean} disabled If true for disable showMessage. Currently needed by tests.
     */
    disabled: false,

    /**
     * Available toast icons.
     *
     * @constant {Object} icons
     * @property {String} error
     * @property {String} info
     * @property {String} success
     * @property {String} warning
     */
    icons: {
        error: CMDBuildUI.util.helper.IconHelper.getIconId('times-circle', 'solid'),
        info: CMDBuildUI.util.helper.IconHelper.getIconId('info-circle', 'solid'),
        success: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'solid'),
        warning: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-circle', 'solid')
    },

    /**
     * Show toast message.
     *
     * @param {String} message The content of the message box.
     * @param {Object} options Required - Message configuration.
     * @param {String} options.title Required - The title of the message box.
     * @param {String} [options.icon] The icont to display in message box header. One of {@link CMDBuildUI.util.Notifier#icons CMDBuildUI.util.Notifier.icons} properties or FontAwsome css class.
     * @param {String} [options.ui="managemenent"] Specify the ui for message box.
     * @param {String} [options.details] An optional text with more details.
     *
     * @example
     * CMDBuildUI.util.Notifier.showMessage(
     *      Ext.String.format("Welcome back {0}", record.get('userDescription')), {
     *          title: "Logged in",
     *          icon: CMDBuildUI.util.Notifier.icons.success
     *      }
     * );
     */
    showMessage: function (message, options) {
        if (message && !CMDBuildUI.util.Notifier.disabled) {
            var w;
            options = Ext.applyIf(options, {
                title: CMDBuildUI.locales.Locales.notifier.info,
                icon: this.icons.info,
                ui: 'default'
            });
            var config = {
                title: Ext.String.format('<span data-testid="message-window-title">{0}</span>', options.title),
                iconCls: options.icon,
                html: Ext.String.format(
                    '<span data-testid="message-window-text">{0}</span>',
                    CMDBuildUI.util.Utilities.removeEventAttributesFromHTML(message)
                ),
                width: 200,
                align: 'br',
                ui: options.ui,
                margin: '-3 0 0 0',
                alwaysOnTop: 9999,
                autoEl: {
                    'data-testid': 'message-window'
                },
                listeners: {
                    show: function (toast) {
                        const el = toast.el.dom;
                        const RIGHT = '10px';
                        let observer;

                        function fix() {
                            if (el.style.right !== RIGHT) {
                                el.style.right = RIGHT;
                            }
                            el.style.left = 'auto';
                        }

                        fix();

                        observer = new MutationObserver(fix);
                        observer.observe(el, { attributes: true, attributeFilter: ['style'] });

                        toast.on(
                            'destroy',
                            function () {
                                observer.disconnect();
                            },
                            null,
                            { single: true }
                        );
                    }
                }
            };

            if (options.icon === this.icons.error) {
                config.autoClose = false;
                config.closable = true;
            }

            let bbar = [];
            let tbar = [];

            if (options.details) {
                bbar.push(
                    { xtype: 'component', flex: 1 },
                    {
                        xtype: 'tool',
                        type: 'help',
                        callback: function () {
                            CMDBuildUI.util.Utilities.openPopup(
                                null,
                                options.title,
                                {
                                    xtype: 'panel',
                                    cls: 'x-selectable',
                                    html: CMDBuildUI.util.helper.FieldsHelper.renderTextField(options.details),
                                    scrollable: true,
                                    bodyPadding: 10
                                },
                                null,
                                { width: '50%', height: '50%' }
                            );
                            w.close();
                        }
                    }
                );
            }

            if (options.code || typeof options.buttonAction === 'function') {
                tbar.push({
                    xtype: 'component',
                    flex: 1,
                    html: Ext.String.format(
                        '<span style="font-weight:bold; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; display:block;" title="{0}">{0}</span>',
                        options.code || CMDBuildUI.locales.Locales.notifier.more
                    )
                });

                if (typeof options.buttonAction === 'function') {
                    tbar.push({
                        xtype: 'button',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular'),
                        style: { backgroundColor: '#fff', border: 'none' },
                        minWidth: 30,
                        maxWidth: 30,
                        tooltip: CMDBuildUI.locales.Locales.common.actions.copyerrormessage,
                        handler: function () {
                            options.buttonAction();
                        }
                    });
                }
            }

            if (tbar.length > 0) {
                config.tbar = tbar;
            }

            if (bbar.length > 0) {
                config.bbar = bbar;
            }

            w = Ext.create('Ext.window.Toast', config);
            w.show();
        }
    },

    /**
     * Show success message.
     *
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="default"] Specify the ui for message box.
     * @param {String} [details] An optional text with more details.
     *
     * @example
     * CMDBuildUI.util.Notifier.showSuccessMessage("Connection successful");
     */
    showSuccessMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format('{0} - {1}', code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.success,
            icon: CMDBuildUI.util.Notifier.icons.success,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show info message.
     *
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="default"] Specify the ui for message box.
     * @param {String} [details] An optional text with more details.
     *
     * @example
     * CMDBuildUI.util.Notifier.showInfoMessage("Filter removed");
     */
    showInfoMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format('{0} - {1}', code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.info,
            icon: CMDBuildUI.util.Notifier.icons.info,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show warning message.
     *
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="default"] Specify the ui for message box.
     * @param {String} [details] An optional text with more details.
     *
     * @example
     * CMDBuildUI.util.Notifier.showWarningMessage("Invalid filter");
     */
    showWarningMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format('{0} - {1}', code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.warning,
            icons: CMDBuildUI.util.Notifier.icons.warning,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.warn, code);
    },

    /**
     * Show error message.
     *
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="management"] Specify the ui for message box.
     * @param {String} [details] An optional text with more details.
     *
     * @example
     * CMDBuildUI.util.Notifier.showErrorMessage("Error loading class Parameter.");
     */
    showErrorMessage: function (message, code, ui, details) {
        let buttonAction = null;

        const errorDetails =
            message === CMDBuildUI.locales.Locales.notifier.genericerror || !details || details.trim() === ''
                ? CMDBuildUI.locales.Locales.notifier.genericerrormessage
                : details;

        if (code) {
            buttonAction = function () {
                CMDBuildUI.util.Utilities.copyToClipboard(
                    CMDBuildUI.util.Utilities.extractTextFromHTML(code + ' -' + message, false),
                    CMDBuildUI.locales.Locales.notifier.copiedtotheclipboard
                );
            };
        }

        CMDBuildUI.util.Notifier.showMessage(message, {
            title: CMDBuildUI.locales.Locales.notifier.error,
            icon: CMDBuildUI.util.Notifier.icons.error,
            ui: ui,
            code: code,
            details: errorDetails,
            buttonAction: buttonAction
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.error, code);
        if (details) {
            CMDBuildUI.util.Logger.log(details, CMDBuildUI.util.Logger.levels.error);
        }
    },

    /**
     * Close all opened messages.
     *
     * @example
     * CMDBuildUI.util.Notifier.closeAll();
     */
    closeAll: function () {
        while (Ext.WindowManager.getActive() && Ext.WindowManager.getActive().xtype === 'toast') {
            Ext.WindowManager.getActive().destroy();
        }
    }
});
