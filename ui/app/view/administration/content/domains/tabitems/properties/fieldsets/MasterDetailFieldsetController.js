Ext.define(
    'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldsetController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset',

        /**
         *
         * @param {Ext.data.Store} store
         * @param {Object} eOpts
         */
        onAllDetailAttributesDatachanged: function (store, eOpts) {
            const vm = this.getViewModel();
            const allAggregateAttrs = vm.get('theDomain.masterDetailAggregateAttrs');

            Ext.Array.forEach(allAggregateAttrs, function (id) {
                const record = store.getById(id);
                if (record) {
                    vm.getStore('masterDetailAggregateAttrsStore').add(record);
                }
            });
            vm.get('newSelectedAttributesStore').removeAll();
            vm.get('newSelectedAttributesStore').add(CMDBuildUI.model.Attribute.create());
        },

        /**
         * On translate button click
         * @param {Ext.button.Button} button
         * @param {Event} event
         * @param {Object} eOpts
         */
        onTranslateClickMasterDetail: function (event, button, eOpts) {
            const vm = this.getViewModel();
            const theDomain = vm.get('theDomain');
            const translationCode =
                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainMasterDetail(
                    !vm.get('actions.add') ? theDomain.get('name') : '.'
                );
            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                translationCode,
                vm.get('action'),
                'theMasterDetailTranslation',
                vm.getParent(),
                true
            );
        }
    }
);
