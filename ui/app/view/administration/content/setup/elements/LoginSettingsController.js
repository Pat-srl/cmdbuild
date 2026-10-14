Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettingsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-loginsettings',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#modeConfig': {
            change: 'onModeConfigChange'
        },
        '#operatorClass': {
            change: 'onOperatorClassChange'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const vm = button.lookupViewModel();
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        const setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function (transport) {
            if (!vm.destroyed) {
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
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
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/setup/authentication', true);
    },

    /**
     *
     * @param {Ext.form.field.Field} combo
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onModeConfigChange: function (combo, newValue, oldValue, eOpts) {
        const view = this.getView();
        const vm = this.getViewModel();

        Ext.Array.forEach(
            ['#operatorClass', '#matchingAttribute', '#matchingUserProperty'],
            function (itemIdElement, index, allItemIdElements) {
                const field = view.down(itemIdElement);
                if (field) {
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                        field,
                        newValue !== 'CMDBUILD_CLASS',
                        view
                    );
                }
            }
        );

        if (oldValue) {
            vm.set('theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__class', null);
            vm.set('theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__attribute', null);
            vm.set('theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__userProperty', null);
        }
    },

    /**
     *
     * @param {Ext.form.field.Field} combo
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onOperatorClassChange: function (combo, newValue, oldValue, eOpts) {
        const vm = this.getViewModel();
        const selectedClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(newValue);
        if (!selectedClass) {
            vm.set('attributeOptions', []);
            return;
        }

        selectedClass.getAttributes().then(function (attributes) {
            const options = [];
            attributes.getRange().forEach(function (attr) {
                if (attr.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string) {
                    options.push({
                        value: attr.get('name'),
                        label: attr.get('description') || attr.get('name')
                    });
                }
            });
            vm.set('attributeOptions', options);
        });
    }
});
