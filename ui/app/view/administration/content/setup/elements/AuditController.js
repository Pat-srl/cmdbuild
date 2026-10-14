Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-audit',

    control: {
        '#editBtn': {
            click: 'onEditBtnCLick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnCLick: function (btn, e, eOpts) {
        var vm = btn.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.set('disabledTabs.log', true);
        vm.set('disabledTabs.retention', true);
        vm.set('disabledTabs.audit', false);
        vm.set('disabledTabs.governancedata', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (btn, e, eOpts) {
        this.redirectTo('administration/setup/logs', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button) {
        var me = this,
            vm = button.lookupViewModel();
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);
        var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function (transport) {
            me.onCancelBtnClick();
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
