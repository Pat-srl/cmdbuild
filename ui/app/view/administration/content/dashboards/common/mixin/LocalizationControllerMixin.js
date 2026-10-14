Ext.define('CMDBuildUI.view.administration.content.dashboards.common.mixin.LocalizationControllerMixin', {
    extend: 'Ext.Mixin',

    mixinConfig: {
        id: 'localizationController'
    },

    // ======================================================
    // ON TRANSLATE BUTTON CLICK
    // ======================================================

    onTranslateComponentDescriptionClick: function (event, button, eOpts) {
        this.openComponentTranslationPopup(
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentDescription,
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentDescription,
            'theComponentDescription'
        );
    },

    onTranslateCategoryAxisTitleClick: function (event, button, eOpts) {
        this.openComponentTranslationPopup(
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentCategoryAxis,
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentCategoryAxis,
            'theComponentCategoryAxisTitle'
        );
    },

    onTranslateValueAxisLabelClick: function (event, button, eOpts) {
        this.openComponentTranslationPopup(
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentValueAxis,
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentValueAxis,
            'theComponentValueAxisLabel'
        );
    },

    onTranslateComponentLabelFieldClick: function (event, button, eOpts) {
        this.openComponentTranslationPopup(
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentLabelField,
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentLabelField,
            'theComponentLabelField'
        );
    },

    privates: {
        /**
         * Check if form context is homepage, based on the card owner xtype
         * @returns {Boolean}
         */
        isHomepageContext: function () {
            const vm = this.getViewModel();
            const owner = vm.get('owner');
            return !!(owner && owner.xtype === 'view-administration-content-dashboards-homepage-card');
        },

        /**
         * Opens the localization popup for a component-level translatable field.
         * @param {Function} getDashboardLocaleKeyFn LocalizationHelper.getLocaleKeyOfDashboardComponent* function
         * @param {Function} getHomepageLocaleKeyFn LocalizationHelper.getLocaleKeyOfHomepageComponent* function
         * @param {String} vmLocalePrefix prefix of the ViewModel field holding the locale value
         */
        openComponentTranslationPopup: function (getDashboardLocaleKeyFn, getHomepageLocaleKeyFn, vmLocalePrefix) {
            const vm = this.getViewModel();
            const ownerVm = vm.get('owner').getViewModel();
            const componentId = vm.get('theComponent._id');
            const getLocaleKeyFn = this.isHomepageContext() ? getHomepageLocaleKeyFn : getDashboardLocaleKeyFn;
            const translationCode = getLocaleKeyFn(ownerVm.get('theDashboard.name'), componentId);
            const vmLocaleObject = Ext.String.format('{0}_{1}', vmLocalePrefix, componentId.replaceAll('-', '_'));

            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                translationCode,
                vm.get('action'),
                vmLocaleObject,
                ownerVm,
                true
            );
        },

        undoLocales: function (theDashboard, theComponent, vm) {
            this.undoParametersLocalizations(
                theDashboard,
                theComponent,
                theComponent.dataSourceParameters().getData(),
                vm
            );
            const componentId = theComponent.get('_id').replaceAll('-', '_');
            [
                'theComponentDescription',
                'theComponentCategoryAxisTitle',
                'theComponentValueAxisLabel',
                'theComponentLabelField'
            ].forEach(function (prefix) {
                const key = Ext.String.format('{0}_{1}', prefix, componentId);
                if (vm.get(key)) vm.set(key, undefined);
            });

            if (theComponent.getTilesStore) {
                theComponent.getTilesStore().each(function (tile) {
                    const tileId = tile.get('_id').replaceAll('-', '_');
                    const key = Ext.String.format('theComponentTileLabel_{0}', tileId);
                    if (vm.get(key)) vm.set(key, undefined);
                });
            }
        },

        undoParametersLocalizations: function (theDashboard, theComponent, parameters, vm, forceDeletion) {
            const isHomepage = this.isHomepageContext();
            Ext.Array.forEach(parameters.items, function (parameter, index) {
                const parameterDescriptionLocaleVmObject = Ext.String.format(
                    'theComponentParameterDescription_{0}_{1}',
                    theComponent.get('_id').replaceAll('-', '_'),
                    index
                );
                if (forceDeletion) {
                    CMDBuildUI.util.Ajax.setActionId('delete-componentparameter-locales');
                    const getLocaleKeyFn = isHomepage
                        ? CMDBuildUI.util.administration.helper.LocalizationHelper
                              .getLocaleKeyOfHomepageComponentParameterName
                        : CMDBuildUI.util.administration.helper.LocalizationHelper
                              .getLocaleKeyOfDashboardComponentParameterName;
                    const localeId = getLocaleKeyFn(theDashboard.get('name'), theComponent.get('_id'), index);
                    Ext.Ajax.request({
                        url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, localeId),
                        method: 'DELETE'
                    });
                }
                if (vm.get(parameterDescriptionLocaleVmObject)) {
                    vm.set(parameterDescriptionLocaleVmObject, undefined);
                }
            });
        }
    }
});
