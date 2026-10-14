Ext.define('CMDBuildUI.view.administration.content.setup.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.setup.View} view
     */
    onBeforeRender: function (view) {
        const vm = this.getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.loadData();
        view.down('panel').add({
            xtype: Ext.String.format('administration-content-setup-elements-{0}', vm.get('currentPage'))
        });
    },

    /**
     * Save the configs with an async
     * @param {Ext.view.View} view
     */
    onAsyncSave: function (view) {
        const vm = view.getViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditSetupBtnClick: function (button, e, eOpts) {
        this.getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.loadData();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        button.disable();
        const vm = this.getViewModel();
        const currentPageType = vm.get('currentPage');

        if (currentPageType === 'homepages') {
            const homepagesPanel = this.getView().down('administration-content-setup-elements-homepages');
            if (homepagesPanel) {
                homepagesPanel.getController().collectSearchClasses();
            }
        }

        if (currentPageType === 'multitenant') {
            if (vm.get('multitenantFieldsDisabled')) {
                me.saveMultitenantData(button);
            } else {
                CMDBuildUI.util.Msg.confirm(
                    CMDBuildUI.locales.Locales.administration.common.messages.attention,
                    Ext.String.format(
                        '{0}</br>{1}</br>{2}',
                        CMDBuildUI.locales.Locales.administration.systemconfig.multitenantactivationmessage,
                        CMDBuildUI.locales.Locales.administration.systemconfig.multitenantlogoutmessage,
                        CMDBuildUI.locales.Locales.administration.systemconfig.multitenantapplychangerequest
                    ),
                    function (btnText) {
                        if (btnText.toLowerCase() === 'yes') {
                            me.saveMultitenantData(button, true);
                        } else {
                            button.enable();
                        }
                    },
                    this
                );
            }
        } else {
            me.uploadIcon(button);
        }
    },

    privates: {
        /**
         * Load data from server, format keys and set vm data for binding
         * @private
         */
        loadData: function () {
            const vm = this.getViewModel();
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (configs) {
                if (!vm.destroyed) {
                    configs.forEach(function (key) {
                        vm.set(Ext.String.format('theSetup.{0}', key._key), key.hasValue ? key.value : key['default']);
                    });
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                }
            });
        },

        /**
         * @param {Ext.data.TreeModel[]|Object[]} data
         * @return {Object[]} checked nodes
         * @private
         */
        getCheckedRecords: function (data) {
            var me = this,
                records = [];

            Ext.Array.forEach(data, function (item) {
                var children = !item.isModel ? item.children : item.get('children');
                var checked = !item.isModel ? item.checked : item.get('checked');

                if (checked) {
                    records.push(item);
                }
                if (children && children.length) {
                    records = Ext.Array.merge(records, me.getCheckedRecords(children)) || [];
                }
            });
            return records;
        },

        /**
         * Save the configuration
         *
         * @param {Ext.button.Button} button
         */
        saveData: function (button) {
            const vm = button.lookupViewModel();
            const theSetup = vm.get('theSetup');

            const isWsEnabledInForm = theSetup['org__DOT__cmdbuild__DOT__services__DOT__websocket__DOT__enabled'];
            CMDBuildUI.util.helper.SessionHelper.initCommunicationMethod(isWsEnabledInForm);

            Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);
            // TODO: workaround #1051

            const setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
                /** theSetup */
                theSetup,
                /** reloadOnSucces */
                true,
                /** forceDropCache */
                false,
                this
            );

            setData.then(function (transport) {
                if (!vm.destroyed) {
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                }
            });
            setData.always(function () {
                if (!button.destroyed) {
                    button.enable();
                }
                if (Ext.getBody().isMasked()) {
                    Ext.getBody().unmask();
                }
            });
        },

        /**
         * Save the configuration of multitenant
         *
         * @param {Ext.button.Button} button
         * @param {Boolean} enableTenant
         */
        saveMultitenantData: function (button, enableTenant) {
            const vm = this.getViewModel();
            const setData = CMDBuildUI.util.administration.helper.ConfigHelper.setMultitenantData(
                vm.get('theSetup'),
                vm.get('multitenantFieldsDisabled')
            );
            setData.then(function (transport) {
                if (enableTenant) {
                    CMDBuildUI.util.Ajax.setActionId('logout');
                    vm.get('theSession').erase({
                        success: function (record, operation) {
                            window.location.reload();
                        }
                    });
                } else if (!vm.destroyed) {
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    window.location.reload();
                }
            });
            setData.always(function () {
                if (!button.destroyed && button.enable) {
                    button.enable();
                }
            });
        },

        /**
         * @private
         * @param {Ext.Button.button} button
         */
        uploadIcon: function (button) {
            const me = this;
            const vm = this.getViewModel();
            CMDBuildUI.util.Ajax.setActionId('config.logo.upload');
            const generalForm = this.getView().down('administration-content-setup-elements-generaloptions');
            const input = generalForm ? generalForm.down('#iconFile').extractFileInput() : null;

            if (!generalForm || !input || !input.files.length) {
                me.saveData(button);
            } else {
                // init formData
                const formData = new FormData();
                // get url
                const url = Ext.String.format(
                    '{0}/uploads?overwrite_existing=true&path=images/companylogo/',
                    CMDBuildUI.util.Config.baseUrl
                );
                // upload
                CMDBuildUI.util.administration.File.upload('POST', formData, input, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (response && response.data) {
                            vm.set('theSetup.org__DOT__cmdbuild__DOT__core__DOT__companyLogo', response.data._id);
                            me.saveData(button);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                            CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                        }
                        me.saveData(button);
                    }
                });
            }
        }
    }
});
