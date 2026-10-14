Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-configuration-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#defaultLanguageCombo': {
            change: 'onDefaultLanguageChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.view} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = me.getViewModel();

        const deferredAllLanguages = new Ext.Deferred();
        const deferredEnabledLanguages = new Ext.Deferred();
        const allLanguagesStore = vm.get('allLanguages');
        const enabledLanguagesStore = vm.get('enabledLanguages');

        Ext.Promise.all([
            // All languages
            deferredAllLanguages.promise,
            // Enabled languages are login languages
            deferredEnabledLanguages.promise,
            // Login languages config
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfig(
                'org__DOT__cmdbuild__DOT__core__DOT__login_languages'
            )
        ]).then(function (data) {
            const recordsallLanguages = data[0];
            const recordsEnabledLanguages = data[1];
            const config = data[2];

            if (!me.destroyed) {
                me.loginLanguages = config.length ? config.split(',') : [];
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }

            const enabledLanguages = [];
            let selectableLanguages = [];
            const languagescheckboxGroup = view.down('#languagescheckboxGroup');
            const loginLanguagescheckboxGroup = view.down('#loginlanguagescheckboxGroup');
            const enabledlanguagesArray = me.createLanguagesArray(recordsEnabledLanguages);
            recordsallLanguages.forEach(function (record) {
                const lang = record.get('description');
                const code = record.get('code');
                // Enabled languages
                if (Ext.Array.contains(enabledlanguagesArray, code)) {
                    record.set('active', true);
                }
                // Selectable languages
                if (Ext.Array.contains(me.loginLanguages, code)) {
                    record.set('loginactive', true);
                }
                const flag =
                    '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' +
                    code +
                    '.png" />';

                function addSelectableLanguage(record) {
                    selectableLanguages.push({
                        boxLabel: flag + lang,
                        language: lang,
                        value: record.get('loginactive'),
                        readOnly: true,
                        disabled: record.get('code') === vm.get('defaultlanguage') && me.loginLanguages.length > 0,
                        config: {
                            record: record
                        },
                        listeners: {
                            change: function (checkbox, newValue, oldValue, eOpts) {
                                const language = checkbox.config.record.get('code');
                                const defaultLanguage = vm.get('defaultlanguage');
                                const rec = checkbox.config.record;
                                rec.loginactive = newValue;
                                if (newValue) {
                                    Ext.Array.push(me.loginLanguages, language);
                                } else {
                                    Ext.Array.remove(me.loginLanguages, language);
                                }
                                // Update the origin array checkbox are generated
                                Ext.Array.map(selectableLanguages, function (item) {
                                    if (item.config.record.get('code') === language) {
                                        item.value = newValue;
                                    }
                                });
                                me.updateCheckboxes(language === defaultLanguage);
                            }
                        }
                    });
                    return selectableLanguages;
                }

                enabledLanguages.push({
                    boxLabel: flag + lang,
                    language: lang,
                    value: record.get('active'),
                    readOnly: true,
                    config: {
                        record: record
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue, eOpts) {
                            const language = checkbox.config.record;
                            if (newValue) {
                                // Add to selectable languages
                                enabledLanguagesStore.add(language);
                                selectableLanguages = addSelectableLanguage(record);
                                me.sortLanguages(selectableLanguages);
                                loginLanguagescheckboxGroup.removeAll();
                                me._loginItems = loginLanguagescheckboxGroup.add(selectableLanguages);
                                me.settingDisabled(false);
                            } else {
                                // Remove from selectable languages
                                enabledLanguagesStore.remove(language);
                                Ext.Array.remove(me.loginLanguages, language.get('code'));
                                Ext.Array.remove(
                                    selectableLanguages,
                                    me.findLanguageCheckBox(selectableLanguages, language.get('code'))
                                );
                                loginLanguagescheckboxGroup.removeAll();
                                me._loginItems = loginLanguagescheckboxGroup.add(selectableLanguages);
                                me.settingDisabled(false);
                            }
                        }
                    }
                });
                if (record.get('active')) {
                    addSelectableLanguage(record);
                }
            });
            me.sortLanguages(enabledLanguages);
            me.sortLanguages(selectableLanguages);
            me.onstoreActiveLoaded();
            me._enabledItems = languagescheckboxGroup.add(enabledLanguages);
            me._loginItems = loginLanguagescheckboxGroup.add(selectableLanguages);
        });

        allLanguagesStore.load(function (records) {
            deferredAllLanguages.resolve(records);
        });

        enabledLanguagesStore.load(function (records) {
            deferredEnabledLanguages.resolve(records);
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.settingDisabled(false);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.settingDisabled(true);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const activelanguagesArray = [];

        vm.get('enabledLanguages')
            .getRange()
            .forEach(function (item) {
                activelanguagesArray.push(item.get('code'));
            });

        const config = {
            org__DOT__cmdbuild__DOT__core__DOT__language: vm.get('defaultlanguage'),
            org__DOT__cmdbuild__DOT__core__DOT__languageprompt: vm.get('languageprompt'),
            org__DOT__cmdbuild__DOT__core__DOT__enabled_languages: activelanguagesArray.join(','),
            org__DOT__cmdbuild__DOT__core__DOT__login_languages: me.loginLanguages.join(',')
        };
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(config, null, null, me).then(function () {
            if (!vm.destroyed) {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }
        });

        me.settingDisabled(true);
    },

    /**
     * @param {boolean} view
     */
    settingDisabled: function (view) {
        this._enabledItems.forEach(function (item) {
            item.setReadOnly(view);
        });
        this._loginItems.forEach(function (item) {
            item.setReadOnly(view);
        });
    },

    /**
     * Update the checkboxes and disable the default language if needed
     * @param {boolean} fromDefaultLanguage
     */
    updateCheckboxes: function (fromDefaultLanguage = false) {
        const me = this;
        const defaultLanguage = me.getViewModel().get('defaultlanguage');
        const defaultLanguageEnabledItem = me.findLanguageCheckBox(me._enabledItems, defaultLanguage);
        const defaultLanguageLoginItem = me.findLanguageCheckBox(me._loginItems, defaultLanguage);
        let loginItemsCounter = 0;
        me._enabledItems.forEach(function (item) {
            if (item == defaultLanguageEnabledItem) {
                item.setDisabled(true);
            } else {
                item.setDisabled(false);
            }
        });
        me._loginItems.forEach(function (item) {
            if (item.getValue() && item != defaultLanguageLoginItem) {
                loginItemsCounter++;
            }
        });

        if (loginItemsCounter > 0) {
            defaultLanguageLoginItem.setDisabled(true);
            defaultLanguageLoginItem.setValue(true);
        } else {
            Ext.asap(function () {
                if (!fromDefaultLanguage) {
                    defaultLanguageLoginItem.setDisabled(false);
                    defaultLanguageLoginItem.setValue(false);
                }
            });
        }
    },

    onstoreActiveLoaded: function () {
        const view = this.getView();
        const vm = this.getViewModel();
        const enabledLanguagesStore = vm.getStore('enabledLanguages');
        const defaultLanguageCombo = view.down('#defaultLanguageCombo');
        defaultLanguageCombo.bindStore(enabledLanguagesStore);
    },

    onDefaultLanguageChange: function (comboBox, newValue, oldValue, eOpts) {
        const me = this;
        if (oldValue) {
            me.findLanguageCheckBox(me._loginItems, oldValue).setDisabled(false);
            this.updateCheckboxes();
        }
    },

    /**
     * @param {array} storeRecords
     */
    createLanguagesArray: function (storeRecords) {
        const activelanguages = [];
        storeRecords.forEach(function (language) {
            activelanguages.push(language.get('code'));
        });
        return activelanguages;
    },

    privates: {
        loginLanguages: [],
        sortLanguages: function (languages) {
            Ext.Array.sort(languages, function (a, b) {
                const lanA = a.language.toLowerCase();
                const lanB = b.language.toLowerCase();
                if (lanA < lanB) {
                    return -1;
                }
                if (lanA > lanB) {
                    return 1;
                }
                return 0;
            });
        },
        /**
         *
         * @param {Ext.form.field.ComboBox[]} checkboxList
         */
        findLanguageCheckBox: function (checkboxList, code) {
            return Ext.Array.findBy(checkboxList, function (item) {
                return item.config.record.get('code') === code;
            });
        }
    }
});
