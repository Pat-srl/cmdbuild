Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.defaultfilters.DefaultFiltersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters',

    control: {
        '#filtertree': {
            beforeedit: 'onBeforeEdit'
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

    /**
     * 
     * @param {*} editor 
     * @param {*} context 
     * @param {*} eOpts 
     */
    onBeforeEdit: function (editor, context, eOpts) {
        var vm = context.view.lookupViewModel();
        if (vm.get('actions.view')) {
            return false;
        }
        var store = Ext.create('Ext.data.Store', {
            source: 'searchfilters.Searchfilters',
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format('/classes/{0}/filters', context.record.get('objecttype')),
                extraParams: {
                    limit: 0
                }
            },
            autoLoad: true,
            autoDestroy: true
        }).load({
            scope: this,
            callback: function (records, operation, success) {
                store.clearFilter();
                store.filterBy(function (item) {
                    return item.get('shared') === true && item.get('target') === context.record.get('objecttype');
                });
                context.column.field.setStore(store);
            }
        });

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel('administration-content-groupsandpermissions-view');
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
        var vmView = Ext.getCmp('CMDBuildAdministrationContentGroupView').getViewModel();
        vmView.toggleEnableTabs(this.getView().up().tabIndex);
    },
    /**
     * 
     * @param {Ext.data.TreeStore} store 
     */
    onTreeStoreDataChanged: function (store) {
        var treepanel = this.getView().down('treepanel');
        treepanel.setStore(store);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var view = me.getView();
        button.setDisabled(true);
        var vm = view.getViewModel();
        var jsonData = [];
        Ext.Array.forEach(vm.get('gridStore').getRange(), function (item) {
            if (item.get('filter')) {
                jsonData.push({
                    _id: item.get('filter'),
                    _defaultFor: item.get('objecttype')
                });
            }
        });
        var mainVM = me.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/roles/{1}/filters',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('theGroup._id')
            ),
            method: 'POST',
            jsonData: jsonData,

            callback: function () {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                vm.get('theGroup').reject();
                vm.set('actions.view', true);
                vm.set('actions.edit', false);
                vm.set('actions.add', false);
                mainVM.toggleEnableTabs();
            }
        });

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.toggleEnableTabs();
        vm.get('theGroup').reject();
        if (vm.get('actions.add')) {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPermissionUrl();
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl);
        }
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.set('actions.add', false);
    }
});