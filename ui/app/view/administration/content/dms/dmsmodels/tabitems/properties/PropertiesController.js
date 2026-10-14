Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-models-tabitems-properties-properties',

    require: ['CMDBuildUI.util.administration.helper.FormHelper'],

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
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
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.classes.strings.deleteclass, // Delete class
            CMDBuildUI.locales.Locales.administration.classes.strings.deleteclassquest, // Are you sure you want to delete this class?
            function (action) {
                if (action === 'yes') {
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
                    var theModel = vm.get('theModel');
                    CMDBuildUI.util.Ajax.setActionId('delete-class');
                    theModel.erase({
                        failure: function (error) {
                            theModel.reject();
                            me.reloadClassesStoreAfterSave(theModel, button);
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(
                                theModel.get('_id')
                            );
                            me.redirectTo(nextUrl, true);
                        },
                        success: function (record, operation) {
                            me.reloadClassesStoreAfterSave(theModel, button);
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                'href',
                                Ext.util.History.getToken(),
                                nextUrl,
                                me
                            );
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                        }
                    });
                } else {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            },
            this
        );
    },

    /**
     * @param {Ext.menu.Item} menuItem
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPrintMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView().lookupViewModel().get('theModel').get('name');
        switch (menuItem.fileType) {
            case 'PDF':
                url = CMDBuildUI.util.administration.helper.ApiHelper.server.getDownloadSchemaUrl(
                    'PDF',
                    objectTypeName,
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel
                );
                break;
            case 'ODT':
                url = CMDBuildUI.util.administration.helper.ApiHelper.server.getDownloadSchemaUrl(
                    'ODT',
                    objectTypeName,
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel
                );
                break;
            default:
                CMDBuildUI.util.Msg.alert('Warning', 'File type of report not implemented!');
        }
        CMDBuildUI.util.File.download(url, menuItem.fileType.toLowerCase());
    },
    /**
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
        var me = this;
        var view = me.getView();
        var vm = view.lookupViewModel();
        var theModel = me.applyAssociatedData(vm, vm.get('theModel'));
        theModel.set('active', !theModel.get('active'));
        theModel.save({
            callback: function () {
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                Ext.getStore('dms.DMSModels').load();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this;
        button.setDisabled(true);
        var vm = this.getViewModel();
        if (vm.get('theModel').isValid()) {
            var theModel = me.applyAssociatedData(vm, vm.get('theModel'));

            // upload the icon
            me.uploadIcon(vm, theModel, button).then(
                function (theModel) {
                    // save the class
                    theModel.save({
                        success: function (record, operation) {
                            me.saveAllTranslations().then(function () {
                                me.reloadClassesStoreAfterSave(record, button);
                            });
                        },
                        failure: function () {
                            button.setDisabled(false);
                            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                        }
                    });
                },
                function (error) {
                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                    CMDBuildUI.util.Logger.log('Upload icon error...', CMDBuildUI.util.Logger.levels.error);
                    CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                }
            );
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var me = this,
            vm = me.getViewModel(),
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(
                vm.get('actions.edit') ? vm.get('theModel._id') : null
            );

        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    },

    /**
     * On translate button click (button, e, eOpts) {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateDescriptionClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassDescription(
            vm.get('theModel').get('name')
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
     * On translate button click
     * @param {Event} e
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateHelpClick: function (e, button, eOpts) {
        var vm = this.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassHelp(
            vm.get('theModel').get('name')
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theHelpTranslation',
            vm,
            true,
            'htmleditor'
        );
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.view.administration.content.classes.ViewModel} vm
         * @param {CMDBuildUI.model.classes.Class} theModel
         *
         * @return {CMDBuildUI.model.classes.Class}
         */
        applyAssociatedData: function (vm, theModel) {
            delete theModel.data.system;
            Ext.apply(theModel.data, theModel.getAssociatedData());
            theModel.data.formTriggers = [];
            theModel.data.attributeGroups = [];

            vm.get('formTriggersStore')
                .getData()
                .items.forEach(function (record, index) {
                    theModel.data.formTriggers.push(record.getData());
                });
            var parent = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(theModel.get('parent'));
            var hierarchy = parent.getHierarchy();
            hierarchy.push(theModel.get('name'));
            vm.get('attributeGroupsStore')
                .getData()
                .items.forEach(function (record, index) {
                    if (
                        !new RegExp(hierarchy.join('|')).test(record.get('name')) &&
                        record.get('name') !== record.get('_id')
                    ) {
                        record.set(
                            'name',
                            theModel.get('name') +
                                ' ' +
                                record.get('name').substring(record.get('name').indexOf(' ') + 1)
                        );
                    }
                    theModel.data.attributeGroups.push(record.getData());
                });

            theModel.data.widgets.forEach(function (record, index) {
                delete theModel.data.widgets[index].id;
                delete theModel.data.widgets[index]._id;
            });
            return theModel;
        },

        reloadClassesStoreAfterSave: function (record, button) {
            var me = this;
            var vm = this.getViewModel();
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(record.get('name'));
            if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                });
            } else {
                CMDBuildUI.util.Stores.loadDmsModelsStore().then(function () {
                    if (!vm.destroyed) {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                            'href',
                            nextUrl,
                            record.get('description'),
                            me
                        );
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                            'href',
                            nextUrl,
                            me
                        );
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                    }
                });
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm
         * @param {Function} successCb
         * @param {Function} errorCb
         */
        uploadIcon: function (vm, record, button) {
            var deferred = new Ext.Deferred();
            CMDBuildUI.util.Ajax.setActionId('class.icon.upload');
            // define method
            var method = 'POST';
            var input = this.lookupReference('iconFile').extractFileInput();

            if (!input.files.length) {
                deferred.resolve(record);
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format(
                    '{0}/uploads?overwrite_existing=true&path=images/classicons/{1}.png',
                    CMDBuildUI.util.Config.baseUrl,
                    vm.get('theModel.name')
                );
                // upload
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (response && response.data) {
                            record.set('_icon', response.data._id);
                            deferred.resolve(record);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                        }
                        deferred.reject(error);
                    }
                });
            }
            return deferred.promise;
        },

        /**
         * exeute all promises for translation save
         */
        saveAllTranslations: function () {
            var deferred = new Ext.Deferred();
            var me = this;
            Ext.Promise.all([
                me.saveDescriptionTranslation(),
                me.saveHelpTranslation(),
                me.saveFormWidgetsTranslations()
            ]).then(function () {
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
            var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassDescription(
                vm.get('theModel').get('name')
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
         * exeute promise for help translation
         */
        saveHelpTranslation: function () {
            var deferred = new Ext.Deferred();
            var vm = this.getViewModel();
            var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassHelp(
                vm.get('theModel').get('name')
            );
            // save the translation
            if (vm.get('theHelpTranslation')) {
                vm.get('theHelpTranslation').crudState = 'U';
                vm.get('theHelpTranslation').crudStateWas = 'U';
                vm.get('theHelpTranslation').phantom = false;
                vm.get('theHelpTranslation').set('_id', key);
                vm.get('theHelpTranslation').save({
                    success: function (translations, operation) {
                        deferred.resolve();
                    }
                });
            } else {
                deferred.resolve();
            }
            return deferred.promise;
        }
    },

    /**
     * exeute promise for form widgets translation
     */
    saveFormWidgetsTranslations: function () {
        var me = this;
        var deferred = new Ext.Deferred();
        var vm = me.getViewModel();
        var promises = [];
        vm.get('theModel')
            .widgets()
            .each(function (widget) {
                promises.push(me.saveFormWidgetTranslation(widget));
            });
        Ext.Promise.all(promises).then(function () {
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     *
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     */
    saveFormWidgetTranslation: function (widget) {
        var me = this;
        var vm = me.getViewModel();
        var deferred = new Ext.Deferred();
        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassFormWidgetItem(
            vm.get('theModel').get('name'),
            widget.get('WidgetId')
        );
        // save the translation
        var vmObject = vm.get('theFormWidgetTranslation_' + widget.get('WidgetId'));
        if (vmObject) {
            CMDBuildUI.util.Ajax.setActionId('class.formwidget_translation');
            vmObject.crudState = 'U';
            vmObject.crudStateWas = 'U';
            vmObject.phantom = false;
            vmObject.set('_id', key);
            vmObject.save({
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
