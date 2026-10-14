Ext.define('CMDBuildUI.view.administration.content.users.elements.changepassword.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-users-changepassword-form',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.users.elements.changepassword.Form} view
     *
     */
    onAfterRender: function (view) {
        var vm = this.getViewModel();
        CMDBuildUI.util.Utilities.showLoader(true, view);
        vm.bind({
            bindTo: '{theUser}',
            single: true
        }, function () {
            CMDBuildUI.util.Utilities.showLoader(false, view);
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.showLoader(true);
        var vm = button.lookupViewModel();
        var form = vm.getView();
        if (form.isValid()) {
            vm.get('theUser').save({
                success: function (record, operation) {
                    CMDBuildUI.util.Utilities.closePopup('popup-change-password');
                    CMDBuildUI.util.Utilities.showLoader(false);
                },
                failure: function () {
                    CMDBuildUI.util.Utilities.showLoader(false);
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
        CMDBuildUI.util.Utilities.closePopup('popup-change-password');
    }
});