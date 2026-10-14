Ext.define('CMDBuildUI.view.administration.content.domains.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-topbar',

    control: {
        '#adddomain': {
            click: 'onAddDomainClickBtn',
            render: 'onRenderAddDomainBtn'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddDomainClickBtn: function (button, e, eOpts) {
        this.redirectTo('administration/domains', true);
        const vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
        this.getView()
            .lookupViewModel()
            .set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
    },

    /**
     *
     * @param {Ext.Component} button
     * @param {Object} eOpts
     */
    onRenderAddDomainBtn: function (button, eOpts) {
        button.setDisabled(!this.getView().lookupViewModel().get('theSession.rolePrivileges.admin_domains_modify'));
    }
});
