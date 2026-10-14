Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-generaloptions',
    data: {
        values: {
            language: null,
            dateFormat: null,
            timeFormat: null,
            decimalsSeparator: null,
            thousandsSeparator: null
        },
        validations: { decimalsSeparator: true, thousandsSeparator: true }
    },

    formulas: {
        inactiveusers_value: {
            bind: { value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod}' },
            get: function (data) {
                if (data.value === '') {
                    return '';
                } else if (Ext.isString(data.value)) {
                    return data.value.replace('M', '').replace('P', '');
                } else {
                    return null;
                }
            },
            set: function (value) {
                if (value !== null) {
                    this.set(
                        'theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod',
                        'P' + value + 'M'
                    );
                } else {
                    this.set(
                        'theSetup.org__DOT__cmdbuild__DOT__auth__DOT__users__DOT__expireInactiveAfterPeriod',
                        null
                    );
                }
            }
        },

        startingClassDescription: {
            bind: { startingClass: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__startingclass}' },
            get: function (data) {
                if (data.startingClass) {
                    var initialPage = data.startingClass,
                        initialPageSplip = initialPage.split(':');

                    if (initialPageSplip.length > 1) {
                        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            initialPageSplip[1],
                            initialPageSplip[0]
                        );
                        if (object) {
                            initialPage = object.get('description');
                        }
                        return this.set('_startingClass_description', initialPage);
                    }
                    return this.set('_startingClass_description', initialPage);
                }
            }
        },
        updateData: {
            get: function () {
                this.set(
                    'values.decimalsSeparator',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.decimalsSeparator)
                );
                this.set(
                    'values.thousandsSeparator',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.thousandsSeparator)
                );
                this.set(
                    'values.dateFormat',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.dateFormat)
                );
                this.set(
                    'values.timeFormat',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timeFormat)
                );
                this.set(
                    'values.language',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.language)
                );
                this.set(
                    'values.timezone',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timezone)
                );
                this.set(
                    'values.preferredOfficeSuite',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredOfficeSuite)
                );
            }
        },

        /**
         * Update separators validations
         */
        updateSeparatorsValidations: {
            bind: { decimals: '{values.decimalsSeparator}', thousands: '{values.thousandsSeparator}' },
            get: function (data) {
                var decimals = true,
                    thousands = true;
                if (data.decimals && data.thousands && data.decimals === data.thousands) {
                    thousands = decimals = CMDBuildUI.locales.Locales.main.preferences.decimalstousandserror;
                } else if (data.decimals && !data.thousands) {
                    thousands = CMDBuildUI.locales.Locales.main.preferences.thousandserror;
                } else if (!data.decimals && data.thousands) {
                    decimals = CMDBuildUI.locales.Locales.main.preferences.decimalserror;
                } else if (!data.decimals && !data.thousands) {
                    decimals = CMDBuildUI.locales.Locales.main.preferences.decimalserror;
                    thousands = CMDBuildUI.locales.Locales.main.preferences.thousandserror;
                }
                this.set('validations.decimalsSeparator', decimals);
                this.set('validations.thousandsSeparator', thousands);
            }
        },

        dateFormatsData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.dateFormatsData();
        },

        timeFormatsData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.timeFormatsData();
        },

        decimalsSeparatorsData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.decimalsSeparatorsData();
        },

        thousandsSeparatorsData: function () {
            return [
                { value: ',', label: CMDBuildUI.locales.Locales.main.preferences.comma },
                { value: '.', label: CMDBuildUI.locales.Locales.main.preferences.period },
                { value: ' ', label: CMDBuildUI.locales.Locales.main.preferences.space }
            ];
        },

        preferredOfficeSuiteData: function () {
            return [
                { value: 'default', label: CMDBuildUI.locales.Locales.main.preferences.default },
                { value: 'msoffice', label: CMDBuildUI.locales.Locales.main.preferences.msoffice }
            ];
        },

        bulkComboData: function () {
            return CMDBuildUI.util.helper.ModelHelper.bulkComboSettingsData();
        },

        updateTimezones: {
            get: function (data) {
                this.set('storeProxyUrl', Ext.String.format('{0}/timezones', CMDBuildUI.util.Config.baseUrl));
                this.set('storeAutoLoad', true);
            }
        },
        chatEnabled: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__chat__DOT__enabled}',
            get: function (chatEnabled) {
                return chatEnabled === 'true';
            },
            set: function (value) {
                this.set('theSetup.org__DOT__cmdbuild__DOT__core__DOT__chat__DOT__enabled', value + '');
            }
        },
        chatMultitenantMode: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__chat__DOT__multitenantMode}',
            get: function (multitenantMode) {
                if (multitenantMode === 'tenant') {
                    return true;
                }
                return false;
            },
            set: function (value) {
                this.set(
                    'theSetup.org__DOT__cmdbuild__DOT__core__DOT__chat__DOT__multitenantMode',
                    value ? 'tenant' : 'global'
                );
            }
        },
        isDetailsWindowReadOnly: {
            bind: {
                openSubclass: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__redirectTo__DOT__subclass__DOT__enabled}',
                isActionEdit: '{actions.edit}'
            },
            get: function (data) {
                // From the setup the value could be a string
                const openSubclassBoolean =
                    typeof data.openSubclass === 'string' ? data.openSubclass == 'true' : data.openSubclass;
                if (data.isActionEdit) {
                    if (openSubclassBoolean) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        },
        pollingIntervalMinutes: {
            get: function (get) {
                const seconds = get(
                    'theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__polling__DOT__interval'
                );
                return seconds ? seconds / 60 : 0;
            },
            set: function (value) {
                this.set(
                    'theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__polling__DOT__interval',
                    value * 60
                );
            }
        },
        pollingIntervalDisplay: {
            bind: {
                value: '{pollingIntervalMinutes}',
                disabled: '{isPollingDisabled}'
            },
            get: function (data) {
                if (data.disabled) {
                    return CMDBuildUI.locales.Locales.administration.systemconfig.disabled;
                }
                return Ext.isEmpty(data.value)
                    ? CMDBuildUI.locales.Locales.administration.systemconfig.notset
                    : data.value;
            }
        },
        isPollingDisabled: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__enabled}',
            get: function (websocketEnabled) {
                return String(websocketEnabled) === 'true';
            }
        }
    },

    stores: {
        languages: { model: 'CMDBuildUI.model.Language', sorters: 'description', autoLoad: true, autoDestroy: true },
        dateFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dateFormatsData}',
            autoLoad: true,
            autoDestroy: true
        },
        timeFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{timeFormatsData}',
            autoLoad: true,
            autoDestroy: true
        },
        decimalsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{decimalsSeparatorsData}',
            autoLoad: true,
            autoDestroy: true
        },
        thousandsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{thousandsSeparatorsData}',
            autoLoad: true,
            autoDestroy: true
        },
        timezones: {
            autoDestroy: true,
            proxy: { type: 'baseproxy', url: '{storeProxyUrl}' },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}'
        },
        preferredOfficeSuite: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{preferredOfficeSuiteData}',
            autoDestroy: true
        },
        defaultForCardEditStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{bulkComboData}',
            autoDestroy: true
        },
        defaultForCardDeletionStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{bulkComboData}',
            autoDestroy: true
        },
        defaultForBulkAbortStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{bulkComboData}',
            autoDestroy: true
        }
    }
});
