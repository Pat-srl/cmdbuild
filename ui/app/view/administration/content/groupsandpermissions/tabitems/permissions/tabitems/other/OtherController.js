Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.other.OtherController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other',

    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#administration-other-permissions': {
            afterrender: 'onOtherAfterRender',
            actionfiltersrowclick: 'onActionFiltersClick'
        }
    },

    onOtherAfterRender: function (grid) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{storeData}'
        }, function () {
            grid.setStore(vm.get('otherStore'));
        });

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
        this.toggleEnablePermissionsTabs(9);
        this.toggleEnableTabs(1);
        button.up().down('grid').reconfigure();
    }
});