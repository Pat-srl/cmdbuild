Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',

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
        this.getView().down('treepanel').reconfigure(this.getView().down('treepanel').getStore());
        this.getView().down('grid').reconfigure(this.getView().down('grid').getStore());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.toggleEnablePermissionsTabs(0);
        this.toggleEnableTabs(1);
        this.getView().down('treepanel').reconfigure(this.getView().down('treepanel').getStore());
        this.getView().down('grid').reconfigure(this.getView().down('grid').getStore());
    },


    /**
     * @param {Ext.view.Table} view The owning TableView.
     * @param { Number} rowIndex The row index clicked on.
     * @param { Number} colIndex The column index clicked on.
     * @param {Object} item The clicked item (or this Column if multiple cfg-items were not configured).
     * @param {Event} e The click event.
     * @param {Ext.data.Model} record The Record underlying the clicked row.
     */
    onManageConfigClick: function (grid, rowIndex, colIndex, item, e, record) {
        CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ManageConfigHelper.onManageConfigClick.call(this, grid, rowIndex, colIndex, item, e, record);
    },

    /**
     * 
     * @param {Ext.view.Table} view The owning TableView.
     * @param {Number} rowIndex The row index clicked on.
     * @param {Number} colIndex The column index clicked on.
     * @param {Object} item The clicked item (or this Column if multiple cfg-items were not configured).
     * @param {Event} e The click event.
     * @param {CMDBuildUI.model.users.Grant} record The Record underlying the clicked row.
     */
    onClearConfigClick: function (grid, rowIndex, colIndex, button, event, record) {
        CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ManageConfigHelper.onClearConfigClick.call(this, grid, rowIndex, colIndex, button, event, record);
    },

    /**
     * 
     * @param {Ext.view.Table} view The owning TableView.
     * @param {Number} rowIndex The row index clicked on.
     * @param {Number} colIndex The column index clicked on.
     * @param {Object} item The clicked item (or this Column if multiple cfg-items were not configured).
     * @param {Event} e The click event.
     * @param {CMDBuildUI.model.users.Grant} record The Record underlying the clicked row.
     */
    onRemoveFilterActionClick: function (grid, rowIndex, colIndex, button, event, record) {
        CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ManageConfigHelper.onRemoveFilterActionClick.call(this, grid, rowIndex, colIndex, button, event, record);
    },

    onHierarchicalViewCheckChange: function (checkbox, value) {
        checkbox.up('administration-content').getViewModel().set('grantHierarchicalView.classes', value);
    }
});