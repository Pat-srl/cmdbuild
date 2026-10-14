Ext.define('CMDBuildUI.view.emails.email.EmailModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.emails-email',

    data: {
        attachmentsTotalCount: 0,
        attachmentsstore: {
            autoload: false,
            urlPreview: null
        },
        disabled: {
            templatechoice: true,
            keepsync: true
        },
        maxAttachmentSize: 0,
        emailloaded: false,
        formValid: false,
        warningMessage: '',
        hideWarningMessage: true,
        fileAttributesOfProcess: []
    },

    formulas: {
        disableSaveButton: {
            bind: {
                formValid: '{formValid}',
                hideWarningMessage: '{hideWarningMessage}'
            },
            get: function (data) {
                return !data.formValid || !data.hideWarningMessage;
            }
        },

        addAttachmentsHidden: {
            get: function () {
                var enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled),
                    model = this.get('objectdata._model'),
                    access = model ? model[CMDBuildUI.model.users.Grant.permissions.attachment_write] : true;

                return !(enabled && access);
            }
        },

        /**
         * Update attachments store configuration
         */
        updateAttachmentsStore: {
            bind: {
                email: '{theEmail}',
                loaded: '{emailloaded}',
                isHidden: '{addAttachmentsHidden}'
            },
            get: function (data) {
                var url;
                if (!data.isHidden && data.loaded) {
                    if (!data.email.phantom) {
                        url =
                            data.email.store.getProxy().getUrl() +
                            Ext.String.format('/{0}/attachments', data.email.getId());
                    } else {
                        url = this.get('storeurl');
                    }
                    this.set('attachmentsstore.proxyurl', url);

                    if (url && Ext.String.startsWith(url, CMDBuildUI.util.Config.baseUrl)) {
                        this.set('attachmentsstore.urlPreview', url.replace(CMDBuildUI.util.Config.baseUrl, ''));
                    }

                    var attachmentsData = [];
                    if (!Ext.isEmpty(data.email._attachments)) {
                        attachmentsData = data.email._attachments;
                    }
                    this.set('attachmentsstore.data', attachmentsData);
                    this.set('attachmentsTotalCount', attachmentsData.length);

                    // load attachments only for saved emails
                    this.set('attachmentsstore.autoload', data.email.crudState !== 'C');
                }
                this.set('modeRead', this.getView().formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read);
            }
        },

        /**
         * Update keep syncronization field configuration
         */
        updateKeepSync: {
            bind: {
                template: '{theEmail.template}'
            },
            get: function (data) {
                this.set('disabled.keepsync', Ext.isEmpty(data.template));
            }
        },

        delaysValues: function () {
            return CMDBuildUI.model.emails.Email.getDelays();
        },

        setAttachmentsTotalCount: {
            bind: {
                store: '{attachments}'
            },
            get: function (data) {
                this.set('attachmentsTotalCount', data.store.getCount());
            }
        },

        attachmentPreviewManager: {
            bind: {
                objectdata: '{objectdata}'
            },
            get: function (data) {
                var objectTypeName = data.objectdata._type;
                this.set('objectType', CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName));
                this.set('objectTypeName', objectTypeName);
                this.set('objectId', data.objectdata._id);
            }
        },
        templateUrl: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                return Ext.String.format(
                    '{0}by-class/{1}',
                    CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                    objectTypeName
                );
            }
        },

        hideEmailTplAttachmentsMsg: {
            bind: {
                hasattachs: '{theEmail._hasTemplateAttachments}',
                status: '{theEmail.status}'
            },
            get: function (data) {
                return !(data.hasattachs && data.status === CMDBuildUI.model.emails.Email.statuses.draft);
            }
        }
    },

    stores: {
        /**
         * Templates list
         */
        templates: {
            model: 'CMDBuildUI.model.emails.Template',
            proxy: {
                type: 'baseproxy',
                url: '{templateUrl}',
                extraParams: {
                    detailed: true
                }
            },
            advancedFilter: {
                attributes: {
                    provider: [
                        {
                            operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                            value: ['email']
                        }
                    ]
                }
            },
            autoLoad: '{!modeRead}',
            pageSize: 0, // disable pagination
            remoteSort: false,
            sorters: {
                property: '_description_translation'
            },
            autoDestroy: true
        },

        /**
         * Signatures list
         */
        signatures: {
            model: 'CMDBuildUI.model.emails.Signature',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Emails.getSignaturesUrl(),
                extraParams: {
                    detailed: true
                }
            },
            autoLoad: '{!modeRead}',
            pageSize: 0, // disable pagination
            autoDestroy: true
        },

        /**
         * Delays list
         */
        delays: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{delaysValues}'
        },

        /**
         * Attachments list
         */
        attachments: {
            type: 'attachments',
            proxy: {
                type: 'baseproxy',
                url: '{attachmentsstore.proxyurl}',
                extraParams: {
                    detailed: true
                }
            },
            autoLoad: '{attachmentsstore.autoload}',
            autoDestroy: true,
            data: '{attachmentsstore.data}',
            listeners: {
                datachanged: 'onAttachmentsDatachanged'
            }
        }
    }
});
