Ext.define('CMDBuildUI.view.administration.content.emails.errors.EmailModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.administration-content-emails-errors-edit',

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
        maxAttachmentSize: 0
    },

    formulas: {

        addAttachmentsHidden: {
            get: function () {
                var enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled),
                    model = this.get("objectdata._model"),
                    access = model ? model[CMDBuildUI.model.users.Grant.permissions.attachment_write] : true;

                return !(enabled && access);
            }
        },

        /**
         * Update attachments store configuration
         */
        updateAttachmentsStore: {
            bind: {
                email: '{theEmail}'
            },
            get: function (data) {
                var url;
                url = Ext.String.format("{0}/classes/Email/cards/{1}/emails/{2}/attachments", CMDBuildUI.util.Config.baseUrl, data.email.get('card'), data.email.getId());

                this.set("attachmentsstore.proxyurl", url);

                if (url && Ext.String.startsWith(url, CMDBuildUI.util.Config.baseUrl)) {
                    this.set("attachmentsstore.urlPreview", url.replace(CMDBuildUI.util.Config.baseUrl, ""));
                }

                var attachmentsData = [];
                if (!Ext.isEmpty(data.email._attachments)) {
                    attachmentsData = data.email._attachments;
                }
                this.set("attachmentsstore.data", attachmentsData);
                this.set("attachmentsTotalCount", attachmentsData.length);

                // load attachments only for saved emails
                this.set("attachmentsstore.autoload", data.email.crudState !== "C");

                this.set("modeRead", this.getView().formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read);
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
                this.set("disabled.keepsync", Ext.isEmpty(data.template));
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
                this.set("attachmentsTotalCount", data.store.getCount());
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
                return Ext.String.format('{0}by-class/{1}', CMDBuildUI.util.api.Emails.getTemplatesUrl(), objectTypeName);
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
                type: "baseproxy",
                url: '{templateUrl}',
                extraParams: {
                    detailed: true
                }
            },
            advancedFilter: {
                attributes: {
                    provider: [{
                        operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        value: ['email']
                    }]
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
                type: "baseproxy",
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
                type: "memory"
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