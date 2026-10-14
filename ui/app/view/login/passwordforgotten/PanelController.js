Ext.define('CMDBuildUI.view.login.passwordforgotten.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-passwordforgotten-panel',

    control: {
        '#resetbtn': {
            click: 'onResetBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        },
        'textfield': {
            specialkey: 'onSpecialKey'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     */
    onResetBtnClick: function (btn, e) {
        var vm = btn.lookupViewModel();
        var form = this.getView();
        if (form.isValid()) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var username = vm.get('username');
            var email = vm.get('email');

            var loadmask = CMDBuildUI.util.Utilities.addLoadMask(form);
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/users/' + username + '/password/recovery',
                method: 'POST',
                jsonData: {
                    email: email
                },
                success: function () {
                    // remove previous error messages
                    CMDBuildUI.util.Notifier.closeAll();
                    // show success message
                    CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.main.password.recoverysuccess);
                    form.fireEvent("closepopup", form);
                },
                callback: function (options, success, response) {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                }
            });
        }
    },

    /**
     * @param {Ext.form.field.Text} textfield
     * @param {Event} e The click event
     */
    onSpecialKey: function (textfield, e) {
        if (e.getKey() == e.ENTER) {
            this.onResetBtnClick(textfield);
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     */
    onCancelBtnClick: function (btn, e) {
        var form = this.getView();
        form.fireEvent("closepopup", form);
    }
});
