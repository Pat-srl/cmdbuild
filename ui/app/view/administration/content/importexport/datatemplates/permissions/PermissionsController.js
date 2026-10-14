Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.permissions.PermissionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-datatemplates-permissions-permissions',

    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
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

    onBeforeRender: function () {
        var grid = this.getView().down('administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid');
        grid.reconfigure(grid.getStore());
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.down('grid').reconfigure(this.getView().down('grid').getStore());
        view.up('#tabpanel').getViewModel().toggleEnableTabs(1);
    },

    onSaveBtnClick: function (button) {
        var me = this;
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var store = vm.get('grantsChainedStore');
        var data = store.getData().items;
        var jsonData = [];
        Ext.Array.forEach(data, function (element) {
            if (element.crudState === 'U') {
                if (element.get('attributePrivileges') === null) {
                    element.set('attributePrivileges', {});
                }
                jsonData.push(element.getData());
            }
        });
        Ext.Ajax.request({
            url: Ext.String.format('{0}{1}', CMDBuildUI.util.Config.baseUrl, '/roles/_ALL/grants/_ANY'),
            method: 'POST',
            jsonData: jsonData,
            callback: function () {
                store.load();
                me.getView().up('#tabpanel').getViewModel().toggleEnableTabs(1);
                me.redirectTo(Ext.History.getToken(), true);
            }
        });
    },

    onCancelBtnClick: function () {
        var me = this;
        me.getView().up('#tabpanel').getViewModel().toggleEnableTabs(1);
        me.redirectTo(Ext.History.getToken(), true);
    }

});
