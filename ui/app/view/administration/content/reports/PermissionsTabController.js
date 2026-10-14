Ext.define('CMDBuildUI.view.administration.content.reports.PermissionsTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-reports-permissionstab',

    mixins: ['CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'],

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
        view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(1);
    },

    onSaveBtnClick: function (button) {
        var me = this;
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var vm = me.getViewModel();
        var view = me.getView();
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
                view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(1);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                me.redirectTo(Ext.History.getToken(), true);
            }
        });
    },

    onCancelBtnClick: function () {
        var me = this,
            view = me.getView();
        view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(1);
        me.redirectTo(Ext.History.getToken(), true);
    }
});
