Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.permissions.PermissionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-permissions-permissions',

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
        view.up('administration-content-classes-view').getViewModel().toggleEnableTabs(7);
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
                button.up('administration-content-classes-view').getViewModel().toggleEnableTabs(7);
                me.redirectTo(Ext.History.getToken(), true);
            }
        });
    },

    onCancelBtnClick: function () {
        var me = this,
            view = this.getView();
        view.up('administration-content-classes-view').getViewModel().toggleEnableTabs(7);
        me.redirectTo(Ext.History.getToken(), true);
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
    }
});
