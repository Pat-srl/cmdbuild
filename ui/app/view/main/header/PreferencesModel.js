Ext.define('CMDBuildUI.view.main.header.PreferencesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-preferences',

    data: {
        values: {
            cm_user_language: null,
            cm_ui_startingClass: null,
            cm_ui_dateFormat: null,
            cm_ui_timezone: null,
            cm_ui_timeFormat: null,
            cm_ui_decimalsSeparator: null,
            cm_ui_thousandsSeparator: null,
            cm_ui_preferredOfficeSuite: null,
            cm_ui_preferredFileCharset: null,
            cm_ui_preferredCsvSeparator: null,
            cm_ui_startDay: null,
            cm_ui_email_groupByStatus: null,
            cm_ui_email_defaultDelay: null,
            cm_ui_preferredMenu: {
                position: 'before',
                collapsed: false
            }
        },
        validations: {
            decimalsSeparator: true,
            thousandsSeparator: true
        }
    },

    formulas: {
        updateData: {
            get: function () {
                this.set(
                    'values.cm_user_language',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.language)
                );
                this.set(
                    'values.cm_ui_startingClass',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startingpage)
                );
                this.set(
                    'values.cm_ui_dateFormat',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.dateFormat)
                );
                this.set(
                    'values.cm_ui_timezone',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timezone)
                );
                this.set(
                    'values.cm_ui_timeFormat',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timeFormat)
                );
                this.set(
                    'values.cm_ui_decimalsSeparator',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.decimalsSeparator)
                );
                this.set(
                    'values.cm_ui_thousandsSeparator',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.thousandsSeparator)
                );
                this.set(
                    'values.cm_ui_preferredOfficeSuite',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredOfficeSuite)
                );
                this.set(
                    'values.cm_ui_preferredFileCharset',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredfilecharset)
                );
                this.set(
                    'values.cm_ui_preferredCsvSeparator',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredCsvSeparator)
                );
                this.set(
                    'values.cm_ui_startDay',
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startDay)
                );
                this.set(
                    'values.cm_ui_preferredMenu.position',
                    CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuPosition()
                );
                this.set(
                    'values.cm_ui_preferredMenu.collapsed',
                    CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuCollapsed()
                );
                this.set(
                    'values.cm_ui_preferredMenu.items',
                    CMDBuildUI.util.helper.UserPreferences.getFavouritesMenuItems()
                );
                // avatar icon
                this.set(
                    Ext.String.format('values.{0}', CMDBuildUI.model.users.Preference.icon),
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.icon)
                );
                // avatar preset
                this.set(
                    Ext.String.format('values._{0}', CMDBuildUI.model.users.Preference.icon),
                    CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.icon)
                );
                const notificationSoundVolume = CMDBuildUI.util.helper.UserPreferences.get(
                    CMDBuildUI.model.users.Preference.notifications.soundVolume
                );
                this.set(
                    'values.cm_ui_notifications_sound_volume',
                    notificationSoundVolume == null ? 100 : notificationSoundVolume
                );
                this.set(
                    Ext.String.format('values.{0}', CMDBuildUI.model.users.Preference.notifications.soundEnabled),
                    CMDBuildUI.util.helper.UserPreferences.get(
                        CMDBuildUI.model.users.Preference.notifications.soundEnabled
                    )
                );
                this.set(
                    Ext.String.format('values.{0}', CMDBuildUI.model.users.Preference.notifications.groupEmailByStatus),
                    CMDBuildUI.util.helper.UserPreferences.get(
                        CMDBuildUI.model.users.Preference.notifications.groupEmailByStatus
                    )
                );
                this.set(
                    Ext.String.format('values.{0}', CMDBuildUI.model.users.Preference.notifications.defaultEmailDelay),
                    CMDBuildUI.util.helper.UserPreferences.get(
                        CMDBuildUI.model.users.Preference.notifications.defaultEmailDelay
                    )
                );
            }
        },

        /**
         * Update separators validations
         */
        updateSeparatorsValidations: {
            bind: {
                decimals: '{values.cm_ui_decimalsSeparator}',
                thousands: '{values.cm_ui_thousandsSeparator}'
            },
            get: function (data) {
                var decimals = true,
                    thousands = true;
                if (data.decimals && data.thousands && data.decimals === data.thousands) {
                    thousands = decimals = CMDBuildUI.locales.Locales.main.preferences.decimalstousandserror;
                } else if (data.decimals && !data.thousands) {
                    thousands = CMDBuildUI.locales.Locales.main.preferences.thousandserror;
                } else if (!data.decimals && data.thousands) {
                    decimals = CMDBuildUI.locales.Locales.main.preferences.decimalserror;
                }
                this.set('validations.decimalsSeparator', decimals);
                this.set('validations.thousandsSeparator', thousands);
            }
        },

        dateFormatsData: function () {
            return [
                {
                    label: 'dd/mm/yyyy',
                    value: 'd/m/Y'
                },
                {
                    label: 'dd-mm-yyyy',
                    value: 'd-m-Y'
                },
                {
                    label: 'dd.mm.yyyy',
                    value: 'd.m.Y'
                },
                {
                    label: 'mm/dd/yyyy',
                    value: 'm/d/Y'
                },
                {
                    label: 'yyyy/mm/dd',
                    value: 'Y/m/d'
                },
                {
                    label: 'yyyy-mm-dd',
                    value: 'Y-m-d'
                }
            ];
        },

        timeFormatsData: function () {
            return [
                {
                    value: 'H:i:s',
                    label: CMDBuildUI.locales.Locales.main.preferences.twentyfourhourformat
                },
                {
                    value: 'h:i:s A',
                    label: CMDBuildUI.locales.Locales.main.preferences.twelvehourformat
                }
            ];
        },

        decimalsSeparatorsData: function () {
            return [
                {
                    value: ',',
                    label: CMDBuildUI.locales.Locales.main.preferences.comma
                },
                {
                    value: '.',
                    label: CMDBuildUI.locales.Locales.main.preferences.period
                }
            ];
        },

        thousandsSeparatorsData: function () {
            return [
                {
                    value: ',',
                    label: CMDBuildUI.locales.Locales.main.preferences.comma
                },
                {
                    value: '.',
                    label: CMDBuildUI.locales.Locales.main.preferences.period
                },
                {
                    value: ' ',
                    label: CMDBuildUI.locales.Locales.main.preferences.space
                }
            ];
        },

        preferredOfficeSuiteData: function () {
            return [
                {
                    value: 'default',
                    label: CMDBuildUI.locales.Locales.main.preferences.default
                },
                {
                    value: 'msoffice',
                    label: CMDBuildUI.locales.Locales.main.preferences.msoffice
                }
            ];
        },

        preferredCsvSeparatorData: function () {
            return CMDBuildUI.model.importexports.Template.getCsvSeparators();
        },

        yesOrNoData: function () {
            return [
                {
                    value: true,
                    label: CMDBuildUI.locales.Locales.main.preferences.yes
                },
                {
                    value: false,
                    label: CMDBuildUI.locales.Locales.main.preferences.no
                }
            ];
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
        },

        updateTimezones: {
            get: function (data) {
                this.set('storeProxyUrl', Ext.String.format('{0}/timezones', CMDBuildUI.util.Config.baseUrl));
                this.set('storeAutoLoad', true);
            }
        },

        favouritesMenuLocationData: function () {
            return [
                {
                    value: CMDBuildUI.util.helper.UserPreferences.menuPosition.before,
                    label: CMDBuildUI.locales.Locales.main.preferences.showfovouritesbefore
                },
                {
                    value: CMDBuildUI.util.helper.UserPreferences.menuPosition.after,
                    label: CMDBuildUI.locales.Locales.main.preferences.showfovouritesafter
                }
            ];
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Languages.getLanguagesUrl(),
                extraParams: {
                    active: true
                }
            }
        },

        dateFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dateFormatsData}',
            autoDestroy: true
        },

        timeFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{timeFormatsData}',
            autoDestroy: true
        },

        decimalsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{decimalsSeparatorsData}',
            autoDestroy: true
        },

        thousandsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{thousandsSeparatorsData}',
            autoDestroy: true
        },

        timezones: {
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}'
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}'
        },

        preferredOfficeSuite: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{preferredOfficeSuiteData}',
            autoDestroy: true
        },

        preferredFileCharset: {
            fields: ['_id', 'description'],
            proxy: {
                type: 'baseproxy',
                url: '/system/charsets'
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true
        },

        csvSeparatorsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{preferredCsvSeparatorData}',
            autoDestroy: true
        },

        startDays: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{startDaysData}',
            autoDestroy: true
        },

        defaultEmailDelay: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [
                {
                    value: '0',
                    label: '0'
                },
                {
                    value: '5',
                    label: '5'
                },
                {
                    value: '10',
                    label: '10'
                },
                {
                    value: '20',
                    label: '20'
                },
                {
                    value: '30',
                    label: '30'
                }
            ],
            autoDestroy: true
        },

        yesOrNo: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{yesOrNoData}'
        },

        favouritesMenuLocationStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{favouritesMenuLocationData}'
        }
    }
});
