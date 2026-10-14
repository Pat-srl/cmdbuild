Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.ImportexportsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports',
    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {Ext.panel.Panel} view
     */
    onBeforeRender: function (view) {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.toggleEnablePermissionsTabs(7);
        this.toggleEnableTabs(1);
    }
});