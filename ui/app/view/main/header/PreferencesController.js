Ext.define('CMDBuildUI.view.main.header.PreferencesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-preferences',
    control: {
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onSaveBtnClick: function (btn, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var view = this.getView(),
            vm = btn.lookupViewModel(),
            oldLanguage = CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.language),
            file = view.down("#iconFile").getValue(),
            values = vm.get("values"),
            cancelBtn = view.down("#cancelbtn"),
            newLanguage;

        btn.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

        if (values.cm_user_language !== oldLanguage) {
            if (values.cm_user_language) {
                newLanguage = values.cm_user_language;
            } else {
                newLanguage = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.defaultlanguage);
            }
            CMDBuildUI.util.helper.LocalStorageHelper.set(
                CMDBuildUI.util.helper.LocalStorageHelper.keys.loginlanguage,
                newLanguage
            );
        }
        values.cm_ui_preferredMenu = Ext.JSON.encode(values.cm_ui_preferredMenu);
        function updatePreferences() {
            CMDBuildUI.util.helper.UserPreferences.updatePreferences(values).then(function () {
                CMDBuildUI.util.helper.UserPreferences.load().then(function () {
                    CMDBuildUI.util.helper.UserPreferences.formats = {};
                    CMDBuildUI.util.Utilities.closePopup('UserPreferences');

                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    // If the user changes the language reload the page to upadate the locales
                    if (newLanguage) {
                        window.location.reload();
                    }
                });
            });
        }

        if (!Ext.isEmpty(file)) {
            var reader = new FileReader();
            reader.onload = function () {
                vm.set('values.icon', reader.result);
                updatePreferences();
            };
            reader.readAsDataURL(file[0].get("file"));
        } else {
            updatePreferences();
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onCancelBtnClick: function (btn, e, eOpts) {
        CMDBuildUI.util.Utilities.closePopup('UserPreferences');
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onRemoveImageBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        if (vm.get('values.icon') === vm.get('values._icon')) {
            vm.set('values.icon', '');
            vm.set('values._icon', '');
        } else {
            vm.set('values._icon', '');
        }
    }
});