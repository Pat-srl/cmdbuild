Ext.define('CMDBuildUI.view.joinviews.configuration.items.GeneralPropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-generalproperties',

    onDescriptionTranslationClick: function () {        
        var mainView = this.getView().up('joinviews-configuration-main');        
        var mainVm = mainView.getViewModel();    
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewDescription(mainVm.get('theView.name'));
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, mainVm.get('action'), 'theDescriptionTranslation', mainVm, true);

    }

});