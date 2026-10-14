Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-signatures-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#setDefaultSignature': {
            click: 'onToggleDefaultBtnClick'
        },
        '#defaultSignature': {
            click: 'onToggleDefaultBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const record = view.getInitialConfig()._rowContext.record;
        vm.set('theSignature', record);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.openDetailWindow(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        this.openDetailWindow(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        const me = this;
        const view = me.getView();
        const vm = view.lookupViewModel();
        const store = view._rowContext.ownerGrid.getStore();
        CMDBuildUI.util.Utilities.showLoader(true, view);
        const callback = function (btnText) {
            if (btnText === 'yes') {
                CMDBuildUI.util.Ajax.setActionId('delete-signature');
                vm.get('theSignature').erase({
                    success: function (record, operation) {
                        CMDBuildUI.util.Utilities.showLoader(false, view);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        store.load();
                    },
                    failure: function () {
                        CMDBuildUI.util.Utilities.showLoader(false, view);
                        vm.get('theSignature').reject();
                    }
                });
            } else {
                CMDBuildUI.util.Utilities.showLoader(false, view);
            }
        };

        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleDefaultBtnClick: function (button, e, eOpts) {
        const me = this;
        const view = this.getView();
        const vm = view.getViewModel();
        const theSignature = vm.get('theSignature');
        const theSignatureId = theSignature.get('code');
        const ajaxconf = {
            url: Ext.String.format('{0}/system/config/_MANY', CMDBuildUI.util.Config.baseUrl),
            method: 'PUT',
            jsonData: {
                'org.cmdbuild.email.signatureDefault': theSignature.get('_default') ? null : theSignatureId
            },
            success: function (transport) {
                view.up('grid')
                    .getStore()
                    .load({
                        callback: function (data, operation) {
                            view.up('administration-content-emails-signatures-grid')
                                .getPlugin('administration-forminrowwidget')
                                .view.fireEventArgs('togglerow', [
                                    view.up('grid'),
                                    this.findRecord('_id', vm.get('theSignature').get('_id')),
                                    this.findExact('_id', vm.get('theSignature').get('_id'))
                                ]);
                        }
                    });
            }
        };

        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request(ajaxconf);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [
            true,
            null,
            CMDBuildUI.locales.Locales.administration.common.messages.saving
        ]);
        const me = this;
        const view = me.getView();
        const grid = view.up('administration-content-emails-signatures-grid');
        const vm = view.lookupViewModel();
        const theSignature = vm.get('theSignature');
        theSignature.set('active', !theSignature.get('active'));
        theSignature.save({
            callback: function (record) {
                CMDBuildUI.util.administration.Utilities.showToggleActiveMessage(record);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [
                    grid,
                    record,
                    this
                ]);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateDescriptionClick: function (button, event, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSignatureDescription(
                vm.get('theSignature.code')
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm,
            true
        );
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateSignatureContentClick: function (button, event, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSignatureContent(
            vm.get('theSignature.code')
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theSignatureContentTranslation',
            vm,
            true,
            'htmleditor'
        );
    },

    privates: {
        /**
         *
         * @param {String} mode
         */
        openDetailWindow: function (mode) {
            const vm = this.getViewModel();
            const record = this.getView().getInitialConfig()._rowContext.record;
            const container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.add({
                xtype: 'administration-content-emails-signatures-card-card',
                viewModel: {
                    data: {
                        theSignature: record,
                        actions: {
                            view: CMDBuildUI.util.administration.helper.FormHelper.formActions.view === mode,
                            edit: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit === mode,
                            add: CMDBuildUI.util.administration.helper.FormHelper.formActions.add === mode
                        }
                    }
                }
            });
        }
    }
});
