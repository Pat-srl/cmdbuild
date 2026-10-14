Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.processes.ProcessesController', {
    extend: 'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes',
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
        var vm = view.up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);        
        this.getView().down('treepanel').reconfigure( this.getView().down('treepanel').getStore());
        this.getView().down('grid').reconfigure( this.getView().down('grid').getStore());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.toggleEnablePermissionsTabs(1);
        this.toggleEnableTabs(1);
        this.getView().down('treepanel').reconfigure( this.getView().down('treepanel').getStore());
        this.getView().down('grid').reconfigure( this.getView().down('grid').getStore());
    },

    onHierarchicalViewCheckChange: function (checkbox, value) {
        checkbox.up('administration-content').getViewModel().set('grantHierarchicalView.processes', value);
    }
});