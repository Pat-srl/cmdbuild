Ext.define('CMDBuildUI.view.administration.content.setup.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-view',

    data: {
        classList: [],
        functionList: [],
        isClassMode: false,
        isEditBtnHidden: true,
        isEditButtonDisabled: false,
        isFunctionMode: false,
        isMultitenantEnabled: false,
        multitenantFieldsDisabled: false,
        preferredFileCharsetStoreAutoload: false,
        theSetup: {},
        actions: {
            view: true,
            edit: false
        },
        toolAction: {
            _canUpdate: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            get: function (data) {
                this.set('toolAction._canUpdate', data.canModify === true);
            }
        },
        configManager: function () {
            var me = this;

            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (configs) {
                if (!me.destroyed) {
                    configs.forEach(function (key) {
                        me.set(Ext.String.format('theSetup.{0}', key._key), key.hasValue ? key.value : key.default);
                    });
                }
            });
        },
        curentPageManager: {
            bind: '{currentPage}',
            get: function (currentPage) {
                if (currentPage && currentPage === 'generaloptions') {
                    this.set('preferredFileCharsetStoreAutoload', true);
                }
            }
        },

        editBtnManager: {
            bind: {
                multiTenantEnabled: '{multiTenantEnabled}',
                currentPage: '{currentPage}',
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                if (
                    data.isEdit ||
                    ['servermanagement', 'system', 'scheduler', 'buslogs', 'joblogs', 'logs'].indexOf(
                        data.currentPage
                    ) > -1
                ) {
                    this.set('isEditBtnHidden', true);
                } else {
                    this.set('isEditBtnHidden', false);
                }
            }
        },
        multitenantConfigurationModeComboHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (!data) {
                    return true;
                }
                if (data.multitenantMode === 'DISABLED' || !data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationModeDisplayHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationClassComboHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.multitenantMode === 'DB_FUNCTION' || !data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationClassDisplayHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.multitenantMode === 'DB_FUNCTION' || data.editMode) {
                    return true;
                }
                return false;
            }
        },
        /**
         * Multitenant configuration
         */

        multiTenantEnabled: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
            get: function (mode) {
                if (mode) {
                    switch (mode) {
                        case 'DISABLED':
                        case '':
                            this.set('isClassMode', false);
                            this.set('isFunctionMode', false);
                            return false;
                        case 'CMDBUILD_CLASS':
                            this.set('isClassMode', true);
                            this.set('isFunctionMode', false);
                            return true;
                        case 'DB_FUNCTION':
                            this.set('isClassMode', false);
                            this.set('isFunctionMode', true);
                            return true;
                        default:
                            return false;
                    }
                }
                return false;
            },
            set: function (value) {
                switch (value) {
                    case true:
                        this.set('theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode', 'CMDBUILD_CLASS');
                        break;
                    default:
                        this.set('theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode', 'DISABLED');
                        break;
                }
            }
        },

        logo: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__companyLogo}',
            get: function (logoId) {
                if (logoId) {
                    var logoUrl = Ext.String.format(
                        '{0}/resources/company_logo/download?_dc={1}',
                        CMDBuildUI.util.Config.baseUrl,
                        new Date().getTime()
                    );

                    return logoUrl;
                }
            }
        },
        tenantConnections: {
            get: function () {
                return [
                    {
                        value: 'CMDBUILD_CLASS',
                        label: CMDBuildUI.locales.Locales.administration.common.messages.connectedtoclass
                    },
                    {
                        value: 'DB_FUNCTION',
                        label: CMDBuildUI.locales.Locales.administration.common.messages.connectedtofunction
                    }
                ];
            }
        },
        csvSeparators: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getCsvSeparators();
            }
        },
        fulltextData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getSearchfieldInGridsOptions();
        },
        startDaysData: function () {
            var ret = [];
            for (var i = 0; i <= 6; i++) {
                ret.push({
                    value: i,
                    label: Ext.Date.dayNames[i]
                });
            }
            return ret;
        }
    },

    stores: {
        getConfigurationModeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{tenantConnections}',
            autoDestroy: true
        },
        getFilteredClasses: {
            source: 'classes.Classes',
            autoLoad: true,
            autoDestroy: true,
            sorters: ['description'],
            pageSize: 0,
            filters: [
                function (item) {
                    return item.data.name !== CMDBuildUI.util.helper.ModelHelper.systemClasses.class;
                }
            ]
        },
        preferredFileCharsetStore: {
            fields: ['_id', 'description'],
            proxy: {
                type: 'baseproxy',
                url: '/system/charsets'
            },
            pageSize: 0,
            autoLoad: '{preferredFileCharsetStoreAutoload}',
            autoDestroy: true
        },
        csvSeparatorsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{csvSeparators}'
        },
        startDaysStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{startDaysData}',
            autoDestroy: true
        },
        fulltextStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{fulltextData}',
            autoDestroy: true
        }
    },

    /**
     * Change form mode
     *
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        if (me.get('actions.edit') && mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
            this.set('isEditBtnHidden', false);
        }

        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                break;

            default:
                me.set('actions.view', true);
                me.set('actions.edit', false);
                break;
        }
    }
});
