Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfigController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig',

    control: {
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
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this;
        button.setDisabled(true);
        var vm = me.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        if (vm.get('theGroup').isValid()) {
            var theGroup = vm.get('theGroup');
            delete theGroup.data.system;
            Ext.apply(theGroup.data, theGroup.getAssociatedData());
            theGroup.save({
                failure: function () {
                    button.setDisabled(false);
                },
                success: function (record, operation) {
                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    vm.set('actions.view', true);
                    vm.set('actions.edit', false);
                    vm.set('actions.add', false);
                    vm.toggleEnableTabs();
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        this.redirectTo(
            Ext.String.format('administration/groupsandpermissions/{0}', vm.get('theGroup').get('_id')),
            true
        );
    }
});
