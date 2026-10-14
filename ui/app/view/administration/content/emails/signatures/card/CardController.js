Ext.define('CMDBuildUI.view.administration.content.emails.signatures.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-signatures-card-card',

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

    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.CreateController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind('{panelTitle}', function (title) {
            view.up('administration-detailswindow').getViewModel().set('title', title);
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this,
            vm = this.getViewModel(),
            form = this.getView();
        if (form.isValid()) {
            var theSignature = vm.get('theSignature');
            theSignature.save({
                success: function (record, operation) {
                    me.saveAllTranslations().then(function () {
                        Ext.GlobalEvents.fireEventArgs('signaturecreated', [record]);
                        var container =
                            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                        container.fireEvent('closed');
                    });
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var popup = this.getView().up('panel');
        popup.close();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateDescriptionClick: function (button, event, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode =
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
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSignatureContent(
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

    /**
     * exeute all promises for translation save
     */
    saveAllTranslations: function () {
        var deferred = new Ext.Deferred();
        var me = this;
        Ext.Promise.all([me.saveDescriptionTranslation(), me.saveSignatureContentTranslation()]).then(function () {
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     * exeute promise for description translation
     */
    saveDescriptionTranslation: function () {
        var deferred = new Ext.Deferred();
        var vm = this.getViewModel();
        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSignatureDescription(
            vm.get('theSignature.code')
        );
        // save the translation
        if (vm.get('theDescriptionTranslation')) {
            vm.get('theDescriptionTranslation').crudState = 'U';
            vm.get('theDescriptionTranslation').crudStateWas = 'U';
            vm.get('theDescriptionTranslation').phantom = false;
            vm.get('theDescriptionTranslation').set('_id', key);
            vm.get('theDescriptionTranslation').save({
                success: function (translations, operation) {
                    deferred.resolve();
                }
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    },

    /**
     * exeute promise for signature content translation
     */
    saveSignatureContentTranslation: function () {
        var deferred = new Ext.Deferred();
        var vm = this.getViewModel();
        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSignatureContent(
            vm.get('theSignature.code')
        );
        // save the translation
        if (vm.get('theSignatureContentTranslation')) {
            vm.get('theSignatureContentTranslation').crudState = 'U';
            vm.get('theSignatureContentTranslation').crudStateWas = 'U';
            vm.get('theSignatureContentTranslation').phantom = false;
            vm.get('theSignatureContentTranslation').set('_id', key);
            vm.get('theSignatureContentTranslation').save({
                success: function (translations, operation) {
                    deferred.resolve();
                }
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    }
});
