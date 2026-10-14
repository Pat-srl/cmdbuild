Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.form.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-helps-form-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        Ext.Ajax.request({
            url: Ext.String.format("{0}/translations/{1}", CMDBuildUI.util.Config.baseUrl, Ext.String.format('activity.{0}.{1}.instructions', vm.get('objectTypeName'), vm.get('activityName'))),
            method: 'GET',
            success: function (_response) {

                var response = Ext.JSON.decode(_response.responseText);
                if (response && response.data['default']) {
                    vm.set('help', response.data['default']);
                } else {
                    vm.set('help', vm.get('theActivity.instructions'));
                }
                vm.set('originalHelpValue', vm.get('help'));
            },
            error: function (response) {
                CMDBuildUI.util.Logger.log("unable to fetch the translations of the instruction", CMDBuildUI.util.Logger.levels.error);
                vm.set('help', vm.get('theActivity.instructions'));
            }
        });
    },


    /**
     * On translate button click
     * @param {Event} e
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (e, button, eOpts) {
        var vm = this.getViewModel();
        var translationCode = Ext.String.format('activity.{0}.{1}.instructions', vm.get('objectTypeName'), vm.get('activityName'));        
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm, true, 'htmleditor');
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onSaveBtnClick: function (button) {
        var vm = this.getViewModel();
        var jsonData = {
            'default': vm.get('help')
        };
        Ext.Ajax.request({
            url: Ext.String.format("{0}/translations/{1}", CMDBuildUI.util.Config.baseUrl, Ext.String.format('activity.{0}.{1}.instructions', vm.get('objectTypeName'), vm.get('activityName'))),
            method: 'PUT',
            jsonData: jsonData,
            success: function (_response) {
                var response = Ext.JSON.decode(_response.responseText);
                var successCalback = function () {
                    if (response && response['default']) {
                        vm.set('help', response.data['default']);
                    } else {
                        vm.set('help', vm.get('theActivity.instructions'));
                    }                    
                    vm.get('grid').getViewModel().get('activitiesStore').load();
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                };
                if (vm.get('theTranslation')) {
                    vm.get('theTranslation').save({
                        callback: successCalback
                    });
                } else {
                    successCalback();
                }
            },
            error: function (response) {
                CMDBuildUI.util.Logger.log("unable to save the default translations of the instruction", CMDBuildUI.util.Logger.levels.error);
            }
        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onCancelBtnClick: function (button) {
        var vm = button.lookupViewModel();
        vm.set('help', vm.get('originalHelpValue'));
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    }

});