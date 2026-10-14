Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-documentmanagementsystem',

    data: {
        dmsServicesStoreData: null,
        dmsServicesData: null
    },

    formulas: {
        isAlfresco: {
            bind: "{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}",
            get: function (type) {
                return type === "alfresco";
            }

        },
        isPostgres: {
            bind: "{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}",
            get: function (type) {
                return type === "postgres";
            }

        },
        isSharepoint: {
            bind: "{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}",
            get: function (type) {
                return type === "sharepoint_online";
            }

        },
        isPlugin: {
            bind: {
                type: "{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}",
                services: "{dmsServicesData}"
            },
            get: function (data) {
                if (data.services) {
                    return data.services[data.type].isPlugin;
                } else {
                    return false;
                }

            }
        },
        getPluginTitle: {
            bind: {
                type: "{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}",
                services: "{dmsServicesData}"
            },
            get: function (data) {
                return data.services ? data.services[data.type].label : "";
            }
        },
        updateDisplayPassword: {
            bind: {
                password: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__password}',
            },
            get: function (data) {
                var hiddenPassword = CMDBuildUI.util.administration.helper.RendererHelper.getDisplayPassword(data.password);
                this.set('hiddenPassword', hiddenPassword);
            },
        },

        dmsServicesStoreDataManager: function () {
            const me = this;
            const store = Ext.getStore('pluginmanager.Plugins');
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type').then(function (value) {
                const services = {};
                const addService = function (value, label, isPlugin = false, pluginName = "") {
                    services[value] = {
                        value: value,
                        label: label,
                        isPlugin: isPlugin,
                        pluginName: pluginName
                    };
                };

                /* -- services object -- */
                // services default
                addService('alfresco', CMDBuildUI.locales.Locales.administration.systemconfig.alfresco);
                addService('sharepoint_online', CMDBuildUI.locales.Locales.administration.systemconfig.sharepoint);
                // services from plugins
                store.getRange().forEach(function (plugin) {
                    if (plugin.get('tag') === 'dms') {
                        addService(plugin.get('service'), plugin.get('description'), true, plugin.get('name'))
                    }
                });
                if (value === 'postgres') {
                    addService('postgres', CMDBuildUI.locales.Locales.administration.systemconfig.postgres); // only for development
                }
                me.set('dmsServicesData', services);

                // service array for combobox
                const types = Object.values(services);
                me.set('dmsServicesStoreData', types);
            });
        },

        dmsSharePointProtocolsData: {
            get: function () {
                return [
                    {
                        value: 'msazureoauth2_application',
                        label: CMDBuildUI.locales.Locales.administration.systemconfig.application,
                    },
                    {
                        value: 'msazureoauth2_delegated',
                        label: CMDBuildUI.locales.Locales.administration.systemconfig.delegated,
                    },
                    {
                        value: 'msazureoauth2_password',
                        label: CMDBuildUI.locales.Locales.administration.systemconfig.password,
                    },
                ];
            },
        },

        usernameAndPasswordHidden: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__protocol}',
            get: function (protocol) {
                if (['msazureoauth2_application', 'msazureoauth2_delegated'].indexOf(protocol) > -1) {
                    return true;
                }
                return false;
            },
        },
        authorizationCodeHidden: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__protocol}',
            get: function (protocol) {
                if (protocol === 'msazureoauth2_delegated') {
                    return false;
                }
                return true;
            },
        },
    },
    stores: {
        sharePointProtocolsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dmsSharePointProtocolsData}',
        },
        dmsCategoryTypesStore: {
            source: 'dms.DMSCategoryTypes',
            autoDestroy: true,
        },
        dmsServiceTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dmsServicesStoreData}',
            sorters: ['label'],
        },
    },
});