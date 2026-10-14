Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.CardController', {
    imports: [
        'CMDBuildUI.util.Utilities'
    ],
    mixins: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ToolsMixin'
    ],
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabitems-values-card',

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
     * @param {CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var typeView = Ext.ComponentQuery.query('viewport')[0].down('administration-content-dms-dmscategorytypes-view');
        vm.set('_is_system', typeView.lookupViewModel().get('theDMSCategoryType._is_system'));        
        vm.bind({
            bindTo: '{theValue}'
        }, function (theValue) {
            if (theValue) {
                view.isValid();
            }
        });        

    },

    /**
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        var vm = input.lookupViewModel();
        var file = input.fileInputEl.dom.files[0];
        var reader = new FileReader();
        var preview = input.up('fieldcontainer').down('#lookupValueImagePreview');
        reader.addEventListener("load", function () {
            vm.set('theValue.icon_image', reader.result);
            if (preview) {
                preview.setSrc(reader.result);
            }
        }, false);
        if (file) {
            reader.readAsDataURL(file);
        }
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theValue = vm.get('theValue');
        var translationCode = Ext.String.format('lookup.{0}.{1}.description', theValue.get('_type'), theValue.get('code'));
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.edit, 'theTranslation', vm);
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var successCb = function (record, operation) {

        };
        var errorCb = function () {

        };
        if (vm.get('_is_system')) {
            this.getView().up().fireEvent("closed");
            return;
        }
        me.save(button.up('form'), successCb, errorCb);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndAddBtnClick: function (button, e, eOpts) {

        var me = this,
            vm = me.getViewModel(),
            grid = vm.get('grid'),
            lookupTypeName = vm.get('lookupTypeName'),
            proxy = CMDBuildUI.model.dms.DMSCategory.getProxy(),
            viewModel = {
                links: {
                    theValue: {
                        type: 'CMDBuildUI.model.dms.DMSCategory',
                        create: true
                    }
                },
                data: {
                    actions: {
                        edit: false,
                        add: true,
                        view: false
                    },
                    lookupTypeName: lookupTypeName,
                    values: grid.getStore().getRange(),
                    title: Ext.String.format(
                        '{0} - {1}',
                        lookupTypeName,
                        CMDBuildUI.locales.Locales.administration.tasks.value
                    ),
                    grid: grid
                }
            };

        proxy.setUrl(CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(lookupTypeName));
        var successCb = function (record) {

            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

            container.removeAll();

            container.add({
                xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-card',
                viewModel: viewModel
            });
        };

        var errorCb = function () {

        };
        if (vm.get('_is_system')) {
            successCb();
            return;
        }
        me.save(button.up('form'), successCb, errorCb);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theValue").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },

    privates: {
        disableSaveButtons: function (form, value) {
            if (form && form.down('#saveAndAddBtn')) {
                form.down('#saveAndAddBtn').setDisabled(value);
            }
            if (form && form.down('#saveBtn')) {
                form.down('#saveBtn').setDisabled(value);
            }
        },
        save: function (form, successCb, errorCb) {
            var me = this;
            var vm = form.getViewModel();

            me.disableSaveButtons(form, true);

            if (form.isValid()) {
                var theValue = vm.get('theValue');
                if (!Ext.isEmpty(theValue.get('icon_image'))) {
                    theValue.set('icon_type', 'image');
                } else {
                    theValue.set('icon_type', 'none');
                }
                theValue.save({
                    success: function (record, operation) {
                        if (vm.get('actions.edit')) {
                            Ext.GlobalEvents.fireEventArgs("lookupvalueupdated", [record]);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            successCb(record, operation);
                        } else {
                            var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLookupValueDescription(theValue.get('_type'), theValue.get('code'));
                            if (vm.get(('theTranslation'))) {
                                vm.get('theTranslation').crudState = 'U';
                                vm.get('theTranslation').crudStateWas = 'U';
                                vm.get('theTranslation').phantom = false;
                                vm.get('theTranslation').set('_id', key);
                                vm.get('theTranslation').save({
                                    success: function (translation, operation) {
                                        Ext.GlobalEvents.fireEventArgs("lookupvaluecreated", [record]);
                                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                    }
                                });
                            } else {
                                me.disableSaveButtons(form, false);
                                Ext.GlobalEvents.fireEventArgs("lookupvaluecreated", [record]);
                                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                successCb(record, operation);
                            }
                        }

                    },
                    failure: function () {
                        me.disableSaveButtons(form, false);
                        errorCb(arguments);
                    }
                });
            } else {
                errorCb();
            }
        }
    }

});