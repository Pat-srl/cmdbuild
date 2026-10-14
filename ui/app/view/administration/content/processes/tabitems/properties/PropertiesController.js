Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-properties-properties',

    require: ['CMDBuildUI.util.Utilities', 'CMDBuildUI.util.administration.helper.FormHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
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

    onBeforeRender: function (view) {
        var me = this;
        me.updateVersionBtn();
    },

    onEditBtnClick: function (button) {
        this.getView()
            .up('administration-content-processes-view')
            .getViewModel()
            .set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    onDeleteBtnClick: function (button) {
        var me = this,
            vm = this.getViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    CMDBuildUI.util.Utilities.showLoader(true);
                    var theProcess = vm.get('theProcess');
                    var tmpGetAssociated = theProcess.getAssociatedData;
                    theProcess.getAssociatedData = function () {
                        return false;
                    };

                    CMDBuildUI.util.Ajax.setActionId('delete-process');

                    theProcess.erase({
                        error: function () {
                            theProcess.getAssociatedData = tmpGetAssociated;
                            CMDBuildUI.util.Utilities.showLoader(false);
                        },
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl();
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
                            CMDBuildUI.util.Utilities.showLoader(false);
                        }
                    });
                }
            }
        );
    },
    onTemplateMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView()
                .up('#CMDBuildAdministrationContentProcessView')
                .getViewModel()
                .get('objectTypeName');

        url = CMDBuildUI.util.api.Processes.getTemplateFileUrl(objectTypeName);
        CMDBuildUI.util.File.download(url, 'xpdl');
    },

    onVersionMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView()
                .up('#CMDBuildAdministrationContentProcessView')
                .getViewModel()
                .get('objectTypeName');

        url = CMDBuildUI.util.api.Processes.getVersionFileUrl(objectTypeName, menuItem.planId);
        CMDBuildUI.util.File.download(url, 'xpdl');
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
        var view = this.getView();
        var vm = view.getViewModel();
        var theProcess = vm.get('theProcess');
        Ext.apply(theProcess.data, theProcess.getAssociatedData());
        theProcess.set('active', !theProcess.get('active'));
        theProcess.save({
            callback: function () {
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
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
        if (vm.get('theProcess').isValid()) {
            var theProcess = vm.get('theProcess');
            if (theProcess.get('icon_font')) {
                theProcess.set(
                    'icon_font',
                    CMDBuildUI.util.helper.IconHelper.setIconDBValue(theProcess.get('icon_font'))
                );
            }
            delete theProcess.data.system;
            Ext.apply(theProcess.data, theProcess.getAssociatedData());
            theProcess.data.formTriggers = [];
            theProcess.data.attributeGroups = [];
            if (theProcess.data.contextMenuItems) {
                CMDBuildUI.util.Logger.log('contextMenuItems store exist', CMDBuildUI.util.Logger.levels.debug);
                theProcess.data.contextMenuItems.forEach(function (record, index) {
                    delete theProcess.data.contextMenuItems[index].id;
                    delete theProcess.data.contextMenuItems[index]._id;
                });
            }
            if (vm.get('formTriggersStore')) {
                CMDBuildUI.util.Logger.log('formTriggersStore store exist', CMDBuildUI.util.Logger.levels.debug);
                vm.get('formTriggersStore')
                    .getData()
                    .items.forEach(function (record, index) {
                        theProcess.data.formTriggers.push(record.getData());
                    });
            }
            if (vm.get('attributeGroupsStore')) {
                CMDBuildUI.util.Logger.log('attributeGroupsStore store exist', CMDBuildUI.util.Logger.levels.debug);
                var parent = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(theProcess.get('parent'));
                var hierarchy = parent.getHierarchy();
                hierarchy.push(theProcess.get('name'));
                vm.get('attributeGroupsStore')
                    .getData()
                    .items.forEach(function (record, index) {
                        if (
                            !new RegExp(hierarchy.join('|')).test(record.get('name')) &&
                            record.get('name') !== record.get('_id')
                        ) {
                            record.set(
                                'name',
                                theProcess.get('name') +
                                    ' ' +
                                    record.get('name').substring(record.get('name').indexOf(' ') + 1)
                            );
                        }
                        theProcess.data.attributeGroups.push(record.getData());
                    });
            }
            me.getView().down('#customRoutesFieldset').parseAndeSetObjectRoutes(theProcess);
            theProcess.save({
                success: function (record, operation) {
                    me.uploadXpdlAndIcon(
                        vm,
                        function () {
                            me.saveAllTranslations().then(function () {
                                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                                me.reloadProcessStoreAfterSave(record);
                                button.setDisabled(false);
                                vm.getView()
                                    .up('administration-content-processes-view')
                                    .getViewModel()
                                    .set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            });
                        },
                        function () {
                            me.saveAllTranslations().then(function () {
                                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                                me.reloadProcessStoreAfterSave(record);
                                button.setDisabled(false);
                                vm.getView()
                                    .up('administration-content-processes-view')
                                    .getViewModel()
                                    .set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            });
                        }
                    );
                },
                failure: function (reason) {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
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
        if (this.getViewModel().get('actions.edit')) {
            this.redirectTo(
                Ext.String.format('administration/processes/{0}', this.getViewModel().get('theProcess._id')),
                true
            );
        } else if (this.getViewModel().get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode('objecttype', CMDBuildUI.model.administration.MenuItem.types.process);
            vm.set('selected', currentNode);
            this.redirectTo('administration/processes_empty', true);
        }
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
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfProcessDescription(
            vm.get('theProcess').get('name')
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
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfProcessHelp(
            vm.get('theProcess').get('name')
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

    /**
     * @param {Ext.menu.Item} menuItem
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPrintMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView().lookupViewModel().get('theProcess').get('name');
        switch (menuItem.fileType) {
            case 'PDF':
                url = CMDBuildUI.util.administration.helper.ApiHelper.server.getDownloadSchemaUrl(
                    'PDF',
                    objectTypeName,
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                );
                break;
            case 'ODT':
                url = CMDBuildUI.util.administration.helper.ApiHelper.server.getDownloadSchemaUrl(
                    'ODT',
                    objectTypeName,
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                );
                break;
            default:
                CMDBuildUI.util.Msg.alert('Warning', 'File type of report not implemented!');
        }
        CMDBuildUI.util.File.download(url, menuItem.fileType.toLowerCase());
    },
    privates: {
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm
         * @param {Function} successCb
         * @param {Function} errorCb
         */
        uploadXpdlAndIcon: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('process.xpdl.upload');
            // define method
            var me = this,
                method = 'POST',
                input = me.lookupReference('xpdlFile').extractFileInput();
            if (!input.files.length) {
                me.uploadIcon(vm, successCb, errorCb);
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format(
                    '{0}/processes/{1}/versions',
                    CMDBuildUI.util.Config.baseUrl,
                    vm.get('theProcess._id')
                );
                // upload
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        me.updateVersionBtn();
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [response]);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                        }
                        CMDBuildUI.util.Notifier.showErrorMessage();
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
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
        uploadIcon: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('process.icon.upload');
            // define method
            var method = 'POST';
            var input = this.lookupReference('iconFile').extractFileInput();

            if (!input.files.length) {
                if (Ext.isFunction(successCb)) {
                    Ext.callback(successCb, null, []);
                }
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format(
                    '{0}/uploads?overwrite_existing=true&path=images/classicons/{1}.png',
                    CMDBuildUI.util.Config.baseUrl,
                    vm.get('theProcess._id')
                );
                // upload
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (response && response.data) {
                            vm.get('theProcess').set('_icon', response.data._id);
                            Ext.apply(vm.get('theProcess').data, vm.get('theProcess').getAssociatedData());

                            vm.get('theProcess').save({
                                success: function (record, operation) {
                                    if (Ext.isFunction(successCb)) {
                                        Ext.callback(successCb, null, [response]);
                                    }
                                }
                            });
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                        }
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            }
        },
        reloadProcessStoreAfterSave: function (record) {
            var me = this;
            var vm = this.getViewModel();
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(record.get('name'));
            if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                });
            } else {
                CMDBuildUI.util.Stores.loadProcessesStore().then(function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                        'href',
                        nextUrl,
                        record.get('description'),
                        me
                    );
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
                });
            }
        },

        updateVersionBtn: function () {
            var view = this.getView();
            var vm = view.up('#CMDBuildAdministrationContentProcessView').getViewModel();
            var versionBtn = view.down('#versionBtn');
            versionBtn.getMenu().removeAll();
            var objectTypeName = vm.get('objectTypeName');

            if (objectTypeName && versionBtn) {
                Ext.create('Ext.data.Store', {
                    model: 'CMDBuildUI.model.processes.ProcessVersion',
                    proxy: {
                        type: 'baseproxy',
                        url: '/processes/' + objectTypeName + '/versions',
                        reader: {
                            type: 'json'
                        }
                    },
                    autoLoad: true,
                    autoDestroy: true,
                    sorters: [
                        {
                            // Sort by version, in descending order
                            sorterFn: function (record1, record2) {
                                var version1 = record1.get('version'),
                                    version2 = record2.get('version');
                                return version1 > version2 ? 1 : version1 === version2 ? 0 : -1;
                            },
                            direction: 'DESC'
                        }
                    ],
                    listeners: {
                        load: function (store, records, success, operation, opts) {
                            var items = [];
                            if (records && records.length) {
                                Ext.Array.forEach(records, function (record) {
                                    var item = {
                                        text: record.get('version'),
                                        planId: record.get('planId'),
                                        listeners: {
                                            click: 'onVersionMenuItemClick'
                                        },
                                        cls: 'menu-item-nospace',
                                        autoEl: {
                                            'data-testid':
                                                'administration-processes-properties-tool-version-' +
                                                record.get('version')
                                        }
                                    };

                                    items.push(item);
                                    if (!versionBtn.destroyed) {
                                        versionBtn.menu.add(item);
                                    }
                                });
                            }

                            var template = {
                                text: CMDBuildUI.locales.Locales.administration.processes.strings.template,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.processes.strings.template'
                                },
                                planId: 'template',
                                listeners: {
                                    click: 'onTemplateMenuItemClick'
                                },
                                cls: 'menu-item-nospace',
                                autoEl: {
                                    'data-testid': 'administration-processes-properties-tool-version-template'
                                }
                            };

                            if (versionBtn.menu) {
                                versionBtn.menu.add(template);
                            }
                        }
                    }
                });
            }
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
                me.saveContextMenuTranslations(),
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
                vm.get('theProcess').get('name')
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
                vm.get('theProcess').get('name')
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
    saveContextMenuTranslations: function () {
        var me = this;
        var deferred = new Ext.Deferred();
        var vm = me.getViewModel();
        var promises = [];
        vm.get('theProcess')
            .contextMenuItems()
            .each(function (conetxtMenu) {
                promises.push(me.saveContextMenuTranslation(conetxtMenu));
            });
        Ext.Promise.all(promises).then(function () {
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     *
     * @param {CMDBuildUI.model.ContextMenuItem} conetxtMenu
     */
    saveContextMenuTranslation: function (contextMenu) {
        var me = this;
        var vm = me.getViewModel();
        var deferred = new Ext.Deferred();
        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassContextMenuItem(
            vm.get('theProcess').get('name'),
            contextMenu.get('label')
        );
        // save the translation
        var vmObject = vm.get(
            'theContextMenuTranslation_' + CMDBuildUI.util.Utilities.stringToHex(contextMenu.get('label'))
        );
        if (vmObject) {
            CMDBuildUI.util.Ajax.setActionId('process.contextmenu_translation');
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
    },

    /**
     * exeute promise for form widgets translation
     */
    saveFormWidgetsTranslations: function () {
        const me = this;
        const promises = [];
        const vm = me.getViewModel();
        const deferred = new Ext.Deferred();

        vm.get('theProcess')
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
        const me = this;
        const vm = me.getViewModel();
        const deferred = new Ext.Deferred();
        const widgetId = widget.get('WidgetId');
        const key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassFormWidgetItem(
            vm.get('theProcess').get('name'),
            widgetId
        );

        // save the translation
        const vmObject = vm.get('theFormWidgetTranslation_' + widgetId);
        if (vmObject) {
            CMDBuildUI.util.Ajax.setActionId('process.formwidget_translation');
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
