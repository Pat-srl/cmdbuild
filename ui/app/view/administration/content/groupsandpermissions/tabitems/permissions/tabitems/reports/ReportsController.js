Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.ReportsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports',
    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function () {
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
        this.toggleEnablePermissionsTabs(5);
        this.toggleEnableTabs(1);
    }
});