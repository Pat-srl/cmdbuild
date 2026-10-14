Ext.define(
    'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

        control: {
            '#': {
                afterrender: 'onAfterRender',
                beforerender: 'onBeforeRender'
            }
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset} view
         * @param {Object} eOpts
         */
        onBeforeRender: function (view, eOpts) {
            const vm = this.getViewModel();
            // disable or enable cascade actions ask confirm fields
            vm.bind(
                {
                    bindTo: {
                        cascadeActionDirect: '{theDomain.cascadeActionDirect}',
                        cascadeActionInverse: '{theDomain.cascadeActionInverse}'
                    },
                    deep: true
                },
                function (data) {
                    vm.set(
                        'cascadeActionDirect_askConfirm_disabled',
                        Ext.isEmpty(data.cascadeActionDirect) ||
                            data.cascadeActionDirect === CMDBuildUI.model.domains.Domain.cascadeAction.restrict
                    );
                    vm.set(
                        'cascadeActionInverse_askConfirm_disabled',
                        Ext.isEmpty(data.cascadeActionInverse) ||
                            data.cascadeActionInverse === CMDBuildUI.model.domains.Domain.cascadeAction.restrict
                    );
                }
            );
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset} view
         * @param {Object} eOpts
         */
        onAfterRender: function (view, eOpts) {
            if (this.getViewModel().get('actions.add')) {
                view.down('#domainname').maxLength = 20;
            }
        },

        /**
         * On translate button click
         * @param {Ext.button.Button} button
         * @param {Event} event
         * @param {Object} eOpts
         */
        onTranslateClickDescription: function (event, button, eOpts) {
            const vm = this.getViewModel();
            const theDomain = vm.get('theDomain');
            const translationCode =
                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDescription(
                    !vm.get('actions.add') ? theDomain.get('name') : '.'
                );
            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                translationCode,
                vm.get('action'),
                'theDomainDescriptionTranslation',
                vm.getParent(),
                true
            );
        },

        /**
         * On translate button click
         * @param {Ext.button.Button} button
         * @param {Event} event
         * @param {Object} eOpts
         */
        onTranslateClickDirect: function (event, button, eOpts) {
            const vm = this.getViewModel();
            const theDomain = vm.get('theDomain');
            const translationCode =
                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDirectDescription(
                    !vm.get('actions.add') ? theDomain.get('name') : '.'
                );
            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                translationCode,
                vm.get('action'),
                'theDirectDescriptionTranslation',
                vm.getParent(),
                true
            );
        },

        /**
         * On translate button click
         * @param {Ext.button.Button} button
         * @param {Event} event
         * @param {Object} eOpts
         */
        onTranslateClickInverse: function (event, button, eOpts) {
            const vm = this.getViewModel();
            const theDomain = vm.get('theDomain');
            const translationCode =
                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainInverseDescription(
                    !vm.get('actions.add') ? theDomain.get('name') : '.'
                );
            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
                translationCode,
                vm.get('action'),
                'theInverseDescriptionTranslation',
                vm.getParent(),
                true
            );
        },

        /**
         *
         * @param {Ext.form.field.Field} combo
         * @param {Object} newValue
         * @param {Object} oldValue
         * @param {Object} eOpts
         */
        onSourceChange: function (combo, newValue, oldValue, eOpts) {
            const vm = combo.lookupViewModel();
            this.resetSummaryGrid(combo, newValue, oldValue);
            const isSourceProcess = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue) ? true : false;
            vm.set('theDomain.sourceProcess', isSourceProcess);
        },

        /**
         *
         * @param {Ext.form.field.Field} combo
         * @param {Object} newValue
         * @param {Object} oldValue
         * @param {Object} eOpts
         */
        onDestinationChange: function (combo, newValue, oldValue, eOpts) {
            const vm = combo.lookupViewModel();
            this.resetSummaryGrid(combo, newValue, oldValue);
            const isSourceProcess = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue) ? true : false;
            vm.set('theDomain.destinationProcess', isSourceProcess);
        },

        /**
         *
         * @param {Ext.form.field.Field} combo
         * @param {Object} newValue
         * @param {Object} oldValue
         * @param {Object} eOpts
         */
        resetSummaryGrid: function (combo, newValue, oldValue, eOpts) {
            if (oldValue) {
                const masterDetailAggregateAttrsGrid = combo.up('form').down('#sumattributesGrid');
                masterDetailAggregateAttrsGrid.getStore().removeAll();
            }
        }
    }
);
