Ext.define('CMDBuildUI.model.emails.Email', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],
    statics: {
        statuses: {
            draft: 'draft',
            error: 'error',
            outgoing: 'outgoing',
            received: 'received',
            sent: 'sent',
            new: 'new',
            skipped: 'skipped'
        },
        statusicon: {
            draft: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'),
            error: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
            outgoing: CMDBuildUI.util.helper.IconHelper.getIconId('hourglass', 'regular'),
            received: CMDBuildUI.util.helper.IconHelper.getIconId('envelope-open', 'regular'),
            sent: 'cmdbuildicon-envelope-sent-o',
            new: CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'regular'),
            skipped: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid')

        },
        getDelays: function (withNegative) {
            var positiveDelays = [],
                negativeDelays = [];

            if (withNegative) {
                negativeDelays = [{
                    value: -3600, // -1 hour in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativehour1
                }, {
                    value: -7200, // 2 hours in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativehours2
                }, {
                    value: -14400, // 4 hours in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativehours4
                }, {
                    value: -86400, // 1 day in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativeday1
                }, {
                    value: -172800, // 2 days in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativedays2
                }, {
                    value: -345600, // 4 days in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativedays4
                }, {
                    value: -604800, // 1 week in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativeweek1
                }, {
                    value: -1209600, // 2 weeks in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativeweeks2
                }, {
                    value: -2629746, // 1 month in seconds
                    label: CMDBuildUI.locales.Locales.emails.delays.negativemonth1
                }];
            }
            positiveDelays = [{
                value: 0,
                label: CMDBuildUI.locales.Locales.emails.delays.none
            }, {
                value: 3600, // 1 hour in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.hour1
            }, {
                value: 7200, // 2 hours in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.hours2
            }, {
                value: 14400, // 4 hours in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.hours4
            }, {
                value: 86400, // 1 day in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.day1
            }, {
                value: 172800, // 2 days in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.days2
            }, {
                value: 345600, // 4 days in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.days4
            }, {
                value: 604800, // 1 week in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.week1
            }, {
                value: 1209600, // 2 weeks in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.weeks2
            }, {
                value: 2629746, // 1 month in seconds
                label: CMDBuildUI.locales.Locales.emails.delays.month1
            }];

            var delays = Ext.Array.merge(positiveDelays, negativeDelays);
            return delays;

        }
    },

    fields: [{
        name: 'keepSynchronization',
        type: 'boolean',
        critical: true
    }, {
        name: 'account',
        type: 'string',
        critical: true
    }, {
        name: 'bcc',
        type: 'string',
        critical: true
    }, {
        name: 'body',
        type: 'string',
        critical: true
    }, {
        name: 'cc',
        type: 'string',
        critical: true
    }, {
        name: 'date',
        type: 'date',
        critical: true
    }, {
        name: 'delay',
        type: 'number',
        critical: true
    }, {
        name: 'from',
        type: 'string',
        critical: true
    }, {
        name: 'noSubjectPrefix',
        type: 'boolean',
        critical: true
    }, {
        name: 'notifyWith',
        type: 'string',
        critical: true
    }, {
        name: 'promptSynchronization',
        type: 'boolean',
        critical: true
    }, {
        name: 'status',
        type: 'string',
        critical: true
    }, {
        name: 'subject',
        type: 'string',
        critical: true,
        validators: ['trimpresence']
    }, {
        name: 'template',
        type: 'string',
        critical: true
    }, {
        name: 'to',
        type: 'string',
        critical: true
    }, {
        name: 'contentType',
        type: 'string',
        defaultValue: 'text/html',
        critical: true
    }, {
        name: 'signature',
        type: 'string',
        critical: true
    }, {
        name: '_content_html',
        persist: false,
        validators: ['trimpresence']
    }, {
        name: '_content_plain',
        persist: false
    }, {
        name: '_hasTemplateAttachments',
        type: 'boolean',
        persist: false
    }],
    proxy: {
        type: 'baseproxy'
    },

    /**
     *
     * @param {Ext.data.Model[]} attachments
     *
     * @return {Ext.promise.Promise}
     */
    saveAttachments: function (attachments) {
        var deferred = new Ext.Deferred(),
            me = this;

        if (Ext.isEmpty(attachments)) {
            deferred.resolve();
        } else {
            var promises = [];
            // get attachments url
            var attachmentsUrl = Ext.String.format(
                "{0}/{1}/attachments",
                this.getProxy().getUrl(),
                this.getId()
            );
            // save attachments
            attachments.forEach(function (attachment) {
                if (attachment.get("newAttachment")) {
                    if (attachment.get('DMSAttachment')) {
                        promises.push(me._saveDMSAttachment(attachmentsUrl, attachment));
                    } else {
                        promises.push(me._saveFileAttachment(attachmentsUrl, attachment));
                    }
                }
            });

            Ext.Promise.all(promises).then(function () {
                deferred.resolve();
            }, function () {
                deferred.reject();
            });
        }

        return deferred;
    },

    send: function () {
        var url = this.getProxy().getUrl(),
            customDelay = CMDBuildUI.util.helper.UserPreferences.getEmailSendDelay();

        if (!this.phantom) {
            url = Ext.String.format(
                '{0}/{1}/send',
                url,
                this.getId()
            );
        }

        var options = {
            url: url,
            method: 'POST',
            params: {},
            jsonData: {}
        };

        // if the custom delay is greater then the default delay customize it
        if (customDelay) {
            if (customDelay > this.get('delay')) {
                options.params.delay = customDelay + 1;
            }
            this.createAbortSendEmailInfoMessage(customDelay);
        }

        if (this.get('_hasTemplateAttachments')) {
            options.params.upload_template_attachments = true;
        }

        Ext.Ajax.request(options);
    },


    privates: {
        /**
         * Save DMS attachment
         *
         * @param {String} url
         * @param {Ext.data.Model} attachment
         *
         * @return {Ext.promise.Promise}
         */
        _saveDMSAttachment: function (url, attachment) {
            var deferred = new Ext.Deferred();

            CMDBuildUI.util.Ajax.setActionId('email.attachment.copyfromdms');
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                params: {
                    copyFrom_class: attachment.get('objectTypeName'),
                    copyFrom_card: attachment.get('objectId'),
                    copyFrom_id: attachment.get('_id')
                },
                callback: function (options, success, response) {
                    deferred.resolve(success);
                }
            });

            return deferred;
        },

        /**
         * Save DMS attachment
         *
         * @param {String} url
         * @param {Ext.data.Model} attachment
         *
         * @return {Ext.promise.Promise}
         */
        _saveFileAttachment: function (url, attachment) {
            var deferred = new Ext.Deferred();

            CMDBuildUI.util.Ajax.setActionId('email.attachment.upload');
            CMDBuildUI.util.File.uploadFileWithMetadata(
                "POST",
                url,
                attachment.get('_file')
            ).then(function (response) {
                deferred.resolve();
            }, function () {
                deferred.reject();
            });

            return deferred;
        },

        /**
         * @private
         *
         * @param {Number} delay
         */
        createAbortSendEmailInfoMessage: function (delay) {
            var toast;

            // get email original data
            var abortOptions = {
                url: Ext.String.format(
                    '{0}/{1}/abort',
                    this.getProxy().getUrl(),
                    this.getId()
                ),
                method: 'POST',
                jsonData: {
                    delay: this.get('delay')
                },
                success: function (response, options) {
                    toast.close();
                }
            }

            // get config
            toast = Ext.create('Ext.window.Toast', {
                title: Ext.String.format('<span data-testid="message-window-title">{0}</span>', CMDBuildUI.locales.Locales.notifier.success),
                iconCls: CMDBuildUI.util.Notifier.icons.success,
                width: 200,
                align: 'br',
                ui: 'default',
                alwaysOnTop: 9999,
                autoEl: {
                    'data-testid': 'message-window'
                },
                stickWhileHover: false,
                autoCloseDelay: delay * 1000,
                autoClose: true,
                closable: true,
                items: [{
                    xtype: 'container',
                    layout: 'hbox',
                    items: [{
                        xtype: 'component',
                        html: CMDBuildUI.locales.Locales.emails.sendingemail
                    }, {
                        xtype: 'linkbutton',
                        text: CMDBuildUI.locales.Locales.emails.abort,
                        padding: '0 0 0 5',
                        listeners: {
                            click: function () {
                                Ext.Ajax.request(abortOptions);
                            }
                        }
                    }]
                }]
            });

            toast.show();
        }
    }
});