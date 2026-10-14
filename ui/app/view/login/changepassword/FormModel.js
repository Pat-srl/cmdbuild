Ext.define('CMDBuildUI.view.login.changepassword.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-changepassword-form',

    data: {
        username: null,
        oldpassword: null,
        newpassword: null,
        confirmpassword: null
    },

    formulas: {
        updateusername: {
            get: function () {
                var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
                if (session) {
                    this.set("username", session.get("username"));
                }
            }
        },

        isvalidpassword: {
            bind: {
                username: '{username}',
                oldpassword: '{oldpassword}',
                newpassword: '{newpassword}'
            },
            get: function (data) {
                if (data.username && data.newpassword) {
                    return CMDBuildUI.util.Utilities.validatePassword(data.newpassword, data.oldpassword, data.username);
                }
            }
        },

        isvalidconfirmpassword: {
            bind: {
                newpassword: '{newpassword}',
                confirmpassword: '{confirmpassword}'
            },
            get: function (data) {
                return data.newpassword !== data.confirmpassword ? CMDBuildUI.locales.Locales.main.password.err_confirm : true;
            }
        }
    }

});