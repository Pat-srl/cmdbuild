Ext.define('CMDBuildUI.view.administration.content.searchfilters.PermissionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-searchfilters-permissions',

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
        this.getView().down('grid').reconfigure(this.getView().down('grid').getStore());
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.down('grid').reconfigure(this.getView().down('grid').getStore());
        view.up('administration-content-searchfilters-tabpanel').getViewModel().toggleEnableTabs(1);
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
                button.up('administration-content-searchfilters-tabpanel').getViewModel().toggleEnableTabs(1);
                me.redirectTo(Ext.History.getToken(), true);
            }
        });
    },

    onCancelBtnClick: function () {
        var me = this;
        var view = me.getView();
        view.up('administration-content-searchfilters-tabpanel').getViewModel().toggleEnableTabs(1);
        me.redirectTo(Ext.History.getToken(), true);
    }
});
