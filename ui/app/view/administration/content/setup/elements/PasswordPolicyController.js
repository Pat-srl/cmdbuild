Ext.define('CMDBuildUI.view.administration.content.setup.elements.PasswordPolicyController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-passwordpolicy',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/setup/authentication', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const vm = button.lookupViewModel();
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);
        // TODO: workaround #1051

        const setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function () {
            if (!vm.destroyed) {
                vm.set('actions.view', true);
                vm.set('actions.edit', false);
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
    }
});