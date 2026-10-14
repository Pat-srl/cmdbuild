Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-configuration-view',
    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        },
        languageprompt: false,
        defaultlanguage: '',
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
                    var language = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__core__DOT__language';
                    })[0];
                    var languageprompt = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__core__DOT__languageprompt';
                    })[0];
                    var languagepromptvalue = null;
                    if (languageprompt.hasValue) {
                        if (languageprompt.value == 'false' || languageprompt.value == false) {
                            languagepromptvalue = false;
                        } else {
                            if (languageprompt.value == 'true' || languageprompt.value == true) {
                                languagepromptvalue = true;
                            }
                        }
                    }
                    me.set('defaultlanguage', language.hasValue ? language.value : language.default);
                    me.set('languageprompt', languageprompt.hasValue ? languagepromptvalue : languageprompt.default);
                }
            });
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },

    stores: {
        allLanguages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format('{0}/languages', CMDBuildUI.util.Config.baseUrl),
                type: 'baseproxy'
            }
        },
        enabledLanguages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format('{0}/languages?active=true', CMDBuildUI.util.Config.baseUrl),
                type: 'baseproxy'
            },
            sorters: ['description']
        }
    }
});
