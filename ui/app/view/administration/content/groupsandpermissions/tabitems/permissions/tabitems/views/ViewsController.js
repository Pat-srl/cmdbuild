Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ViewsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views',
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
        this.toggleEnablePermissionsTabs(2);
        this.toggleEnableTabs(1);
        button.up().down('grid').reconfigure();
    },

    /**
     * On disabled action button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onManageConfigClick: function (grid, rowIndex, colIndex, button, event, record) {
        CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ManageConfigHelper.onManageConfigClick.call(this, grid, rowIndex, colIndex, button, event, record);
    }
});