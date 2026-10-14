Ext.define('CMDBuildUI.view.joinviews.configuration.permissions.PermissionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-permissions-permissions',

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
        view.up('#joinviewtabpanel').getViewModel().toggleEnableTabs(2);
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
                me.redirectTo(Ext.History.getToken(), true);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    onCancelBtnClick: function () {
        var me = this,
            view = me.getView();
        me.redirectTo(Ext.History.getToken(), true);
        view.up('#joinviewtabpanel').getViewModel().toggleEnableTabs(2);
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
