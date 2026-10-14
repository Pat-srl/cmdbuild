Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.component.parameters.ParameterController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-component-parameters-parameter',

    /**
     * On parameter label description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onParameterNameTranslateClick: function (event, button, eOpts) {
        this.openParameterTranslationPopup('description');
    },

    onParameterEmptyTextTranslateClick: function (event, button, eOpts) {
        this.openParameterTranslationPopup('emptyText');
    },

    openParameterTranslationPopup: function (translationType) {
        const vm = this.getViewModel();
        const currentIndex = vm.get('parameterIndex');
        const componentId = vm.get('componentId');
        const isGlobalParameter = vm.get('isGlobalParameter');
        const helperName = isGlobalParameter
            ? Ext.String.format('getLocaleKeyOfDashboardGlobalParameter{0}', translationType === 'emptyText' ? 'EmptyText' : 'Description')
            : Ext.String.format('getLocaleKeyOfDashboardComponentParameter{0}', translationType === 'emptyText' ? 'EmptyText' : 'Name');
        const translationCode = isGlobalParameter
            ? CMDBuildUI.util.administration.helper.LocalizationHelper[helperName](vm.get('dashboardName'), componentId)
            : CMDBuildUI.util.administration.helper.LocalizationHelper[helperName](
                  vm.get('dashboardName'),
                  componentId,
                  currentIndex
              );
        const vmLocaleObject = isGlobalParameter
            ? Ext.String.format('theGlobalParameter{0}_{1}', translationType === 'emptyText' ? 'EmptyText' : 'Description', componentId.replaceAll('-', '_'))
            : Ext.String.format(
                  'theComponentParameter{0}_{1}_{2}',
                  translationType === 'emptyText' ? 'EmptyText' : 'Description',
                  componentId.replaceAll('-', '_'),
                  currentIndex
              );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            vmLocaleObject,
            vm.getParent().get('owner').getViewModel(),
            true
        );
    }
});
