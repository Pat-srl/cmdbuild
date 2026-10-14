Ext.define('CMDBuildUI.view.administration.content.webhooks.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-webhooks-card',

    data: {
        toolAction: {
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        panelTitle: {
            bind: '{theWebhook.description}',
            get: function (description) {
                if (this.get('theWebhook') && !this.get('theWebhook').phantom) {
                    var title = Ext.String.format(
                        '{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.navigation.webhooks,
                        description
                    );
                    this.getParent().set('title', title);
                } else {
                    this.getParent().set('title', CMDBuildUI.locales.Locales.administration.webhooks.newwebhook);
                }
            }
        },
        languageDescriptionManager: {
            bind: '{theWebhook.language}',
            get: function (lang) {
                var language = Ext.getStore('localizations.Languages').findRecord('code', lang),
                    descritpion = (!language || !lang) ? CMDBuildUI.locales.Locales.administration.common.labels.default : language.get('description');

                this.set('theWebhook._language_description', descritpion);

            }
        },
        eventsDataStore: {
            bind: '{theWebhook.target}',
            get: function (target) {
                var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(target);
                this.set('theWebhook.type', type);
                return CMDBuildUI.util.administration.helper.ModelHelper.getWebhookEvents(type);
            }
        },

        methodsDataStore: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getWebhookMethods();
        },

        headersDataStore: {
            bind: '{theWebhook.headers}',
            get: function (headers) {
                var data = [];
                Ext.Object.getKeys(headers).forEach(function (key, index) {
                    data.push({
                        key: key,
                        value: headers[key]
                    });
                });
                return data;
            }
        },

        headersDataNewStore: function () {
            return [CMDBuildUI.model.base.KeyValue.create()];
        }
    },
    stores: {
        eventsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{eventsDataStore}'
        },

        methodsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{methodsDataStore}'
        },

        headersStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{headersDataStore}'
        },

        headersNewStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{headersDataNewStore}'
        },
        languages: {
            source: 'localizations.Languages',
            sorters: ['description'],
            autoLoad: true,
            pageSize: 0,
            autoDestroy: true
        }
    }

});