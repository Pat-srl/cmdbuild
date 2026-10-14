Ext.define('CMDBuildUI.view.administration.content.dependencytrees.PermissionsTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dependencytrees-permissionstab',

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

    /**
     *
     * @param {CMDBuildUI.view.administration.content.dependencytrees.PermissionsTab} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getView().down('grid').reconfigure(this.getView().down('grid').getStore());
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();

        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.down('grid').reconfigure(this.getView().down('grid').getStore());
        vm.set('disabledTabs.properties', true);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const me = this;
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const vm = this.getViewModel();
        const store = vm.get('grantsChainedStore');
        const data = store.getData().items;
        const jsonData = [];

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
                vm.set('disabledTabs.properties', true);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                me.redirectTo(Ext.History.getToken(), true);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const me = this;
        const view = me.getView();

        vm.set('disabledTabs.properties', true);
        me.redirectTo(Ext.History.getToken(), true);
    }
});
