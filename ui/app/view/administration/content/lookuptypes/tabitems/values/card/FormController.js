Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-card',

    mixins: ['CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndAddBtn': {
            click: 'onSaveAndAddBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const typeView = Ext.ComponentQuery.query('viewport')[0].down('administration-content-lookuptypes-view');
        if (typeView) {
            vm.set('_is_system', vm.get('theLookupType._is_system'));
        }

        vm.bind(
            {
                bindTo: '{theValue}'
            },
            function (theValue) {
                if (theValue) {
                    view.isValid();
                }
            }
        );

        this.watchRequiredFields();
    },

    /**
     *
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        const file = input.fileInputEl.dom.files[0];
        const reader = new FileReader();
        const preview = input.up('fieldcontainer').down('#lookupIconPreview');

        reader.addEventListener(
            'load',
            function () {
                input.lookupViewModel().set('theValue.icon_image', reader.result);
                if (preview) {
                    preview.setSrc(reader.result);
                }
            },
            false
        );

        if (file) {
            reader.readAsDataURL(file);
        }
    },

    /**
     *
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        const vm = this.getViewModel();
        const theValue = vm.get('theValue');
        const translationCode = Ext.String.format(
            'lookup.{0}.{1}.description',
            theValue.get('_type'),
            theValue.get('code')
        );
        const popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
            'theTranslation',
            vm
        );
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        if (button.lookupViewModel().get('_is_system')) {
            this.getView().up().fireEvent('closed');
            return;
        }

        this.save(
            button.up('form'),
            function () {},
            function () {}
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndAddBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        const theLookupType = vm.get('theLookupType');
        const toolAction = vm.get('toolAction');
        const parentLookupsStore = vm.get('parentLookupsStore');
        const successCb = function () {
            const container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.add({
                xtype: 'administration-content-lookuptypes-tabitems-values-card',
                viewModel: {
                    links: {
                        theValue: {
                            type: 'CMDBuildUI.model.lookups.Lookup',
                            create: true
                        }
                    },
                    data: {
                        actions: {
                            edit: false,
                            add: true,
                            view: false
                        },
                        theLookupType: theLookupType,
                        toolAction: toolAction,
                        parentLookupsStore: parentLookupsStore
                    }
                }
            });
        };

        if (vm.get('_is_system')) {
            successCb();
            return;
        }

        this.save(button.up('form'), successCb, function () {});
    },

    /**
     *
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const theValue = this.getViewModel().get('theValue');
        const icon_font = theValue.get('icon_font');
        theValue.reject();
        theValue.set('icon_font', icon_font);
        this.getView().up().fireEvent('closed');
    },

    privates: {
        /**
         *
         */
        watchRequiredFields: function () {
            const view = this.getView();
            this.getViewModel().bind(
                {
                    bindTo: {
                        iconType: '{theValue.icon_type}',
                        iconImage: '{theValue.icon_image}'
                    }
                },
                function (data) {
                    const fieldhelper = CMDBuildUI.util.administration.helper.FieldsHelper;
                    const lookupValueIconFontInput = view.down('#lookupValueIconFont');
                    const lookupValueIconColorInput = view.down('#lookupValueIconColor');
                    const lookupValueImageInput = view.down('#lookupValueImage');
                    switch (data.iconType) {
                        case CMDBuildUI.model.lookups.Lookup.icontypes.none:
                            fieldhelper.setAllowBlank(lookupValueIconFontInput, true, view);
                            fieldhelper.setAllowBlank(lookupValueIconColorInput, true, view);
                            fieldhelper.setAllowBlank(lookupValueImageInput, true, view);
                            break;
                        case CMDBuildUI.model.lookups.Lookup.icontypes.image:
                            fieldhelper.setAllowBlank(lookupValueIconFontInput, true, view);
                            fieldhelper.setAllowBlank(lookupValueIconColorInput, true, view);
                            fieldhelper.setAllowBlank(lookupValueImageInput, !data.iconImage ? false : true, view);
                            break;
                        case CMDBuildUI.model.lookups.Lookup.icontypes.font:
                            fieldhelper.setAllowBlank(lookupValueIconFontInput, false, view);
                            fieldhelper.setAllowBlank(lookupValueIconColorInput, false, view);
                            fieldhelper.setAllowBlank(lookupValueImageInput, true, view);
                            break;
                    }
                    if (view.form) {
                        view.form.checkValidity();
                    }
                }
            );
        },

        /**
         *
         * @param {Ext.form.Panel} form
         * @param {Boolean} value
         */
        disableSaveButtons: function (form, value) {
            if (form && form.down('#saveAndAddBtn')) {
                form.down('#saveAndAddBtn').setDisabled(value);
            }
            if (form && form.down('#saveBtn')) {
                form.down('#saveBtn').setDisabled(value);
            }
        },

        /**
         *
         * @param {Ext.form.Panel} form
         * @param {Function} successCb
         * @param {Function} errorCb
         */
        save: function (form, successCb, errorCb) {
            const me = this;
            const vm = form.getViewModel();

            me.disableSaveButtons(form, true);

            if (form.isValid()) {
                const theValue = vm.get('theValue');
                if (theValue.get('icon_font')) {
                    theValue.set(
                        'icon_font',
                        CMDBuildUI.util.helper.IconHelper.setIconDBValue(theValue.get('icon_font'))
                    );
                }
                theValue.save({
                    success: function (record, operation) {
                        if (vm.get('actions.edit')) {
                            Ext.GlobalEvents.fireEventArgs('lookupvalueupdated', [record]);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            successCb();
                        } else {
                            if (vm.get('theTranslation')) {
                                vm.get('theTranslation').crudState = 'U';
                                vm.get('theTranslation').crudStateWas = 'U';
                                vm.get('theTranslation').phantom = false;
                                vm.get('theTranslation').set(
                                    '_id',
                                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLookupValueDescription(
                                        theValue.get('_type'),
                                        theValue.get('code')
                                    )
                                );
                                vm.get('theTranslation').save({
                                    success: function (_translation, _operation) {
                                        Ext.GlobalEvents.fireEventArgs('lookupvaluecreated', [record]);
                                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                    }
                                });
                            } else {
                                me.disableSaveButtons(form, false);
                                Ext.GlobalEvents.fireEventArgs('lookupvaluecreated', [record]);
                                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                successCb();
                            }
                        }
                    },
                    failure: function () {
                        me.disableSaveButtons(form, false);
                        errorCb();
                    }
                });
            } else {
                errorCb();
            }
        }
    }
});
